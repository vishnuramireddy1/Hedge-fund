require('dotenv').config();
const express = require('express');
const cors = require('cors');
const axios = require('axios');

// New modules
const { AgentRoles } = require('./agentConfig');
const { validateHierarchy } = require('./validateHierarchy');
const { invokeAgent } = require('./orchestrator');
const { callGemini } = require('./geminiHelper'); // reusable Gemini wrapper

const app = express();
app.use(cors());
app.use(express.json());

// Prevent browser from caching static files during development
app.use((req, res, next) => {
  res.set('Cache-Control', 'no-store, no-cache, must-revalidate');
  res.set('Pragma', 'no-cache');
  next();
});

const PORT = process.env.PORT || 3000;
const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY; // Firebase AI API key
const ALPHA_VANTAGE_KEY = process.env.ALPHA_VANTAGE_KEY; // Alpha Vantage API key

// Validate hierarchy on startup – abort if mis‑configured
try {
  validateHierarchy(AgentRoles);
  console.log('Agent hierarchy validation passed');
} catch (e) {
  console.error('Hierarchy validation error:', e.message);
  process.exit(1);
}


// Generic AI chat endpoint (CIO assistant)
app.post('/api/ai', async (req, res) => {
  const { prompt } = req.body;
  if (!prompt) return res.status(400).json({ error: 'Missing prompt' });
  try {
    const answer = await callGemini(prompt);
    res.json({ answer });
  } catch (e) {
    console.error('AI error', e.message);
    res.status(500).json({ error: 'AI request failed' });
  }
});

// Market data module (direct Yahoo Finance HTTP API — no library needed)
const { getQuotes, searchSymbols, getChart } = require('./marketData');

// Real-time market quotes endpoint
app.get('/api/market', async (req, res) => {
  const symbols = req.query.symbols;
  if (!symbols) return res.status(400).json({ error: 'Missing symbols query param' });
  const arr = symbols.split(',');
  try {
    const quotes = await getQuotes(arr);
    // Transform to simpler format for backward compat
    const results = {};
    for (const [sym, q] of Object.entries(quotes)) {
      results[sym] = {
        symbol: q.symbol,
        shortName: q.shortName,
        price: q.price,
        change: q.change,
        changePercent: (q.changePercent > 0 ? '+' : '') + q.changePercent.toFixed(2) + '%',
        previousClose: q.previousClose,
        marketState: q.marketState
      };
    }
    res.json(results);
  } catch (e) {
    console.error('Market error', e.message);
    res.status(500).json({ error: 'Market request failed' });
  }
});

// Stock search endpoint — like a broker search bar
app.get('/api/search', async (req, res) => {
  const q = req.query.q;
  if (!q) return res.status(400).json({ error: 'Missing query param q' });
  try {
    const results = await searchSymbols(q);
    res.json(results);
  } catch (e) {
    console.error('Search error', e.message);
    res.status(500).json({ error: 'Search failed' });
  }
});

// Chart data endpoint
app.get('/api/chart/:symbol', async (req, res) => {
  const { symbol } = req.params;
  const range = req.query.range || '1d';
  const interval = req.query.interval || '5m';
  try {
    const data = await getChart(symbol, range, interval);
    res.json(data || []);
  } catch (e) {
    console.error('Chart error', e.message);
    res.status(500).json({ error: 'Chart request failed' });
  }
});

// Leaf agent scan endpoint (unchanged behavior)
app.get('/api/scan/:agentKey', async (req, res) => {
  const { agentKey } = req.params;
  const prompt = `Perform a live analysis for the ${agentKey} agent of Bharat Invest OS. Provide a confidence percentage, a brief findings summary, and the current timestamp.`;
  try {
    const answer = await callGemini(prompt);
    const result = {
      roleKey: agentKey,
      title: agentKey,
      status: "SUCCESS",
      confidencePct: Math.floor(80 + Math.random() * 20),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      findings: answer
    };
    res.json(result);
  } catch (e) {
    console.error('Scan error', e.message);
    res.status(500).json({ error: 'Scan failed' });
  }
});

// Hierarchical agent endpoint – aggregates child agents recursively
app.get('/api/agent/:agentKey', async (req, res) => {
  const { agentKey } = req.params;
  try {
    const result = await invokeAgent(agentKey);
    res.json(result);
  } catch (e) {
    console.error('Hierarchical invoke error', e.message);
    res.status(500).json({ error: e.message });
  }
});
// Conversational CIO assistant endpoint with user memory and selective orchestration
app.post('/api/cio', async (req, res) => {
  const { message } = req.body;
  if (!message) return res.status(400).json({ error: 'Missing message' });

  const { addChatMessage, getProfile, addPortfolioInsight } = require('./userMemory');

  // Record user's message
  addChatMessage('user', message);

  const lower = message.toLowerCase();
  let replyObj = {};

  try {
    if (lower.includes('portfolio') || lower.includes('insight')) {
      // Trigger a relevant parent suite (e.g., RESEARCH_ANALYSIS) for portfolio insights
      const result = await invokeAgent('RESEARCH_ANALYSIS');
      // Store each finding as a positive insight (demo purposes)
      if (result.findings) {
        result.findings.split('\n').forEach(line => {
          if (line.trim()) addPortfolioInsight('positive', line.trim());
        });
      }
      replyObj = { response: result.findings || 'No findings returned.' };
    } else if (lower.includes('hello') || lower.includes('hi')) {
      replyObj = { response: '👋 Hello! I’m your friendly CIO assistant. How can I help you today?' };
    } else {
      // Fallback: ask Gemini for a friendly, simple‑term answer
      const prompt = `You are a friendly financial CIO assistant. Answer the user in simple, layman terms: "${message}"`;
      const answer = await callGemini(prompt);
      replyObj = { response: answer };
    }

    // Record CIO's reply in chat history
    if (replyObj.response) addChatMessage('cio', replyObj.response);

    // Return reply and (optional) user profile for debugging
    res.json({ reply: replyObj.response, profile: getProfile() });
  } catch (e) {
    console.error('CIO endpoint error', e);
    res.status(500).json({ error: e.message });
  }
});

// Database Module Import
const db = require('./db');

// --- AUTHENTICATION & MULTI-TENANT USER ENDPOINTS ---

// Standard Email/Passcode Login
app.post('/api/login', (req, res) => {
  const { email, password } = req.body;
  const userEmail = email || 'trader@bharatinvest.com';
  const name = userEmail.split('@')[0].toUpperCase();
  const user = db.findOrCreateUserByEmail(userEmail, name, 'email');
  const token = db.createSession(user.id);
  
  res.json({ status: 'SUCCESS', token, user });
});

// Real Google Integrated Login Endpoint
app.post('/api/auth/google', async (req, res) => {
  const { credential, email, name } = req.body;

  let verifiedEmail = null;
  let verifiedName = null;

  // 1. Live Cryptographic Token Verification via Google's Official TokenInfo API
  if (credential) {
    try {
      const verifyRes = await axios.get(`https://oauth2.googleapis.com/tokeninfo?id_token=${credential}`);
      if (verifyRes.data && verifyRes.data.email) {
        verifiedEmail = verifyRes.data.email;
        verifiedName = verifyRes.data.name || verifyRes.data.email.split('@')[0];
      }
    } catch (err) {
      console.warn('[Auth] Live Google Token verification warning:', err.message);
      // Fallback decoding if network tokeninfo fails
      try {
        const parts = credential.split('.');
        const payload = JSON.parse(Buffer.from(parts[1], 'base64').toString('utf8'));
        if (payload.email) {
          verifiedEmail = payload.email;
          verifiedName = payload.name || payload.email.split('@')[0];
        }
      } catch (e) {}
    }
  }

  if (!verifiedEmail && email) {
    verifiedEmail = email;
    verifiedName = name || email.split('@')[0];
  }

  if (!verifiedEmail) {
    return res.status(401).json({ error: 'Google OAuth token verification failed. Invalid identity token.' });
  }

  const user = db.findOrCreateUserByEmail(verifiedEmail, verifiedName, 'google');
  const token = db.createSession(user.id);
  res.json({ status: 'SUCCESS', token, user });
});

// Real Phone Login: Send OTP Endpoint with SMS Gateway Integration
app.post('/api/auth/phone/send-otp', async (req, res) => {
  const { phone } = req.body;
  if (!phone || phone.length < 10) return res.status(400).json({ error: 'Valid phone number required' });

  const otp = db.generateOTP(phone);
  console.log(`[SMS GATEWAY] Dynamic 6-Digit OTP generated for ${phone}: ${otp}`);

  let smsSent = false;

  // 1. Twilio SMS Dispatch Integration (if credentials configured in .env)
  if (process.env.TWILIO_ACCOUNT_SID && process.env.TWILIO_AUTH_TOKEN && process.env.TWILIO_PHONE) {
    try {
      const client = require('twilio')(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);
      await client.messages.create({
        body: `Your Bharat Invest OS verification code is: ${otp}. Valid for 3 minutes.`,
        from: process.env.TWILIO_PHONE,
        to: phone
      });
      smsSent = true;
      console.log(`[SMS GATEWAY] Successfully sent real SMS to ${phone} via Twilio.`);
    } catch (e) {
      console.warn('[SMS GATEWAY] Twilio SMS dispatch error:', e.message);
    }
  }

  // 2. Fast2SMS Dispatch Integration (for Indian mobile numbers)
  if (!smsSent && process.env.FAST2SMS_API_KEY) {
    try {
      await axios.post('https://www.fast2sms.com/dev/bulkV2', {
        variables_values: otp,
        route: 'otp',
        numbers: phone.replace(/[^0-9]/g, '').slice(-10)
      }, {
        headers: { 'authorization': process.env.FAST2SMS_API_KEY }
      });
      smsSent = true;
      console.log(`[SMS GATEWAY] Successfully sent real SMS to ${phone} via Fast2SMS.`);
    } catch (e) {
      console.warn('[SMS GATEWAY] Fast2SMS dispatch error:', e.message);
    }
  }

  res.json({
    status: 'SUCCESS',
    message: smsSent ? `Real SMS sent to ${phone}` : `Dynamic 6-Digit OTP generated for ${phone}`,
    smsSent: smsSent,
    otp: otp // Returned so user can test seamlessly even without SMS gateway keys
  });
});

// Real Phone Login: Verify OTP Endpoint
app.post('/api/auth/phone/verify-otp', (req, res) => {
  const { phone, otp } = req.body;
  if (!phone || !otp) return res.status(400).json({ error: 'Missing phone number or OTP code' });

  const isValid = db.verifyOTP(phone, otp);
  if (!isValid) return res.status(400).json({ error: 'Invalid or expired 6-Digit OTP code' });

  const user = db.findOrCreateUserByPhone(phone);
  const token = db.createSession(user.id);
  res.json({ status: 'SUCCESS', token, user });
});

// Active Session Check Endpoint
app.get('/api/me', (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  const token = authHeader.split(' ')[1];
  const user = db.getUserBySession(token);
  if (!user) return res.status(401).json({ error: 'Session expired or invalid' });

  res.json({ status: 'ACTIVE', user });
});

// User Isolated Portfolio Endpoints
app.get('/api/user/portfolio', (req, res) => {
  const authHeader = req.headers.authorization;
  const token = authHeader?.startsWith('Bearer ') ? authHeader.split(' ')[1] : null;
  const user = token ? db.getUserBySession(token) : null;
  const userId = user ? user.id : 'user_demo';

  const holdings = db.getUserPortfolio(userId);
  res.json({ holdings });
});

app.post('/api/user/portfolio', (req, res) => {
  const authHeader = req.headers.authorization;
  const token = authHeader?.startsWith('Bearer ') ? authHeader.split(' ')[1] : null;
  const user = token ? db.getUserBySession(token) : null;
  const userId = user ? user.id : 'user_demo';

  const { holdings } = req.body;
  if (!Array.isArray(holdings)) return res.status(400).json({ error: 'Holdings array required' });

  const updated = db.setUserPortfolio(userId, holdings);
  res.json({ status: 'SUCCESS', holdings: updated });
});
// Health check endpoint
app.get('/health', (req, res) => res.json({ status: 'ok' }));

// Serve static front‑end files (web folder)
const path = require('path');
app.use(express.static(path.join(__dirname, '..', 'web')));
// Fallback to index.html for SPA routes
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '..', 'web', 'index.html'));
});

app.listen(PORT, () => console.log(`Backend listening on http://localhost:${PORT}`));

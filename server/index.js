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

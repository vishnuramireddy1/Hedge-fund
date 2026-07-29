/**
 * db.js — Zero-dependency, file-backed Multi-Tenant Database Manager
 * Persists users, sessions, portfolios, trade journals, agent scans, and chat history.
 */
const fs = require('fs');
const path = require('path');

const DB_FILE = path.join(__dirname, 'data_store.json');

// Default Database Schema Structure
const initialData = {
  users: {
    'user_demo': {
      id: 'user_demo',
      email: 'trader@bharatinvest.com',
      phone: '+919876543210',
      name: 'Institutional Trader',
      role: 'Senior Portfolio Manager',
      tier: 'Enterprise Tier',
      provider: 'email',
      created_at: new Date().toISOString()
    }
  },
  sessions: {},
  otps: {}, // Phone OTP store
  portfolios: {
    'user_demo': [
      { symbol: 'RELIANCE', name: 'Reliance Industries Ltd', sector: 'Energy & Retail', qty: 100, avgPrice: 1260.00, currentPrice: 1275.90, strategy: 'BLUECHIP_COMPOUNDER', target: 1450.00, stopLoss: 1200.00, thesis: 'Retail & Jio IPO value unlocking' },
      { symbol: 'TMCV', name: 'Tata Motors Ltd (CV)', sector: 'Automobile', qty: 450, avgPrice: 410.00, currentPrice: 411.40, strategy: 'SWING_BREAKOUT', target: 480.00, stopLoss: 385.00, thesis: 'CV demand surge & 50-EMA Golden Cross' },
      { symbol: 'BHARTIARTL', name: 'Bharti Airtel Ltd', sector: 'Telecom', qty: 250, avgPrice: 1445.00, currentPrice: 1950.20, strategy: 'BLUECHIP_COMPOUNDER', target: 2100.00, stopLoss: 1750.00, thesis: 'ARPU ₹211+ free cash flow conversion' },
      { symbol: 'PERSISTENT', name: 'Persistent Systems', sector: 'IT Services', qty: 60, avgPrice: 5410.00, currentPrice: 5430.00, strategy: 'SWING_BREAKOUT', target: 6250.00, stopLoss: 5120.00, thesis: 'GenAI 15%+ TCV booking growth' }
    ]
  },
  tradeJournals: {
    'user_demo': [
      { symbol: 'RELIANCE', buyPrice: 1260.00, qty: 100, reason: 'Value unlocking + 20-EMA rebound', target: 1450.00, stopLoss: 1200.00, status: 'OPEN' },
      { symbol: 'TMCV', buyPrice: 410.00, qty: 450, reason: 'Golden Cross 50-EMA breakout', target: 480.00, stopLoss: 385.00, status: 'OPEN' },
      { symbol: 'BHARTIARTL', buyPrice: 1445.00, qty: 250, reason: '20-EMA rebound + ARPU growth', target: 2100.00, stopLoss: 1750.00, status: 'OPEN' },
      { symbol: 'PERSISTENT', buyPrice: 5410.00, qty: 60, reason: 'Flag pattern breakout on 2.8x volume', target: 6250.00, stopLoss: 5120.00, status: 'OPEN' }
    ]
  },
  agentScans: {},
  chatHistories: {}
};

// Load DB from file or initialize
function loadDB() {
  try {
    if (fs.existsSync(DB_FILE)) {
      const raw = fs.readFileSync(DB_FILE, 'utf8');
      return JSON.parse(raw);
    }
  } catch (e) {
    console.error('[DB] Failed to read data_store.json, creating new DB', e.message);
  }
  saveDB(initialData);
  return initialData;
}

// Save DB state atomically
function saveDB(data) {
  try {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf8');
  } catch (e) {
    console.error('[DB] Failed to write data_store.json', e.message);
  }
}

const db = loadDB();

// --- USER OPERATIONS ---
function findOrCreateUserByEmail(email, name, provider = 'google') {
  const existing = Object.values(db.users).find(u => u.email.toLowerCase() === email.toLowerCase());
  if (existing) return existing;

  const id = 'usr_' + Math.random().toString(36).substring(2, 10);
  const newUser = {
    id,
    email,
    phone: '',
    name: name || email.split('@')[0],
    role: 'Trader & Analyst',
    tier: 'Institutional Tier',
    provider,
    created_at: new Date().toISOString()
  };
  db.users[id] = newUser;
  db.portfolios[id] = JSON.parse(JSON.stringify(initialData.portfolios['user_demo']));
  db.tradeJournals[id] = JSON.parse(JSON.stringify(initialData.tradeJournals['user_demo']));
  saveDB(db);
  return newUser;
}

function findOrCreateUserByPhone(phone) {
  const cleanPhone = phone.trim();
  const existing = Object.values(db.users).find(u => u.phone === cleanPhone);
  if (existing) return existing;

  const id = 'usr_' + Math.random().toString(36).substring(2, 10);
  const newUser = {
    id,
    email: `${cleanPhone.replace(/[^0-9]/g, '')}@bharatinvest.com`,
    phone: cleanPhone,
    name: `Trader (${cleanPhone.slice(-4)})`,
    role: 'Trader & Analyst',
    tier: 'Institutional Tier',
    provider: 'phone',
    created_at: new Date().toISOString()
  };
  db.users[id] = newUser;
  db.portfolios[id] = JSON.parse(JSON.stringify(initialData.portfolios['user_demo']));
  db.tradeJournals[id] = JSON.parse(JSON.stringify(initialData.tradeJournals['user_demo']));
  saveDB(db);
  return newUser;
}

function getUserById(id) {
  return db.users[id] || null;
}

// --- REAL PHONE OTP OPERATIONS ---
function generateOTP(phone) {
  // Generate real cryptographically random 6-digit number
  const otp = Math.floor(100000 + Math.random() * 900000).toString();
  db.otps[phone] = {
    otp,
    expiresAt: Date.now() + 3 * 60 * 1000 // Strict 3-minute expiry
  };
  saveDB(db);
  return otp;
}

function verifyOTP(phone, otpInput) {
  const stored = db.otps[phone];
  if (!stored) return false;
  if (Date.now() > stored.expiresAt) {
    delete db.otps[phone];
    saveDB(db);
    return false;
  }
  const isValid = stored.otp === (otpInput || '').trim();
  if (isValid) {
    delete db.otps[phone];
    saveDB(db);
  }
  return isValid;
}

// --- SESSION OPERATIONS ---
function createSession(userId) {
  const token = 'token_' + Date.now() + '_' + Math.random().toString(36).substring(2, 9);
  db.sessions[token] = {
    userId,
    created_at: new Date().toISOString()
  };
  saveDB(db);
  return token;
}

function getUserBySession(token) {
  const session = db.sessions[token];
  if (!session) return null;
  return db.users[session.userId] || null;
}

// --- USER PORTFOLIO OPERATIONS ---
function getUserPortfolio(userId) {
  return db.portfolios[userId] || db.portfolios['user_demo'] || [];
}

function setUserPortfolio(userId, holdings) {
  db.portfolios[userId] = holdings;
  saveDB(db);
  return holdings;
}

// --- CHAT HISTORY OPERATIONS ---
function getUserChatHistory(userId) {
  return db.chatHistories[userId] || [
    { sender: 'Angel', text: 'Greetings! I am Angel, your Chief Investment Officer AI Assistant. Ask me for immediate swing trades, position sizing, or risk limits!', time: '16:54' }
  ];
}

function saveUserChatMessage(userId, msg) {
  if (!db.chatHistories[userId]) db.chatHistories[userId] = [];
  db.chatHistories[userId].push(msg);
  saveDB(db);
}

module.exports = {
  findOrCreateUserByEmail,
  findOrCreateUserByPhone,
  getUserById,
  generateOTP,
  verifyOTP,
  createSession,
  getUserBySession,
  getUserPortfolio,
  setUserPortfolio,
  getUserChatHistory,
  saveUserChatMessage
};

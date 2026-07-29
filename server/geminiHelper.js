// server/geminiHelper.js
/**
 * Re‑usable wrapper for the Firebase Gemini API.
 * Exported so other modules (e.g., orchestrator) can call it without pulling in the entire Express app.
 */
const axios = require('axios');
const FIREBASE_API_KEY = process.env.FIREBASE_API_KEY; // expects .env to be loaded before this file is required

async function callGemini(prompt) {
  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${FIREBASE_API_KEY}`;
  const body = { contents: [{ role: "user", parts: [{ text: prompt }] }] };
  const response = await axios.post(url, body);
  const candidates = response.data?.candidates || [];
  return candidates.map(c => c.content.parts.map(p => p.text).join('')).join('\n');
}

module.exports = { callGemini };

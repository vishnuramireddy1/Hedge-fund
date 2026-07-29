// server/geminiHelper.js
/**
 * Re‑usable wrapper for the Firebase Gemini API.
 * Exported so other modules (e.g., orchestrator) can call it without pulling in the entire Express app.
 */
const axios = require('axios');
async function callGemini(prompt) {
  const apiKey = process.env.FIREBASE_API_KEY || process.env.GEMINI_API_KEY || '';
  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${apiKey}`;
  const body = { contents: [{ role: "user", parts: [{ text: prompt }] }] };
  const response = await axios.post(url, body);
  const candidates = response.data?.candidates || [];
  return candidates.map(c => c.content.parts.map(p => p.text).join('')).join('\n');
}

module.exports = { callGemini };

// server/userMemory.js
/**
 * Simple wrapper around MemoryManager to store a user profile.
 * The profile contains:
 *   - financialLiteracy: string
 *   - work: string
 *   - lifestyle: string
 *   - chatHistory: [{ from: 'user'|'cio', text: string, timestamp: string }]
 *   - portfolioInsights: [{ type: 'positive'|'negative', text: string }]
 */
const memory = require('./memoryManager');
const PROFILE_KEY = 'userProfile';

function getProfile() {
  const defaultProfile = {
    financialLiteracy: null,
    work: null,
    lifestyle: null,
    chatHistory: [],
    portfolioInsights: []
  };
  return { ...defaultProfile, ...(memory.get(PROFILE_KEY) || {}) };
}

function updateProfile(updates) {
  const profile = getProfile();
  memory.set(PROFILE_KEY, { ...profile, ...updates });
}

function addChatMessage(from, text) {
  const profile = getProfile();
  const entry = { from, text, timestamp: new Date().toLocaleTimeString() };
  profile.chatHistory.push(entry);
  memory.set(PROFILE_KEY, { ...profile });
}

function addPortfolioInsight(type, text) {
  const profile = getProfile();
  profile.portfolioInsights.push({ type, text });
  memory.set(PROFILE_KEY, { ...profile });
}

module.exports = { getProfile, updateProfile, addChatMessage, addPortfolioInsight };

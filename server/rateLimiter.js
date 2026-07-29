// server/rateLimiter.js
/**
 * Simple token bucket rate limiter per agent key.
 * Allows `maxCalls` within `windowMs` (default 5 calls per minute).
 */
class RateLimiter {
  constructor(maxCalls = 5, windowMs = 60 * 1000) {
    this.maxCalls = maxCalls;
    this.windowMs = windowMs;
    this.buckets = new Map(); // key -> array of timestamps
  }

  allow(key) {
    const now = Date.now();
    if (!this.buckets.has(key)) {
      this.buckets.set(key, []);
    }
    const timestamps = this.buckets.get(key).filter(ts => now - ts < this.windowMs);
    if (timestamps.length < this.maxCalls) {
      timestamps.push(now);
      this.buckets.set(key, timestamps);
      return true;
    }
    return false;
  }
}

module.exports = new RateLimiter();

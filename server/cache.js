// server/cache.js
/**
 * Simple in‑memory cache with TTL (seconds).
 * Stores arbitrary values keyed by a string.
 */
class Cache {
  constructor() {
    this.store = new Map(); // key -> { value, expires }
  }

  /**
   * Store a value for `ttlSeconds`.
   * @param {string} key
   * @param {*} value
   * @param {number} ttlSeconds
   */
  set(key, value, ttlSeconds) {
    const expires = Date.now() + ttlSeconds * 1000;
    this.store.set(key, { value, expires });
  }

  /** Retrieve a value if it hasn't expired, otherwise null. */
  get(key) {
    const entry = this.store.get(key);
    if (!entry) return null;
    if (Date.now() > entry.expires) {
      this.store.delete(key);
      return null;
    }
    return entry.value;
  }
}

module.exports = new Cache();

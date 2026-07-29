const fs = require('fs');
const path = require('path');

/**
 * Simple in‑memory store with optional JSON persistence.
 * Stores a plain object per agent key. Child agents can read the merged context
 * of all their ancestors via `getEffectiveContext`.
 */
class MemoryManager {
  constructor(persist = true) {
    this.store = {};
    this.persist = persist;
    this.filePath = path.resolve(__dirname, 'memory.json');
    if (this.persist) this._loadFromDisk();
  }

  set(agentKey, data) {
    this.store[agentKey] = { ...(this.store[agentKey] || {}), ...data };
    if (this.persist) this._saveToDisk();
  }

  get(agentKey) {
    return this.store[agentKey] || {};
  }

  /** Merge contexts from root -> target using the hierarchy definition */
  getEffectiveContext(targetKey, hierarchy) {
    const path = this._findPath(targetKey, hierarchy);
    if (!path) return {};
    return path.reduce((ctx, key) => ({ ...ctx, ...(this.store[key] || {}) }), {});
  }

  /** Recursively find the ancestry path for a given key */
  _findPath(targetKey, hierarchy, curPath = [], visited = new Set()) {
    for (const [key, node] of Object.entries(hierarchy)) {
      if (visited.has(key)) continue;
      visited.add(key);
      const newPath = [...curPath, key];
      if (key === targetKey) return newPath;
      if (node.children) {
        const childHierarchy = {};
        node.children.forEach(childKey => {
          if (hierarchy[childKey]) childHierarchy[childKey] = hierarchy[childKey];
        });
        const res = this._findPath(targetKey, childHierarchy, newPath, visited);
        if (res) return res;
      }
    }
    return null;
  }

  _loadFromDisk() {
    try {
      if (fs.existsSync(this.filePath)) {
        const raw = fs.readFileSync(this.filePath, 'utf-8');
        this.store = JSON.parse(raw);
      }
    } catch (e) {
      console.error('MemoryManager load error:', e);
    }
  }

  _saveToDisk() {
    try {
      fs.writeFileSync(this.filePath, JSON.stringify(this.store, null, 2), 'utf-8');
    } catch (e) {
      console.error('MemoryManager save error:', e);
    }
  }
}

module.exports = new MemoryManager(true);

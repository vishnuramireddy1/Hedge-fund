// server/validateHierarchy.js
/**
 * Validates the AgentRoles hierarchy for cycles and missing references.
 * Throws an Error if a problem is detected.
 */
function validateHierarchy(agentRoles) {
  const visited = new Set();
  const stack = new Set();

  function dfs(key) {
    if (!agentRoles[key]) {
      throw new Error(`Agent key "${key}" referenced but not defined`);
    }
    if (stack.has(key)) {
      const cycle = Array.from(stack).concat(key).join(' -> ');
      throw new Error(`Circular hierarchy detected: ${cycle}`);
    }
    if (visited.has(key)) return; // already checked
    visited.add(key);
    stack.add(key);
    const node = agentRoles[key];
    if (node.children) {
      for (const child of node.children) {
        dfs(child);
      }
    }
    stack.delete(key);
  }

  // start from every top‑level node (those not referenced as children)
  const allChildren = new Set();
  Object.values(agentRoles).forEach(agent => {
    if (agent.children) agent.children.forEach(c => allChildren.add(c));
  });
  const roots = Object.keys(agentRoles).filter(k => !allChildren.has(k));
  if (roots.length === 0) {
    throw new Error('No root nodes detected in agent hierarchy');
  }
  roots.forEach(dfs);
}

module.exports = { validateHierarchy };

// server/orchestrator.js
const { AgentRoles } = require('./agentConfig');
const memory = require('./memoryManager');
const axios = require('axios');
const { callGemini } = require('./geminiHelper'); // reuse existing function from index.js (you may need to export it)
const rateLimiter = require('./rateLimiter');
const cache = require('./cache');
const { runTools } = require('./toolRegistry'); // new import for tool execution

/**
 * Helper to invoke a leaf scan via existing backend endpoint.
 * Uses a short timeout and returns a structured result.
 */
async function runLeafScan(agentKey) {
  try {
    const source = `http://localhost:3000/api/scan/${agentKey}`;
    const resp = await axios.get(source, { timeout: 5000 });
    const data = resp.data;
    memory.set(agentKey, { lastResult: data });
    return data;
  } catch (e) {
    console.error(`Leaf scan error for ${agentKey}:`, e.message);
    return { roleKey: agentKey, status: 'ERROR', findings: `Error: ${e.message}` };
  }
}

/**
 * Recursive orchestrator. Handles both leaf and parent agents.
 * Executes declared tools, aggregates child results, optionally synthesizes with Gemini, and respects rate‑limiting.
 */
async function invokeAgent(agentKey) {
  // Rate‑limit per‑agent (max 5 calls per minute by default)
  if (!rateLimiter.allow(agentKey)) {
    return { roleKey: agentKey, status: 'THROTTLED', findings: 'Rate limit exceeded' };
  }

  const cached = cache.get(agentKey);
  if (cached) return cached;

  const agent = AgentRoles[agentKey];
  if (!agent) {
    return { roleKey: agentKey, status: 'ERROR', findings: 'Agent not defined' };
  }

  // -------------------------------------------------------------------
  // 1️⃣ Run any tools declared for this agent.
  // -------------------------------------------------------------------
  const toolResults = await runTools(agent.tools || []);

  // -------------------------------------------------------------------
  // 2️⃣ Leaf node handling – if the agent has no children we either
  //    run its tools (if any) or fall back to the generic scan endpoint.
  // -------------------------------------------------------------------
  if (!agent.children) {
    // If tools produced output, prefer that as the authoritative result.
    if (Object.keys(toolResults).length) {
      const leafResult = {
        roleKey: agentKey,
        title: agent.title,
        status: 'SUCCESS',
        tools: toolResults,
        timestamp: new Date().toLocaleTimeString()
      };
      memory.set(agentKey, { lastResult: leafResult });
      cache.set(agentKey, leafResult, 30);
      return leafResult;
    }
    // Otherwise delegate to the generic scan endpoint.
    const result = await runLeafScan(agentKey);
    // Merge any tool output (empty in this path) for consistency.
    result.tools = toolResults;
    cache.set(agentKey, result, 30); // cache leaf result for 30 seconds
    return result;
  }

  // -------------------------------------------------------------------
  // 3️⃣ Parent node – invoke children in parallel with per‑child timeout.
  // -------------------------------------------------------------------
  const childPromises = agent.children.map(childKey =>
    // Wrap each child with its own timeout (4s) to avoid hanging the whole parent
    Promise.race([
      invokeAgent(childKey),
      new Promise(resolve => setTimeout(() => resolve({ roleKey: childKey, status: 'TIMEOUT', findings: 'Child timed out' }), 4000))
    ])
  );

  const childResults = await Promise.all(childPromises);

  // Aggregate findings and compute average confidence (ignore missing values)
  const valid = childResults.filter(r => typeof r.confidencePct === 'number');
  const avgConfidence = valid.length ? Math.round(valid.reduce((s, r) => s + r.confidencePct, 0) / valid.length) : null;
  const aggregatedFindings = childResults.map(r => r.findings).join('\n---\n');

  const agg = {
    roleKey: agentKey,
    title: agent.title,
    status: 'SUCCESS',
    confidencePct: avgConfidence,
    timestamp: new Date().toLocaleTimeString(),
    findings: aggregatedFindings,
    tools: toolResults // expose this agent's own tool outputs alongside child findings
  };

  // Optional AI synthesis – can be toggled via env var
  if (process.env.SYNTHESIZE_PARENT === 'true') {
    try {
      const prompt = `Summarize the following findings from ${agent.title} and provide a concise actionable insight:\n${aggregatedFindings}`;
      const synthesis = await callGemini(prompt);
      agg.summary = synthesis;
    } catch (e) {
      console.error('Gemini synthesis failed:', e.message);
    }
  }

  // Store context for downstream agents
  memory.set(agentKey, { lastResult: agg, summary: agg.summary });
  cache.set(agentKey, agg, 60); // cache parent result for 60 seconds
  return agg;
}

module.exports = { invokeAgent };

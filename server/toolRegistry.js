// server/toolRegistry.js
/**
 * Central registry of tool names used by agents.
 * The actual execution is delegated to `toolExecutor.js` which selects the
 * appropriate framework (Google ADK, LangChain, or MCP) based on the
 * `TOOL_FRAMEWORK` environment variable.
 */

// List of all supported tool identifiers. The orchestrator will ask the
// registry to run a subset of these for each agent.
const tools = [
  // Market Operations tools
  'NSEDataStream',
  'MarketBreadthScanner',
  'IndexCalculator',

  // News Intelligence tools
  'BseAnnouncementScanner',
  'NewsSentimentAnalyzer',
  'EntityExtractor',

  // Portfolio Management tools
  'PositionSizer',
  'PortfolioBetaCalculator',
  'DrawdownMonitor',

  // Fundamental Analysis tools
  'FinancialStatementParser',
  'RatioCalculator',
  'DupontAnalyzer',

  // Technical Analysis tools
  'TechnicalIndicators',
  'ChartPatternRecognizer',
  'SupportResistanceFinder',

  // Macro tools
  'RbiPolicyTracker',
  'MacroDataFetcher',
  'InflationModel',

  // Add further tool identifiers as the system grows.
];

const { executeTool } = require('./toolExecutor');

/**
 * Execute an array of tool names.
 * Returns an object mapping each tool name to its result (or error).
 */
async function runTools(toolNames) {
  const results = {};
  for (const name of toolNames) {
    if (!tools.includes(name)) {
      console.warn(`Tool ${name} is not registered in toolRegistry`);
      results[name] = { error: 'Tool not registered' };
      continue;
    }
    try {
      results[name] = await executeTool(name);
    } catch (e) {
      console.error(`Tool ${name} execution error:`, e);
      results[name] = { error: e.message };
    }
  }
  return results;
}

module.exports = { runTools, tools };

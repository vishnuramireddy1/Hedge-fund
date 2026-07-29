const AgentRoles = {
  // ── ROOT LEVEL (high‑level suites) ──────────────────────────────────────
  MARKET_OPERATIONS: {
    key: "MARKET_OPERATIONS",
    title: "Market Operations Suite",
    description: "Orchestrates market‑data, liquidity, and news agents.",
    tools: [],
    children: ["MARKET_INTELLIGENCE", "LIQUIDITY_MARKET_STRUCTURE", "NEWS_INTELLIGENCE"]
  },
  RESEARCH_ANALYSIS: {
    key: "RESEARCH_ANALYSIS",
    title: "Research & Analysis Suite",
    description: "Coordinates fundamental, technical, macro, and sector agents.",
    tools: [],
    children: [
      "FUNDAMENTAL_ANALYSIS",
      "TECHNICAL_ANALYSIS",
      "MACRO_ECONOMY",
      "SECTOR_ROTATION",
      "COMPANY_RESEARCH",
      "COMPETITION_ANALYSIS",
      "BUSINESS_QUALITY",
      "MANAGEMENT_ACCOUNTABILITY",
      "TRAP_DETECTION",
      "EARNINGS_ANALYSIS",
      "VALUATION"
    ]
  },
  // ── LEAF AGENTS (unchanged from original) ──────────────────────────────────────
  CIO: {
    key: "CIO",
    title: "Chief Investment Officer Agent",
    description: "Final decision maker, synthesises research from all sub‑agents into actionable theses.",
    tools: ["ThesisSynthesizer", "PortfolioAllocated", "RiskGatekeeper"]
  },
  MARKET_INTELLIGENCE: {
    key: "MARKET_INTELLIGENCE",
    title: "Market Intelligence Agent",
    description: "Monitors real‑time NSE/BSE market structure, breadth, and order flows.",
    tools: ["NSEDataStream", "MarketBreadthScanner", "IndexCalculator"]
  },
  NEWS_INTELLIGENCE: {
    key: "NEWS_INTELLIGENCE",
    title: "News Intelligence Agent",
    description: "Scans SEBI filings, BSE announcements, financial news, and sentiment.",
    tools: ["BseAnnouncementScanner", "NewsSentimentAnalyzer", "EntityExtractor"]
  },
  PORTFOLIO_MANAGER: {
    key: "PORTFOLIO_MANAGER",
    title: "Portfolio Manager Agent",
    description: "Monitors portfolio allocation, position sizing, beta, cash ratio, and drawdown.",
    tools: ["PositionSizer", "PortfolioBetaCalculator", "DrawdownMonitor"]
  },
  FUNDAMENTAL_ANALYSIS: {
    key: "FUNDAMENTAL_ANALYSIS",
    title: "Fundamental Analysis Agent",
    description: "Analyzes Balance Sheets, P&L, Cash Flow, ROE, ROCE, and Debt ratios.",
    tools: ["FinancialStatementParser", "RatioCalculator", "DupontAnalyzer"]
  },
  TECHNICAL_ANALYSIS: {
    key: "TECHNICAL_ANALYSIS",
    title: "Technical Analysis Agent",
    description: "Calculates RSI, MACD, Moving Averages, Supertrend, and Chart Breakouts.",
    tools: ["TechnicalIndicators", "ChartPatternRecognizer", "SupportResistanceFinder"]
  },
  MACRO_ECONOMY: {
    key: "MACRO_ECONOMY",
    title: "Macro Economy Agent",
    description: "Monitors RBI Monetary Policy, Inflation (CPI/WPI), US Fed rates, and Crude Oil.",
    tools: ["RbiPolicyTracker", "MacroDataFetcher", "InflationModel"]
  },
  SECTOR_ROTATION: {
    key: "SECTOR_ROTATION",
    title: "Sector Rotation Agent",
    description: "Tracks relative strength of Indian sectors (Nifty Auto, IT, Pharma, Infra).",
    tools: ["SectorHeatmapCalculator", "RelativeStrengthIndex", "FlowTracker"]
  },
  COMPANY_RESEARCH: {
    key: "COMPANY_RESEARCH",
    title: "Company Research Agent",
    description: "Conducts deep‑dive company research, corporate history, and product mix.",
    tools: ["AnnualReportReader", "CorporateFilingParser", "ProductMixAnalyzer"]
  },
  COMPETITION_ANALYSIS: {
    key: "COMPETITION_ANALYSIS",
    title: "Competition Analysis Agent",
    description: "Evaluates peer market share, competitive intensity, and industry pricing power.",
    tools: ["PeerComparisonTool", "MarketShareTracker", "PricingPowerEvaluator"]
  },
  BUSINESS_QUALITY: {
    key: "BUSINESS_QUALITY",
    title: "Business Quality Agent",
    description: "Evaluates Economic Moats, pricing power, capital allocation, and ROIC.",
    tools: ["MoatEvaluator", "CapitalEfficiencyCalculator", "PricingPowerScore"]
  },
  MANAGEMENT_ACCOUNTABILITY: {
    key: "MANAGEMENT_ACCOUNTABILITY",
    title: "Management Accountability Agent",
    description: "Checks promoter background, promoter pledged shares, salary vs profit, and SEBI compliance.",
    tools: ["PromoterPledgeChecker", "GovernanceAuditor", "SebiFilingAnalyzer"]
  },
  LIQUIDITY_MARKET_STRUCTURE: {
    key: "LIQUIDITY_MARKET_STRUCTURE",
    title: "Liquidity & Market Structure Agent",
    description: "Analyzes FII/DII institutional flows, delivery percentages, and float liquidity.",
    tools: ["FiiDiiFlowTracker", "DeliveryVolumeAnalyzer", "FloatLiquidityCalculator"]
  },
  TRAP_DETECTION: {
    key: "TRAP_DETECTION",
    title: "Trap Detection Agent",
    description: "Detects value traps, accounting red flags, promoter dumps, and momentum traps.",
    tools: ["RedFlagScanner", "PledgeWarningDetector", "AccountingAnomalyChecker"]
  },
  OPPORTUNITY_DISCOVERY: {
    key: "OPPORTUNITY_DISCOVERY",
    title: "Opportunity Discovery Agent",
    description: "Discovers high‑probability momentum breakouts and undervalued compounders.",
    tools: ["BreakoutScanner", "UndervaluedCompounderFinder", "VolumeSpikeDetector"]
  },
  RISK_ANALYSIS: {
    key: "RISK_ANALYSIS",
    title: "Risk Analysis Agent",
    description: "Evaluates Value at Risk (VaR), portfolio beta, correlation matrix, and downside risk.",
    tools: ["VaRCalculator", "CorrelationMatrix", "DownsideRiskEvaluator"]
  },
  EARNINGS_ANALYSIS: {
    key: "EARNINGS_ANALYSIS",
    title: "Earnings Analysis Agent",
    description: "Parses quarterly earnings reports (Q1‑Q4), concall transcripts, and revenue guidance.",
    tools: ["EarningsTranscriptParser", "GuidanceTracker", "SurpriseCalculator"]
  },
  VALUATION: {
    key: "VALUATION",
    title: "Valuation Agent",
    description: "Calculates DCF Fair Value, PE/PB Band multiples, and Margin of Safety.",
    tools: ["DCFCalculator", "MultipleBandAnalyzer", "MarginOfSafetyCalculator"]
  },
  EVENT_IMPACT: {
    key: "EVENT_IMPACT",
    title: "Event Impact Agent",
    description: "Quantifies market impact of Union Budget, RBI rate decisions, and geopolitical events.",
    tools: ["EventImpactModel", "VolatilityPredictor", "PolicyParser"]
  },
  WATCHLIST: {
    key: "WATCHLIST",
    title: "Watchlist Agent",
    description: "Curates and ranks high‑conviction stocks nearing buy zones.",
    tools: ["BuyZoneAlertSystem", "WatchlistRanker", "CatalystTracker"]
  },
  LEARNING_PERFORMANCE: {
    key: "LEARNING_PERFORMANCE",
    title: "Learning & Performance Agent",
    description: "Analyzes past trade journal outcomes, calculates win‑rate, and updates AI weights.",
    tools: ["TradeJournalAuditor", "WinRateCalculator", "FeedbackLoopOptimizer"]
  },
  MEMORY_MANAGEMENT: {
    key: "MEMORY_MANAGEMENT",
    title: "Memory Management Agent",
    description: "Maintains historical memory, updates Markdown knowledge base, and vector memory.",
    tools: ["KnowledgeBaseUpdater", "MarkdownFormatter", "MemorySearchEngine"]
  },
  DAILY_BRIEFING: {
    key: "DAILY_BRIEFING",
    title: "Daily Briefing Agent",
    description: "Generates daily pre‑market & post‑market executive briefing summaries.",
    tools: ["DailyReportGenerator", "MarketSummaryBuilder", "ExecutiveBriefWriter"]
  },
  WEEKLY_REVIEW: {
    key: "WEEKLY_REVIEW",
    title: "Weekly Review Agent",
    description: "Synthesizes weekly portfolio performance, sectoral trends, and trade reviews.",
    tools: ["WeeklyReportGenerator", "TrendSynthesizer", "TradeReviewer"]
  },
  MONTHLY_STRATEGY: {
    key: "MONTHLY_STRATEGY",
    title: "Monthly Strategy Agent",
    description: "Formulates monthly macroeconomic outlook, asset allocation shifts, and long‑term thesis.",
    tools: ["MonthlyStrategyBuilder", "AssetAllocationPlanner", "MacroOutlookSynthesizer"]
  },
  SWING_TRADE_EXPERT: {
    key: "SWING_TRADE_EXPERT",
    title: "Swing Trade & Breakout Specialist",
    description: "Finds immediate high‑conviction swing trade setups with precise entry, target, stop loss, and R:R ratio.",
    tools: ["SwingSetupScanner", "BreakoutVolumeScanner", "RiskRewardCalculator"]
  },
  TIMING_CATALYST_AGENT: {
    key: "TIMING_CATALYST_AGENT",
    title: "Why Now? Catalyst Agent",
    description: "Analyzes immediate technical breakouts, earnings catalysts, order book spikes, and news events explaining why to trade right now.",
    tools: ["CatalystTriggerDetector", "OrderFlowSpikeAnalyzer", "TimingPrecisionEngine"]
  }
};

module.exports = { AgentRoles };

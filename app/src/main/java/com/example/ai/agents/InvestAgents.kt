package com.example.ai.agents

enum class AgentRole(
    val title: String,
    val category: String,
    val description: String,
    val tools: List<String>
) {
    CIO("Chief Investment Officer Agent", "Core Executive", "Final decision maker, synthesizes research from all sub-agents into actionable theses.", listOf("ThesisSynthesizer", "PortfolioAllocated", "RiskGatekeeper")),
    MARKET_INTELLIGENCE("Market Intelligence Agent", "Market Operations", "Monitors real-time NSE/BSE market structure, breadth, and order flows.", listOf("NSEDataStream", "MarketBreadthScanner", "IndexCalculator")),
    NEWS_INTELLIGENCE("News Intelligence Agent", "Market Operations", "Scans SEBI filings, BSE announcements, financial news, and sentiment.", listOf("BseAnnouncementScanner", "NewsSentimentAnalyzer", "EntityExtractor")),
    PORTFOLIO_MANAGER("Portfolio Manager Agent", "Portfolio & Risk", "Monitors portfolio allocation, position sizing, beta, cash ratio, and drawdown.", listOf("PositionSizer", "PortfolioBetaCalculator", "DrawdownMonitor")),
    FUNDAMENTAL_ANALYSIS("Fundamental Analysis Agent", "Research & Analysis", "Analyzes Balance Sheets, P&L, Cash Flow, ROE, ROCE, and Debt ratios.", listOf("FinancialStatementParser", "RatioCalculator", "DupontAnalyzer")),
    TECHNICAL_ANALYSIS("Technical Analysis Agent", "Research & Analysis", "Calculates RSI, MACD, Moving Averages, Supertrend, and Chart Breakouts.", listOf("TechnicalIndicators", "ChartPatternRecognizer", "SupportResistanceFinder")),
    MACRO_ECONOMY("Macro Economy Agent", "Macro & Sector", "Monitors RBI Monetary Policy, Inflation (CPI/WPI), US Fed rates, and Crude Oil.", listOf("RbiPolicyTracker", "MacroDataFetcher", "InflationModel")),
    SECTOR_ROTATION("Sector Rotation Agent", "Macro & Sector", "Tracks relative strength of Indian sectors (Nifty Auto, IT, Pharma, Infra).", listOf("SectorHeatmapCalculator", "RelativeStrengthIndex", "FlowTracker")),
    COMPANY_RESEARCH("Company Research Agent", "Research & Analysis", "Conducts deep-dive company research, corporate history, and product mix.", listOf("AnnualReportReader", "CorporateFilingParser", "ProductMixAnalyzer")),
    COMPETITION_ANALYSIS("Competition Analysis Agent", "Research & Analysis", "Evaluates peer market share, competitive intensity, and industry pricing power.", listOf("PeerComparisonTool", "MarketShareTracker", "PricingPowerEvaluator")),
    BUSINESS_QUALITY("Business Quality Agent", "Research & Analysis", "Evaluates Economic Moats, pricing power, capital allocation, and ROIC.", listOf("MoatEvaluator", "CapitalEfficiencyCalculator", "PricingPowerScore")),
    MANAGEMENT_ACCOUNTABILITY("Management Accountability Agent", "Research & Analysis", "Checks promoter background, promoter pledged shares, salary vs profit, and SEBI compliance.", listOf("PromoterPledgeChecker", "GovernanceAuditor", "SebiFilingAnalyzer")),
    LIQUIDITY_MARKET_STRUCTURE("Liquidity & Market Structure Agent", "Market Operations", "Analyzes FII/DII institutional flows, delivery percentages, and float liquidity.", listOf("FiiDiiFlowTracker", "DeliveryVolumeAnalyzer", "FloatLiquidityCalculator")),
    TRAP_DETECTION("Trap Detection Agent", "Risk & Safeguard", "Detects value traps, accounting red flags, promoter dumps, and momentum traps.", listOf("RedFlagScanner", "PledgeWarningDetector", "AccountingAnomalyChecker")),
    OPPORTUNITY_DISCOVERY("Opportunity Discovery Agent", "Discovery", "Discovers high-probability momentum breakouts and undervalued compounders.", listOf("BreakoutScanner", "UndervaluedCompounderFinder", "VolumeSpikeDetector")),
    RISK_ANALYSIS("Risk Analysis Agent", "Portfolio & Risk", "Evaluates Value at Risk (VaR), portfolio beta, correlation matrix, and downside risk.", listOf("VaRCalculator", "CorrelationMatrix", "DownsideRiskEvaluator")),
    EARNINGS_ANALYSIS("Earnings Analysis Agent", "Research & Analysis", "Parses quarterly earnings reports (Q1-Q4), concall transcripts, and revenue guidance.", listOf("EarningsTranscriptParser", "GuidanceTracker", "SurpriseCalculator")),
    VALUATION("Valuation Agent", "Research & Analysis", "Calculates DCF Fair Value, PE/PB Band multiples, and Margin of Safety.", listOf("DCFCalculator", "MultipleBandAnalyzer", "MarginOfSafetyCalculator")),
    EVENT_IMPACT("Event Impact Agent", "Macro & Sector", "Quantifies market impact of Union Budget, RBI rate decisions, and geopolitical events.", listOf("EventImpactModel", "VolatilityPredictor", "PolicyParser")),
    WATCHLIST("Watchlist Agent", "Discovery", "Curates and ranks high-conviction stocks nearing buy zones.", listOf("BuyZoneAlertSystem", "WatchlistRanker", "CatalystTracker")),
    LEARNING_PERFORMANCE("Learning & Performance Agent", "System Intelligence", "Analyzes past trade journal outcomes, calculates win-rate, and updates AI weights.", listOf("TradeJournalAuditor", "WinRateCalculator", "FeedbackLoopOptimizer")),
    MEMORY_MANAGEMENT("Memory Management Agent", "System Intelligence", "Maintains historical memory, updates Markdown knowledge base, and vector memory.", listOf("KnowledgeBaseUpdater", "MarkdownFormatter", "MemorySearchEngine")),
    DAILY_BRIEFING("Daily Briefing Agent", "Reporting", "Generates daily pre-market & post-market executive briefing summaries.", listOf("DailyReportGenerator", "MarketSummaryBuilder", "ExecutiveBriefWriter")),
    WEEKLY_REVIEW("Weekly Review Agent", "Reporting", "Synthesizes weekly portfolio performance, sectoral trends, and trade reviews.", listOf("WeeklyReportGenerator", "TrendSynthesizer", "TradeReviewer")),
    MONTHLY_STRATEGY("Monthly Strategy Agent", "Reporting", "Formulates monthly macroeconomic outlook, asset allocation shifts, and long-term thesis.", listOf("MonthlyStrategyBuilder", "AssetAllocationPlanner", "MacroOutlookSynthesizer")),
    SWING_TRADE_EXPERT("Swing Trade & Breakout Specialist", "Discovery & Execution", "Finds immediate high-conviction swing trade setups with precise entry, target, stop loss, and R:R ratio.", listOf("SwingSetupScanner", "BreakoutVolumeScanner", "RiskRewardCalculator")),
    TIMING_CATALYST_AGENT("Why Now? Catalyst Agent", "Market Operations", "Analyzes immediate technical breakouts, earnings catalysts, order book spikes, and news events explaining why to trade right now.", listOf("CatalystTriggerDetector", "OrderFlowSpikeAnalyzer", "TimingPrecisionEngine"))
}

data class AgentExecutionResult(
    val agentRole: AgentRole,
    val status: String, // SUCCESS, WARNING, RUNNING, IDLE, ERROR
    val confidencePct: Int,
    val taskName: String,
    val findingsText: String,
    val timestamp: String,
    val tokenUsage: Int,
    val executionTimeMs: Long
)

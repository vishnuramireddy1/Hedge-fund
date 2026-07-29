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
  // ── LEAF AGENTS (30-Year Veteran Domain Experts & 200% Toolsets) ──────────────────────────────────────
  CIO: {
    key: "CIO",
    title: "Angel — Chief Investment Officer Agent",
    description: "30-Year Veteran Hedge Fund CIO. Synthesizes research from all 27 sub-agents into institutional investment theses, capital allocation, and strict risk limits.",
    tools: ["ThesisSynthesizer", "MultiAgentOrchestrator", "PortfolioCapitalAllocator", "RiskGatekeeper", "MacroRegimeClassifier", "DrawdownShield"]
  },
  MARKET_INTELLIGENCE: {
    key: "MARKET_INTELLIGENCE",
    title: "Market Intelligence & Microstructure Agent",
    description: "30-Year Senior Market Microstructure Specialist. Monitors real-time NSE/BSE market structure, order book liquidity, market breadth, and tick-level flows.",
    tools: ["NSEBseLiveDataStream", "MarketBreadthScanner", "AdvanceDeclineRatioModel", "TickLevelOrderBookScanner", "InstitutionalBlockDealTracker", "VIXVolatilityRegimeDetector"]
  },
  NEWS_INTELLIGENCE: {
    key: "NEWS_INTELLIGENCE",
    title: "Corporate News & SEBI Intelligence Agent",
    description: "30-Year Corporate Disclosure Veteran. Scans SEBI regulatory filings, BSE announcements, sentiment, and insider trading patterns.",
    tools: ["SEBIFilingAnalyzer", "BSECorporateAnnouncementsScanner", "FinancialNewsSentimentEngine", "EntityRelationshipExtractor", "InsiderTradingMonitor", "MediaHypeDetector"]
  },
  PORTFOLIO_MANAGER: {
    key: "PORTFOLIO_MANAGER",
    title: "Senior Quantitative Portfolio Manager",
    description: "30-Year Senior Quantitative Portfolio Manager. Monitors optimal capital sizing, portfolio beta, Kelly Criterion limits, and cash buffer reserves.",
    tools: ["OptimalPositionSizer", "PortfolioBetaCalculator", "KellyCriterionEngine", "DrawdownMonitor", "CashBufferReserveManager", "RebalanceOptimizer"]
  },
  FUNDAMENTAL_ANALYSIS: {
    key: "FUNDAMENTAL_ANALYSIS",
    title: "Senior Fundamental Analysis Agent",
    description: "30-Year Senior Fundamental & Financial Statement Analyst. Analyzes Balance Sheets, P&L, DuPont ROE, ROIC vs WACC, and Free Cash Flow Yield.",
    tools: ["FinancialStatementParser", "DuPont3FactorAnalyzer", "ROICvsWACCEvaluator", "FreeCashFlowYieldCalculator", "DebtCoverageRatioModel", "WorkingCapitalEfficiencyScanner"]
  },
  TECHNICAL_ANALYSIS: {
    key: "TECHNICAL_ANALYSIS",
    title: "Technical Analysis & Chart Pattern Architect",
    description: "30-Year Chartered Market Technician (CMT). Calculates multi-timeframe EMAs, RSI/MACD confluence, Supertrend breakouts, and Volume Profile nodes.",
    tools: ["MultiTimeframeEMAEngine", "RSI_MACD_ConfluenceScanner", "SupertrendBreakoutDetector", "SupportResistanceNodeFinder", "VolumeProfileAnalyzer", "FibonacciRetracementEngine"]
  },
  MACRO_ECONOMY: {
    key: "MACRO_ECONOMY",
    title: "Chief Macro Economy & Policy Agent",
    description: "30-Year Chief Macro Economist. Tracks RBI Monetary Policy stance, CPI/WPI inflation, US Fed rate trajectories, Brent Crude, and Yield Curves.",
    tools: ["RBIMonetaryPolicyTracker", "FedInterestRateExpectationModel", "InflationCPI_WPI_Analyzer", "BrentCrudeFxSensitivityModel", "YieldCurveInversionDetector", "GlobalCapitalFlowMonitor"]
  },
  SECTOR_ROTATION: {
    key: "SECTOR_ROTATION",
    title: "Sector Rotation & Relative Strength Agent",
    description: "30-Year Senior Sector Strategist. Tracks relative strength of Indian sectors (Nifty Auto, IT, Pharma, Infra, PSU Banks) and capital flows.",
    tools: ["NiftySectorHeatmapCalculator", "SectorRelativeStrengthIndex", "InstitutionalSectorFlowTracker", "CyclicalVsDefensiveRotationModel", "AlphaGenerationEngine", "ThemeBreakoutScanner"]
  },
  COMPANY_RESEARCH: {
    key: "COMPANY_RESEARCH",
    title: "Equity Research Director",
    description: "30-Year Equity Research Director. Conducts granular deep-dives into corporate history, product mix, capex execution, and management guidance.",
    tools: ["AnnualReportDeepDiveReader", "CorporateFilingParser", "SegmentalRevenueAnalyzer", "ProductMixMarginEvaluator", "CapexExecutionTracker", "ManagementGuidanceAuditor"]
  },
  COMPETITION_ANALYSIS: {
    key: "COMPETITION_ANALYSIS",
    title: "Competitive Moat & Market Share Agent",
    description: "30-Year Competitive Intelligence Head. Evaluates peer market share, competitive intensity, Porter's 5 Forces, and pricing power.",
    tools: ["PeerComparisonMatrix", "IndustryMarketShareTracker", "PricingPowerEvaluator", "Porter5ForcesScorer", "CustomerSwitchingCostAnalyzer", "SupplyChainBottleneckDetector"]
  },
  BUSINESS_QUALITY: {
    key: "BUSINESS_QUALITY",
    title: "Business Quality & Moat Auditor",
    description: "30-Year Warren Buffett Style Business Auditor. Evaluates Economic Moats, ROIC reinvestment rates, capital efficiency, and owner earnings.",
    tools: ["EconomicMoatEvaluator", "CapitalEfficiencyScorecard", "PricingPowerRatingEngine", "ROICReinvestmentRateModel", "OwnerEarningsCalculator", "PricingDisciplineTracker"]
  },
  MANAGEMENT_ACCOUNTABILITY: {
    key: "MANAGEMENT_ACCOUNTABILITY",
    title: "Corporate Governance & Integrity Agent",
    description: "30-Year Corporate Governance Head. Checks promoter backgrounds, pledge ratios, related-party transactions (RPTs), and SEBI compliance.",
    tools: ["PromoterPledgeChecker", "RelatedPartyTransactionAuditor", "ExecutiveCompensationVsProfitModel", "SEBIEnforcementTracker", "BoardIndependenceScorer", "AuditQualificationScanner"]
  },
  LIQUIDITY_MARKET_STRUCTURE: {
    key: "LIQUIDITY_MARKET_STRUCTURE",
    title: "Institutional Flow & Liquidity Agent",
    description: "30-Year Institutional Trading Desk Head. Analyzes FII/DII net flow dynamics, delivery volume spikes, free-float liquidity, and block deals.",
    tools: ["FIIDIIFlowTracker", "DeliveryVolumeSpikeAnalyzer", "FreeFloatLiquidityCalculator", "BulkBlockDealScanner", "DarkPoolFlowEstimator", "ShortInterestTracker"]
  },
  TRAP_DETECTION: {
    key: "TRAP_DETECTION",
    title: "Forensic Accounting & Trap Detector",
    description: "30-Year Forensic Accounting Auditor. Scans for Beneish M-Score manipulation, Altman Z-Score solvency risks, OCF divergence, and value traps.",
    tools: ["BeneishMScoreCalculator", "AltmanZScoreModel", "PledgeWarningDetector", "OperatingCashFlowDivergenceScanner", "AggressiveRevenueRecognitionDetector", "AuditorResignationAlert"]
  },
  OPPORTUNITY_DISCOVERY: {
    key: "OPPORTUNITY_DISCOVERY",
    title: "Alpha Opportunity Discovery Agent",
    description: "30-Year Alpha Discovery Head. Discovers high-probability momentum breakouts, 52-week consolidation breakouts, and turnaround compounders.",
    tools: ["HighConvictionBreakoutScanner", "UndervaluedCompounderFinder", "VolumeSpikeDetector", "TurnaroundCandidateFilter", "52WeekHighBreakoutMonitor", "DeepValueScreen"]
  },
  RISK_ANALYSIS: {
    key: "RISK_ANALYSIS",
    title: "Chief Risk Officer (CRO) Agent",
    description: "30-Year Chief Risk Officer. Evaluates Historical VaR, Monte Carlo simulations, correlation matrices, tail risks, and drawdown limits.",
    tools: ["HistoricalVaRCalculator", "MonteCarloSimulationEngine", "CrossAssetCorrelationMatrix", "DownsideTailRiskEvaluator", "StressTestingModel", "PortfolioMaxDrawdownShield"]
  },
  EARNINGS_ANALYSIS: {
    key: "EARNINGS_ANALYSIS",
    title: "Earnings & Concall Specialist Agent",
    description: "30-Year Quarterly Earnings Analyst. Parses concall transcripts, guidance beats/misses, margin expansion, and order backlog trends.",
    tools: ["EarningsConcallTranscriptParser", "GuidanceBeatMissTracker", "EarningsSurpriseCalculator", "MarginExpansionContractorModel", "OrderBookBacklogAuditor", "InventoryTurnoverAnalyzer"]
  },
  VALUATION: {
    key: "VALUATION",
    title: "Chief Valuation Strategist Agent",
    description: "30-Year Senior Valuation Strategist. Calculates Multi-Stage DCF Fair Value, Historical PE/PB bands, EV/EBITDA multiples, and Margin of Safety.",
    tools: ["MultiStageDCFCalculator", "HistoricalPEPB_BandAnalyzer", "EVtoEBITDAMultipleModel", "MarginOfSafetyCalculator", "SumOfThePartsValuer", "ReverseDCFMarketExpectationEngine"]
  },
  EVENT_IMPACT: {
    key: "EVENT_IMPACT",
    title: "Catalyst & Macro Event Analyst Agent",
    description: "30-Year High-Impact Event Trader. Quantifies market impact of Union Budget policy shifts, RBI rate decisions, crude shocks, and geopolitical events.",
    tools: ["UnionBudgetImpactModel", "RBIPolicySurprisePredictor", "GeopoliticalShockEvaluator", "EventVolatilityPricer", "RegulatoryChangeImpactModel", "EarningsEventStraddleModel"]
  },
  WATCHLIST: {
    key: "WATCHLIST",
    title: "Watchlist Conviction Strategist Agent",
    description: "30-Year Conviction Strategist. Ranks high-probability stocks approaching optimal institutional buy zones.",
    tools: ["BuyZoneProximitySystem", "ConvictionScoreRanker", "CatalystCountdownTracker", "RiskRewardFilter", "LiquidityGatekeeper", "EntryTriggerNotifier"]
  },
  LEARNING_PERFORMANCE: {
    key: "LEARNING_PERFORMANCE",
    title: "Quant Performance Auditor Agent",
    description: "30-Year Quantitative Performance Auditor. Evaluates trade journal win-rates, Sharpe/Sortino ratios, loss attribution, and optimizes strategy weights.",
    tools: ["TradeJournalAuditor", "WinRateExpectancyCalculator", "SharpeSortinoRatioModel", "LossAttributionAnalyzer", "SystemWeightOptimizer", "BehavioralBiasDetector"]
  },
  MEMORY_MANAGEMENT: {
    key: "MEMORY_MANAGEMENT",
    title: "Knowledge Base & Memory Curator Agent",
    description: "30-Year Historical Pattern Curator. Maintains vector memory, updates Markdown Knowledge Base, and retrieves historical market regime matches.",
    tools: ["KnowledgeBaseUpdater", "MarkdownFormatter", "VectorMemorySearchEngine", "HistoricalPatternRetriever", "CaseStudyArchiver", "RegimeMemorySynchronizer"]
  },
  DAILY_BRIEFING: {
    key: "DAILY_BRIEFING",
    title: "Pre & Post Market Briefing Head Agent",
    description: "30-Year Senior Market Editor. Generates daily pre-market institutional battle-plans and post-market reconciliation summaries.",
    tools: ["PreMarketBriefingGenerator", "PostMarketReconciliationWriter", "KeyLevelNotifier", "GlobalMarketsOvernightTracker", "InstitutionalDeskSummaryBuilder", "RiskAlertSynthesizer"]
  },
  WEEKLY_REVIEW: {
    key: "WEEKLY_REVIEW",
    title: "Weekly Performance Strategist Agent",
    description: "30-Year Weekly Performance Strategist. Synthesizes weekly portfolio performance, sectoral trends, closed positions, and risk adjustments.",
    tools: ["WeeklyPerformanceSynthesizer", "SectorRelativeStrengthReviewer", "ClosedTradeAuditor", "PositionAdjustmentPlanner", "MacroShiftEvaluator", "ExecutiveWeeklyBriefingGenerator"]
  },
  MONTHLY_STRATEGY: {
    key: "MONTHLY_STRATEGY",
    title: "Chief Investment Strategist Agent",
    description: "30-Year Chief Investment Strategist. Formulates monthly macroeconomic allocation shifts, megatrend themes, and long-term theses.",
    tools: ["MonthlyMacroOutlookSynthesizer", "AssetAllocationShiftPlanner", "LongTermThesisAuditor", "GlobalLiquidityCycleModel", "MegatrendThemeExtractor", "StrategicRebalanceEngine"]
  },
  SWING_TRADE_EXPERT: {
    key: "SWING_TRADE_EXPERT",
    title: "Senior Swing & Breakout Execution Specialist",
    description: "30-Year Master Swing Trader. Identifies high-conviction swing setups with exact entry ranges, target prices, hard stop-losses, and >1:2.5 R:R ratios.",
    tools: ["HighVelocitySwingScanner", "BreakoutVolumeSurgeAnalyzer", "PrecisionEntryStopTargetCalculator", "RiskRewardRatioFilter", "TrailingStopLossEngine", "ExecutionSlippageOptimizer"]
  },
  TIMING_CATALYST_AGENT: {
    key: "TIMING_CATALYST_AGENT",
    title: "Precision Timing & Catalyst Agent",
    description: "30-Year Timing Precision Specialist. Pinpoints why to trade right now based on order book spikes, block deal flows, and news triggers.",
    tools: ["ImmediateCatalystDetector", "OrderBookSpikeAnalyzer", "TimingPrecisionEngine", "NewsFlowTriggerScorer", "BlockDealAccelerationTracker", "MomentumContinuationFilter"]
  }
};

module.exports = { AgentRoles };

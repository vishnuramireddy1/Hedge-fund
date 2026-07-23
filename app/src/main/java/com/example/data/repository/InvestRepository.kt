package com.example.data.repository

import com.example.ai.orchestrator.InvestOrchestrator
import com.example.data.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvestRepository(private val db: InvestDatabase) {

    val dao = db.investDao()
    val orchestrator = InvestOrchestrator(dao)

    val allHoldings: Flow<List<PortfolioHolding>> = dao.getAllHoldings()
    val allTradeEntries: Flow<List<TradeJournalEntry>> = dao.getAllTradeEntries()
    val allDailyJournalEntries: Flow<List<DailyJournalEntry>> = dao.getAllDailyJournalEntries()
    val allAgentLogs: Flow<List<AgentLog>> = dao.getAllAgentLogs()
    val allKnowledgeArticles: Flow<List<KnowledgeArticle>> = dao.getAllKnowledgeArticles()
    val watchlist: Flow<List<WatchlistItem>> = dao.getWatchlist()
    val stockQuotes: Flow<List<StockQuote>> = dao.getAllStockQuotes()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        // Seed Stock Quotes (NIFTY 50 + High-Momentum Growth Universe)
        dao.insertStockQuotes(
            listOf(
                StockQuote("RELIANCE", "Reliance Industries Ltd", "NSE", "Energy & Retail", 3120.50, 42.10, 1.37, 3217.90, 2220.30, 26.4, 2110000.0, 4210000, 58.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("TATAMOTORS", "Tata Motors Ltd", "NSE", "Automobile", 985.40, 18.20, 1.88, 1179.00, 593.00, 15.2, 362000.0, 8920000, 62.4, "BULLISH_CROSS", "UPTREND"),
                StockQuote("HDFCBANK", "HDFC Bank Ltd", "NSE", "Banking & Financials", 1640.20, -8.50, -0.51, 1794.00, 1363.50, 18.6, 1250000.0, 11200000, 49.1, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("INFY", "Infosys Ltd", "NSE", "IT Services", 1825.00, 12.30, 0.68, 1978.00, 1355.00, 24.8, 758000.0, 5410000, 54.6, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("LT", "Larsen & Toubro Ltd", "NSE", "Capital Goods & Infra", 3680.00, 54.00, 1.49, 3900.00, 2850.00, 32.1, 506000.0, 2100000, 66.8, "BULLISH_CROSS", "UPTREND"),
                StockQuote("ICICIBANK", "ICICI Bank Ltd", "NSE", "Banking & Financials", 1235.00, 14.50, 1.19, 1258.00, 920.00, 17.8, 865000.0, 7800000, 61.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("BHARTIARTL", "Bharti Airtel Ltd", "NSE", "Telecom", 1450.00, 22.00, 1.54, 1490.00, 840.00, 42.5, 820000.0, 4800000, 65.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("SBIN", "State Bank of India", "NSE", "Banking & Financials", 845.00, 9.20, 1.10, 912.00, 555.00, 10.8, 754000.0, 14200000, 58.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("TCS", "Tata Consultancy Services", "NSE", "IT Services", 4350.00, 38.00, 0.88, 4585.00, 3310.00, 31.2, 1570000.0, 2200000, 53.4, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("KOTAKBANK", "Kotak Mahindra Bank", "NSE", "Banking & Financials", 1780.00, -12.00, -0.67, 1920.00, 1540.00, 21.5, 354000.0, 3100000, 48.0, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("AXISBANK", "Axis Bank Ltd", "NSE", "Banking & Financials", 1180.00, 15.00, 1.29, 1339.00, 930.00, 14.2, 364000.0, 5200000, 59.5, "BULLISH_CROSS", "UPTREND"),
                StockQuote("ITC", "ITC Ltd", "NSE", "FMCG", 495.00, 2.50, 0.51, 520.00, 400.00, 28.2, 618000.0, 10500000, 53.0, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("HINDUNILVR", "Hindustan Unilever Ltd", "NSE", "FMCG", 2680.00, 18.00, 0.68, 2860.00, 2170.00, 56.4, 630000.0, 1800000, 50.2, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("BAJFINANCE", "Bajaj Finance Ltd", "NSE", "NBFC", 6850.00, 95.00, 1.41, 7850.00, 6200.00, 29.8, 423000.0, 1400000, 56.8, "BULLISH_CROSS", "UPTREND"),
                StockQuote("MARUTI", "Maruti Suzuki India", "NSE", "Automobile", 12450.00, 180.00, 1.47, 13680.00, 9600.00, 27.5, 391000.0, 680000, 60.1, "BULLISH_CROSS", "UPTREND"),
                StockQuote("M&M", "Mahindra & Mahindra", "NSE", "Automobile", 2940.00, 62.00, 2.15, 3115.00, 1450.00, 28.0, 365000.0, 3200000, 67.4, "BULLISH_CROSS", "BREAKOUT"),
                StockQuote("SUNPHARMA", "Sun Pharmaceutical Ltd", "NSE", "Pharma", 1720.00, 19.50, 1.15, 1810.00, 1110.00, 36.4, 412000.0, 2900000, 62.1, "BULLISH_CROSS", "UPTREND"),
                StockQuote("TITAN", "Titan Company Ltd", "NSE", "Consumer & Retail", 3420.00, 28.00, 0.83, 3886.00, 2880.00, 82.0, 304000.0, 1100000, 51.5, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("NTPC", "NTPC Ltd", "NSE", "Power & Green Energy", 412.00, 6.80, 1.68, 432.00, 210.00, 18.4, 398000.0, 9500000, 59.8, "NEUTRAL", "UPTREND"),
                StockQuote("POWERGRID", "Power Grid Corp of India", "NSE", "Power Transmission", 342.00, 4.50, 1.33, 366.00, 200.00, 19.2, 318000.0, 8400000, 61.0, "NEUTRAL", "UPTREND"),
                StockQuote("TATASTEEL", "Tata Steel Ltd", "NSE", "Metals & Mining", 168.50, 3.20, 1.94, 184.00, 115.00, 22.1, 210000.0, 28000000, 57.4, "NEUTRAL", "UPTREND"),
                StockQuote("JSWSTEEL", "JSW Steel Ltd", "NSE", "Metals & Mining", 945.00, 16.00, 1.72, 1010.00, 750.00, 25.8, 231000.0, 2900000, 58.9, "BULLISH_CROSS", "UPTREND"),
                StockQuote("ADANIENT", "Adani Enterprises Ltd", "NSE", "Diversified", 3180.00, 72.00, 2.32, 3740.00, 2140.00, 88.0, 362000.0, 2600000, 62.8, "BULLISH_CROSS", "UPTREND"),
                StockQuote("ADANIPORTS", "Adani Ports & SEZ", "NSE", "Ports & Infrastructure", 1480.00, 34.00, 2.35, 1608.00, 750.00, 34.2, 320000.0, 3800000, 64.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("COALINDIA", "Coal India Ltd", "NSE", "Energy & Mining", 512.00, 11.50, 2.30, 542.00, 228.00, 8.4, 315000.0, 11500000, 63.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("ONGC", "Oil & Natural Gas Corp", "NSE", "Energy & Oil", 325.00, 6.20, 1.94, 345.00, 172.00, 8.1, 408000.0, 14500000, 61.5, "NEUTRAL", "UPTREND"),
                StockQuote("ULTRACEMCO", "UltraTech Cement Ltd", "NSE", "Cement & Building", 11400.00, 120.00, 1.06, 12100.00, 8100.00, 44.0, 330000.0, 350000, 56.0, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("GRASIM", "Grasim Industries Ltd", "NSE", "Paints & Materials", 2780.00, 45.00, 1.65, 2920.00, 1810.00, 31.0, 189000.0, 950000, 59.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("CIPLA", "Cipla Ltd", "NSE", "Pharma", 1580.00, 21.00, 1.35, 1660.00, 1150.00, 28.5, 127000.0, 1600000, 60.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("DRREDDY", "Dr Reddy's Laboratories", "NSE", "Pharma", 6880.00, 75.00, 1.10, 7110.00, 5200.00, 22.0, 115000.0, 420000, 57.5, "NEUTRAL", "UPTREND"),
                StockQuote("ASIANPAINT", "Asian Paints Ltd", "NSE", "Paints & Consumer", 2980.00, -15.00, -0.50, 3420.00, 2670.00, 52.0, 285000.0, 1200000, 46.2, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("BRITANNIA", "Britannia Industries", "NSE", "FMCG", 5720.00, 42.00, 0.74, 5980.00, 4350.00, 58.0, 138000.0, 380000, 52.8, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("NESTLEIND", "Nestle India Ltd", "NSE", "FMCG", 2540.00, 12.00, 0.47, 2770.00, 2150.00, 72.0, 245000.0, 520000, 49.5, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("HCLTECH", "HCL Technologies Ltd", "NSE", "IT Services", 1760.00, 24.00, 1.38, 1860.00, 1120.00, 26.5, 477000.0, 2800000, 59.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("TECHM", "Tech Mahindra Ltd", "NSE", "IT Services", 1540.00, 28.00, 1.85, 1620.00, 1080.00, 34.0, 150000.0, 1900000, 61.8, "BULLISH_CROSS", "UPTREND"),
                StockQuote("WIPRO", "Wipro Ltd", "NSE", "IT Services", 535.00, 8.20, 1.56, 580.00, 380.00, 23.5, 280000.0, 5400000, 58.2, "NEUTRAL", "UPTREND"),
                StockQuote("LTIM", "LTIMindtree Ltd", "NSE", "IT Services", 5850.00, 92.00, 1.60, 6400.00, 4500.00, 36.0, 173000.0, 650000, 60.5, "BULLISH_CROSS", "UPTREND"),
                StockQuote("EICHERMOT", "Eicher Motors Ltd", "NSE", "Automobile", 4880.00, 85.00, 1.77, 5100.00, 3300.00, 32.0, 134000.0, 580000, 63.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("HEROMOTOCO", "Hero MotoCorp Ltd", "NSE", "Automobile", 5420.00, 95.00, 1.78, 5890.00, 2900.00, 24.0, 108000.0, 620000, 62.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("BAJAJ-AUTO", "Bajaj Auto Ltd", "NSE", "Automobile", 9850.00, 160.00, 1.65, 10800.00, 4600.00, 33.0, 276000.0, 410000, 64.0, "BULLISH_CROSS", "UPTREND"),
                StockQuote("DIVISLAB", "Divi's Laboratories", "NSE", "Pharma", 4950.00, 82.00, 1.68, 5200.00, 3300.00, 72.0, 131000.0, 480000, 61.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("APOLLOHOSP", "Apollo Hospitals", "NSE", "Healthcare", 6750.00, 110.00, 1.66, 7150.00, 4800.00, 78.0, 97000.0, 320000, 62.5, "BULLISH_CROSS", "UPTREND"),
                StockQuote("TRENT", "Trent Ltd (Westside & Zudio)", "NSE", "Retail & Fashion", 7450.00, 280.00, 3.90, 7800.00, 1950.00, 145.0, 265000.0, 1800000, 78.2, "BULLISH_CROSS", "BREAKOUT"),
                StockQuote("BEL", "Bharat Electronics Ltd", "NSE", "Defense & Tech", 298.00, 7.40, 2.55, 340.00, 125.00, 44.0, 218000.0, 16200000, 67.2, "BULLISH_CROSS", "UPTREND"),
                StockQuote("BPCL", "Bharat Petroleum Corp", "NSE", "Energy & Oil", 348.00, 6.50, 1.90, 375.00, 160.00, 9.2, 151000.0, 9800000, 60.1, "NEUTRAL", "UPTREND"),
                StockQuote("SHRIRAMFIN", "Shriram Finance Ltd", "NSE", "NBFC", 3120.00, 68.00, 2.23, 3300.00, 1800.00, 16.5, 117000.0, 1200000, 65.4, "BULLISH_CROSS", "UPTREND"),
                StockQuote("INDUSINDBK", "IndusInd Bank Ltd", "NSE", "Banking & Financials", 1380.00, 18.00, 1.32, 1690.00, 1200.00, 12.8, 107000.0, 2800000, 54.0, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("HDFCLIFE", "HDFC Life Insurance", "NSE", "Insurance", 715.00, 9.20, 1.30, 750.00, 512.00, 85.0, 154000.0, 3200000, 57.0, "NEUTRAL", "UPTREND"),
                StockQuote("SBILIFE", "SBI Life Insurance", "NSE", "Insurance", 1780.00, 22.00, 1.25, 1860.00, 1260.00, 76.0, 178000.0, 1400000, 58.5, "NEUTRAL", "UPTREND"),
                StockQuote("TATACONSUM", "Tata Consumer Products", "NSE", "FMCG", 1180.00, 15.00, 1.29, 1265.00, 820.00, 82.0, 112000.0, 2100000, 56.8, "NEUTRAL", "UPTREND"),
                
                // High-Momentum Midcap Leaders
                StockQuote("SUZLON", "Suzlon Energy Ltd", "NSE", "Renewable Energy", 64.80, 2.10, 3.35, 86.00, 18.20, 48.0, 88000.0, 45100000, 71.5, "BULLISH_CROSS", "UPTREND"),
                StockQuote("PERSISTENT", "Persistent Systems Ltd", "NSE", "IT Services", 5420.00, 185.00, 3.53, 5600.00, 3200.00, 48.2, 385000.0, 1200000, 74.8, "BULLISH_CROSS", "BREAKOUT"),
                StockQuote("BHEL", "Bharat Heavy Electricals Ltd", "NSE", "Capital Goods & Power", 312.00, 11.40, 3.79, 335.00, 92.00, 52.0, 112000.0, 18500000, 68.4, "BULLISH_CROSS", "BREAKOUT"),
                StockQuote("HAL", "Hindustan Aeronautics Ltd", "NSE", "Defense", 4650.00, 110.00, 2.42, 5480.00, 1750.00, 38.6, 310000.0, 2400000, 63.5, "BULLISH_CROSS", "UPTREND"),
                StockQuote("DIXON", "Dixon Technologies Ltd", "NSE", "EMS / Electronics", 12400.00, -150.00, -1.20, 13800.00, 5100.00, 85.0, 74000.0, 620000, 52.1, "NEUTRAL", "CONSOLIDATION"),
                StockQuote("TATAPOWER", "Tata Power Co Ltd", "NSE", "Power & Renewable", 442.00, 8.60, 1.98, 485.00, 230.00, 36.0, 141000.0, 12400000, 60.5, "BULLISH_CROSS", "UPTREND")
            )
        )

        // Seed Holdings
        dao.insertHolding(
            PortfolioHolding(
                symbol = "RELIANCE",
                name = "Reliance Industries Ltd",
                exchange = "NSE",
                sector = "Energy & Retail",
                quantity = 400,
                avgPrice = 2850.00,
                currentPrice = 3120.50,
                strategyType = StrategyType.LONG_TERM,
                targetPrice = 3500.00,
                stopLoss = 2700.00,
                thesis = "Retail margin expansion, Jio tariff hike flow-through, and New Energy commissioning in FY27.",
                riskScore = 3,
                beta = 0.92,
                moat = "Wide Moat (Telecom & Refining scale)"
            )
        )

        dao.insertHolding(
            PortfolioHolding(
                symbol = "TATAMOTORS",
                name = "Tata Motors Ltd",
                exchange = "NSE",
                sector = "Automobile",
                quantity = 800,
                avgPrice = 820.00,
                currentPrice = 985.40,
                strategyType = StrategyType.SWING,
                targetPrice = 1120.00,
                stopLoss = 920.00,
                thesis = "JLR order book stability + EV market leadership in India (70%+ share). Swing target 1120.",
                riskScore = 5,
                beta = 1.25,
                moat = "Narrow Moat (Brand & EV ecosystem)"
            )
        )

        dao.insertHolding(
            PortfolioHolding(
                symbol = "LT",
                name = "Larsen & Toubro Ltd",
                exchange = "NSE",
                sector = "Capital Goods & Infra",
                quantity = 250,
                avgPrice = 3200.00,
                currentPrice = 3680.00,
                strategyType = StrategyType.LONG_TERM,
                targetPrice = 4200.00,
                stopLoss = 3100.00,
                thesis = "Record international order book ($60B+) + Indian capex cycle acceleration.",
                riskScore = 4,
                beta = 1.05,
                moat = "Wide Moat (EPC scale & execution track record)"
            )
        )

        dao.insertHolding(
            PortfolioHolding(
                symbol = "SUZLON",
                name = "Suzlon Energy Ltd",
                exchange = "NSE",
                sector = "Renewable Energy",
                quantity = 15000,
                avgPrice = 48.00,
                currentPrice = 64.80,
                strategyType = StrategyType.SWING,
                targetPrice = 85.00,
                stopLoss = 54.00,
                thesis = "Turnaround story: Net debt free, 3.8 GW wind order book, strong C&I demand.",
                riskScore = 7,
                beta = 1.65,
                moat = "Narrow Moat (50%+ installed wind base in India)"
            )
        )

        // Seed Trade Journal
        dao.insertTradeEntry(
            TradeJournalEntry(
                symbol = "TATAMOTORS",
                companyName = "Tata Motors Ltd",
                buyDate = "2026-05-10",
                buyPrice = 820.00,
                quantity = 800,
                reason = "Breakout above 800 resistance with 3x average volume. JLR quarterly guidance upgrade.",
                expectedReturnPct = 36.5,
                expectedHoldingDays = 60,
                riskScore = 5,
                confidencePct = 88,
                stopLoss = 760.00,
                target = 1120.00,
                status = "OPEN",
                aiPerformanceScore = 92,
                lessonsLearned = "Entering on retest of 800 level gave an excellent risk-reward ratio."
            )
        )

        dao.insertTradeEntry(
            TradeJournalEntry(
                symbol = "BHARTIARTL",
                companyName = "Bharti Airtel Ltd",
                buyDate = "2026-03-01",
                buyPrice = 1120.00,
                quantity = 500,
                reason = "ARPU growth thesis + Africa business turnaround.",
                expectedReturnPct = 20.0,
                expectedHoldingDays = 45,
                riskScore = 4,
                confidencePct = 90,
                stopLoss = 1050.00,
                target = 1350.00,
                exitPrice = 1342.00,
                exitDate = "2026-04-18",
                actualReturnPct = 19.8,
                status = "TARGET_HIT",
                aiPerformanceScore = 95,
                lessonsLearned = "Target achieved 5 days ahead of schedule as institutional buying surged."
            )
        )

        // Seed Watchlist
        dao.insertWatchlistItem(WatchlistItem("HAL", "Hindustan Aeronautics Ltd", "Defense", 4650.00, 2.40, "Geopolitical defense exports & Tejas Mk1A deliveries.", "Defense capex beneficiary with 5-year revenue visibility.", "BUY"))
        dao.insertWatchlistItem(WatchlistItem("DIXON", "Dixon Technologies Ltd", "EMS / Electronics", 12400.00, -1.20, "Mobile manufacturing PLI scheme expansion.", "Leader in Indian electronics manufacturing.", "WATCH"))

        // Seed Initial Agent Logs
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
        dao.insertAgentLog(AgentLog(agentName = "Chief Investment Officer Agent", timestamp = timeStr, status = "SUCCESS", currentTask = "Portfolio Allocation & Risk Gatekeeping", confidencePct = 94, recentFindings = "Portfolio cash allocation maintained at 34%. Capital Goods & Auto overallocated by +4% relative to baseline Nifty weights."))
        dao.insertAgentLog(AgentLog(agentName = "Market Intelligence Agent", timestamp = timeStr, status = "SUCCESS", currentTask = "NSE 500 Breadth Analysis", confidencePct = 90, recentFindings = "Advance/Decline ratio at 1.84. High institutional buying observed in Capital Goods & Power Transmission."))
        dao.insertAgentLog(AgentLog(agentName = "Trap Detection Agent", timestamp = timeStr, status = "SUCCESS", currentTask = "Promoter Pledge & Accounting Red Flag Scan", confidencePct = 96, recentFindings = "Zero promoter pledge flags in core portfolio holdings. 2 watchlist smallcaps flagged for inventory turnover divergence."))

        // Seed Knowledge Base
        val dateToday = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        dao.insertKnowledgeArticle(
            KnowledgeArticle(
                category = "COMPANIES",
                title = "Reliance Industries — Strategic Deep Dive & Moat Evaluation",
                markdownContent = """
                    # Reliance Industries Ltd (NSE: RELIANCE)
                    ## Business Segments
                    1. **Oil to Chemicals (O2C)**: World's largest refining complex at Jamnagar. Robust gross refining margins (GRM).
                    2. **Jio Platforms**: 470M+ subscribers, leading 5G rollout, digital ecosystem monetization.
                    3. **Reliance Retail**: 18,000+ stores, fast-growing omnichannel presence.
                    4. **Green Energy**: Gigafactories for Solar PV, Green Hydrogen, Storage Batteries commissioning FY27.

                    ## Valuation & Target Thesis
                    - **SOTP Target**: ₹3,500
                    - **Moat**: Wide Moat due to massive scale, vertical integration, and distribution reach across India.
                """.trimIndent(),
                updatedAt = dateToday,
                tags = "Reliance, Energy, Telecom, Retail"
            )
        )

        dao.insertKnowledgeArticle(
            KnowledgeArticle(
                category = "MACRO",
                title = "Indian Economy Macro Outlook — FY27 Growth Trajectory",
                markdownContent = """
                    # Macroeconomic Outlook for India
                    - **GDP Growth**: Forecasted at 6.8% - 7.2% for FY27.
                    - **Inflation (CPI)**: Moderating towards 4.2% RBI target band.
                    - **Capital Expenditure**: Union Budget emphasis on infrastructure, defense, railways, and renewable energy.
                    - **FII/DII Flows**: Strong domestic SIP inflows (₹21,000 Cr+/month) providing robust downside support to NIFTY 50.
                """.trimIndent(),
                updatedAt = dateToday,
                tags = "Macro, GDP, Inflation, RBI, SIP"
            )
        )

        // Seed Daily Journal
        dao.insertDailyJournal(
            DailyJournalEntry(
                date = dateToday,
                marketSummary = "NIFTY 50 closed at 24,141.95 (+0.42%). Capital Goods and Auto sectors led the rally.",
                portfolioSummary = "Portfolio AUM stands at ₹42,85,200 (+1.84% today). Tata Motors and Suzlon contributed major gains.",
                newsSummary = "RBI Monetary Policy Committee held interest rates steady. IIP growth came in at 5.4%.",
                recommendations = "CIO recommends trailing stop loss on Suzlon to ₹58.00 and accumulating Reliance on dips.",
                lessons = "Patience in waiting for technical retests yields superior entry prices.",
                mistakes = "Exited partial Bharti Airtel position slightly early before final target surge.",
                watchlistSymbols = "HAL, DIXON, SIEMENS, TATA POWER"
            )
        )
    }

    suspend fun addHolding(holding: PortfolioHolding) = withContext(Dispatchers.IO) {
        dao.insertHolding(holding)
    }

    suspend fun addTradeEntry(entry: TradeJournalEntry) = withContext(Dispatchers.IO) {
        dao.insertTradeEntry(entry)
    }

    suspend fun addKnowledgeArticle(article: KnowledgeArticle) = withContext(Dispatchers.IO) {
        dao.insertKnowledgeArticle(article)
    }

    suspend fun addWatchlistItem(item: WatchlistItem) = withContext(Dispatchers.IO) {
        dao.insertWatchlistItem(item)
    }
}

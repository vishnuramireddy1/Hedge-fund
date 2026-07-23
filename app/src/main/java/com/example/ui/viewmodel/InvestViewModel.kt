package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.agents.AgentExecutionResult
import com.example.ai.orchestrator.InvestOrchestrator
import com.example.ai.orchestrator.SystemContext
import com.example.data.db.*
import com.example.data.repository.InvestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    DASHBOARD,
    PORTFOLIO,
    CIO_ASSISTANT,
    RESEARCH,
    AGENTS_25,
    JOURNAL,
    KNOWLEDGE,
    SECURITY
}

data class ChatMessage(
    val sender: String, // USER or CIO
    val text: String,
    val timestamp: String,
    val isAgentResult: Boolean = false
)

data class UiState(
    val activeTab: AppTab = AppTab.DASHBOARD,
    val systemContext: SystemContext = SystemContext.getCurrentContext(),
    val isMultiAgentScanning: Boolean = false,
    val isCioThinking: Boolean = false,
    val chatMessages: List<ChatMessage> = emptyList(),
    val scanResults: List<AgentExecutionResult> = emptyList(),
    val totalCashCr: Double = 14.45, // ₹14.45 Lakhs Cash Reserve
    val statusMessage: String? = null,
    val isAppLocked: Boolean = false,
    val securityAuditReport: com.example.security.SecurityAuditReport? = null,
    val lastScanTimestamp: String = "Active (30-Min Cron)",
    val nextScanCountdownMins: Int = 30
)

class InvestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InvestDatabase.getDatabase(application)
    val repository = InvestRepository(db)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val holdings: StateFlow<List<PortfolioHolding>> = repository.allHoldings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tradeEntries: StateFlow<List<TradeJournalEntry>> = repository.allTradeEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyJournals: StateFlow<List<DailyJournalEntry>> = repository.allDailyJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agentLogs: StateFlow<List<AgentLog>> = repository.allAgentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val knowledgeArticles: StateFlow<List<KnowledgeArticle>> = repository.allKnowledgeArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlist: StateFlow<List<WatchlistItem>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockQuotes: StateFlow<List<StockQuote>> = repository.stockQuotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            // Add initial welcome chat message
            _uiState.update { state ->
                state.copy(
                    chatMessages = listOf(
                        ChatMessage(
                            sender = "CIO",
                            text = "Greetings. I am your Chief Investment Officer Agent. I am monitoring the Indian stock market (NSE/BSE) across 27 specialized sub-agents with 30-minute autonomous background scanning. Ask me for immediate swing trades, targeted single-agent scans, or catalysts!",
                            timestamp = SystemContext.getCurrentContext().currentTime
                        )
                    )
                )
            }
            // Start 30-Minute Autonomous Background Scanning Loop
            start30MinScanLoop()
        }
    }

    private fun start30MinScanLoop() {
        viewModelScope.launch {
            while (true) {
                val nowTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.ENGLISH).format(java.util.Date())
                _uiState.update { it.copy(lastScanTimestamp = "Last: $nowTime (30-Min Cron)") }

                // Run autonomous scan in background
                runFullMultiAgentScanInternal()

                // Wait 30 minutes before next scan cycle
                kotlinx.coroutines.delay(30 * 60 * 1000L)
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun refreshSystemContext() {
        _uiState.update { it.copy(systemContext = SystemContext.getCurrentContext()) }
    }

    fun runFullMultiAgentScan() {
        viewModelScope.launch {
            runFullMultiAgentScanInternal()
        }
    }

    private suspend fun runFullMultiAgentScanInternal() {
        _uiState.update { it.copy(isMultiAgentScanning = true, statusMessage = "Executing 27-Agent Autonomous Scan...") }
        try {
            val results = repository.orchestrator.runFullMultiAgentScan()
            val nowTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.ENGLISH).format(java.util.Date())
            _uiState.update { state ->
                state.copy(
                    isMultiAgentScanning = false,
                    scanResults = results,
                    lastScanTimestamp = "Last: $nowTime (30-Min Cron)",
                    statusMessage = "Multi-Agent Scan Complete. All 27 Agents Synchronized."
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isMultiAgentScanning = false, statusMessage = "Scan Note: ${e.localizedMessage}") }
        }
    }

    fun runSingleAgentScan(role: com.example.ai.agents.AgentRole, taskDesc: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(statusMessage = "Executing Single-Agent Scan: ${role.title}...") }
            try {
                repository.orchestrator.runTargetedSingleAgentScan(role, taskDesc)
                _uiState.update { it.copy(statusMessage = "Single-Agent Scan Finished: ${role.title}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(statusMessage = "Single Scan Note: ${e.localizedMessage}") }
            }
        }
    }

    fun sendCioMessage(userQuery: String) {
        val sanitizedQuery = com.example.security.SecurityManager.sanitizeInput(userQuery)
        if (sanitizedQuery.isBlank()) return

        val nowTime = SystemContext.getCurrentContext().currentTime
        val userMsg = ChatMessage(sender = "USER", text = sanitizedQuery, timestamp = nowTime)

        var currentHistory: List<ChatMessage> = emptyList()
        _uiState.update { state ->
            val updatedList = state.chatMessages + userMsg
            currentHistory = updatedList
            state.copy(
                chatMessages = updatedList,
                isCioThinking = true
            )
        }

        viewModelScope.launch {
            try {
                val currentHoldingsList = holdings.value
                val cioResponse = repository.orchestrator.queryCioAssistant(
                    userQuery = sanitizedQuery,
                    holdings = currentHoldingsList,
                    chatHistory = currentHistory
                )

                val cioMsg = ChatMessage(sender = "CIO", text = cioResponse, timestamp = SystemContext.getCurrentContext().currentTime)

                _uiState.update { state ->
                    state.copy(
                        chatMessages = state.chatMessages + cioMsg,
                        isCioThinking = false
                    )
                }
            } catch (e: Exception) {
                val cioErrorMsg = ChatMessage(sender = "CIO", text = "Error querying CIO Agent: ${e.localizedMessage}", timestamp = SystemContext.getCurrentContext().currentTime)
                _uiState.update { state ->
                    state.copy(
                        chatMessages = state.chatMessages + cioErrorMsg,
                        isCioThinking = false
                    )
                }
            }
        }
    }

    fun addTradeEntry(symbol: String, buyPrice: Double, qty: Int, reason: String, target: Double, stopLoss: Double) {
        viewModelScope.launch {
            val sanitizedSymbol = com.example.security.SecurityManager.sanitizeInput(symbol).uppercase()
            val sanitizedReason = com.example.security.SecurityManager.sanitizeInput(reason)

            val entry = TradeJournalEntry(
                symbol = sanitizedSymbol,
                companyName = sanitizedSymbol,
                buyDate = SystemContext.getCurrentContext().currentDate,
                buyPrice = buyPrice,
                quantity = qty,
                reason = sanitizedReason,
                expectedReturnPct = ((target - buyPrice) / buyPrice) * 100,
                expectedHoldingDays = 30,
                riskScore = 5,
                confidencePct = 85,
                stopLoss = stopLoss,
                target = target,
                status = "OPEN"
            )
            repository.addTradeEntry(entry)
            _uiState.update { it.copy(statusMessage = "Trade for $sanitizedSymbol recorded in Trade Journal.") }
        }
    }

    fun addHolding(symbol: String, companyName: String, sector: String, qty: Int, price: Double, strategy: StrategyType, target: Double, stopLoss: Double, thesis: String) {
        viewModelScope.launch {
            val sanitizedSymbol = com.example.security.SecurityManager.sanitizeInput(symbol).uppercase()
            val sanitizedName = com.example.security.SecurityManager.sanitizeInput(companyName)
            val sanitizedThesis = com.example.security.SecurityManager.sanitizeInput(thesis)

            val holding = PortfolioHolding(
                symbol = sanitizedSymbol,
                name = sanitizedName,
                sector = sector,
                quantity = qty,
                avgPrice = price,
                currentPrice = price,
                strategyType = strategy,
                targetPrice = target,
                stopLoss = stopLoss,
                thesis = sanitizedThesis,
                riskScore = 4
            )
            repository.addHolding(holding)
            _uiState.update { it.copy(statusMessage = "Added $sanitizedSymbol to Portfolio ($strategy).") }
        }
    }

    fun refreshSecurityAudit() {
        val app = getApplication<Application>()
        val report = com.example.security.SecurityManager.runSecurityAudit(app)
        val isLocked = com.example.security.SecurityManager.isAppLockEnabled(app)
        _uiState.update { it.copy(securityAuditReport = report, isAppLocked = isLocked) }
    }

    fun unlockAppWithPin(inputPin: String): Boolean {
        val app = getApplication<Application>()
        val isValid = com.example.security.SecurityManager.verifyPin(app, inputPin)
        if (isValid) {
            _uiState.update { it.copy(isAppLocked = false) }
        }
        return isValid
    }

    fun setSecurityPin(newPin: String) {
        val app = getApplication<Application>()
        com.example.security.SecurityManager.setAppPin(app, newPin)
        refreshSecurityAudit()
        _uiState.update { it.copy(statusMessage = "App PIN successfully configured and PBKDF2 encrypted.") }
    }

    fun toggleAppLock(enable: Boolean) {
        val app = getApplication<Application>()
        com.example.security.SecurityManager.setAppLockEnabled(app, enable)
        refreshSecurityAudit()
    }

    fun toggleFlagSecure(enable: Boolean) {
        val app = getApplication<Application>()
        com.example.security.SecurityManager.setFlagSecureEnabled(app, enable)
        refreshSecurityAudit()
    }
}

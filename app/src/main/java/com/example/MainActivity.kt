package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.security.SecurityManager
import com.example.ui.components.AppLockOverlay
import com.example.ui.components.MarketTickerBar
import com.example.ui.components.TerminalBottomNavBar
import com.example.ui.components.TerminalHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TerminalBackground
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.InvestViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: InvestViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val holdings by viewModel.holdings.collectAsStateWithLifecycle()
        val stockQuotes by viewModel.stockQuotes.collectAsStateWithLifecycle()
        val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
        val agentLogs by viewModel.agentLogs.collectAsStateWithLifecycle()
        val tradeEntries by viewModel.tradeEntries.collectAsStateWithLifecycle()
        val dailyJournals by viewModel.dailyJournals.collectAsStateWithLifecycle()
        val knowledgeArticles by viewModel.knowledgeArticles.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
          viewModel.refreshSecurityAudit()
        }

        // Apply Window FLAG_SECURE dynamically for prod security
        LaunchedEffect(uiState.securityAuditReport?.isFlagSecureEnabled) {
          val isFlagSecure = uiState.securityAuditReport?.isFlagSecureEnabled ?: false
          SecurityManager.applyWindowSecurity(this@MainActivity, isFlagSecure)
        }

        if (uiState.isAppLocked) {
          AppLockOverlay(
            onUnlock = { pin -> viewModel.unlockAppWithPin(pin) }
          )
        } else {
          Scaffold(
            modifier = Modifier
              .fillMaxSize()
              .background(TerminalBackground),
            bottomBar = {
              TerminalBottomNavBar(
                activeTab = uiState.activeTab,
                onTabSelected = { viewModel.selectTab(it) }
              )
            }
          ) { innerPadding ->
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TerminalBackground)
            ) {
              // Live Header Bar
              TerminalHeader(
                systemContext = uiState.systemContext,
                isScanning = uiState.isMultiAgentScanning,
                onRunMultiAgentScan = { viewModel.runFullMultiAgentScan() }
              )

              // Live Indices Ticker Bar
              MarketTickerBar(stockQuotes = stockQuotes)

              // Main Active View Container
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
              ) {
                when (uiState.activeTab) {
                  AppTab.DASHBOARD -> DashboardScreen(
                    holdings = holdings,
                    stockQuotes = stockQuotes,
                    isScanning = uiState.isMultiAgentScanning,
                    onRunScan = { viewModel.runFullMultiAgentScan() },
                    onNavigateTab = { viewModel.selectTab(it) }
                  )

                  AppTab.PORTFOLIO -> PortfolioScreen(
                    holdings = holdings,
                    onAddHolding = { sym, name, sec, qty, price, strat, tgt, stop, thesis ->
                      viewModel.addHolding(sym, name, sec, qty, price, strat, tgt, stop, thesis)
                    }
                  )

                  AppTab.CIO_ASSISTANT -> AssistantScreen(
                    chatMessages = uiState.chatMessages,
                    isThinking = uiState.isCioThinking,
                    systemContext = uiState.systemContext,
                    onSendMessage = { viewModel.sendCioMessage(it) }
                  )

                  AppTab.RESEARCH -> ResearchScreen(
                    stockQuotes = stockQuotes,
                    watchlist = watchlist
                  )

                  AppTab.AGENTS_25 -> AgentsScreen(
                    agentLogs = agentLogs,
                    isScanning = uiState.isMultiAgentScanning,
                    onRunScan = { viewModel.runFullMultiAgentScan() }
                  )

                  AppTab.JOURNAL -> JournalScreen(
                    tradeEntries = tradeEntries,
                    dailyJournals = dailyJournals,
                    onAddTrade = { sym, price, qty, reason, tgt, stop ->
                      viewModel.addTradeEntry(sym, price, qty, reason, tgt, stop)
                    }
                  )

                  AppTab.KNOWLEDGE -> KnowledgeScreen(
                    articles = knowledgeArticles,
                    onAddArticle = { /* Save custom knowledge article */ }
                  )

                  AppTab.SECURITY -> SecurityScreen(
                    auditReport = uiState.securityAuditReport,
                    onRefreshAudit = { viewModel.refreshSecurityAudit() },
                    onSetPin = { pin -> viewModel.setSecurityPin(pin) },
                    onToggleAppLock = { enable -> viewModel.toggleAppLock(enable) },
                    onToggleFlagSecure = { enable -> viewModel.toggleFlagSecure(enable) }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}


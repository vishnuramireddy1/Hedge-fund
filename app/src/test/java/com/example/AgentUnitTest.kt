package com.example

import com.example.ai.agents.AgentPromptTemplates
import com.example.ai.agents.AgentRole
import com.example.ai.gemini.GeminiApiClient
import com.example.ai.orchestrator.SystemContext
import org.junit.Assert.*
import org.junit.Test

class AgentUnitTest {

    @Test
    fun `verify all 27 agent roles exist with valid metadata`() {
        val roles = AgentRole.values()
        assertEquals(27, roles.size)

        for (role in roles) {
            assertNotNull("Title should not be null for ${role.name}", role.title)
            assertTrue("Title should not be empty for ${role.name}", role.title.isNotBlank())

            assertNotNull("Category should not be null for ${role.name}", role.category)
            assertTrue("Category should not be empty for ${role.name}", role.category.isNotBlank())

            assertNotNull("Description should not be null for ${role.name}", role.description)
            assertTrue("Description should not be empty for ${role.name}", role.description.isNotBlank())

            assertNotNull("Tools list should not be null for ${role.name}", role.tools)
            assertTrue("Tools list should contain tools for ${role.name}", role.tools.isNotEmpty())
        }
    }

    @Test
    fun `verify agent prompt generation for all 27 agent roles`() {
        val systemContext = SystemContext.getCurrentContext()
        val header = systemContext.toFormattedPromptHeader()

        for (role in AgentRole.values()) {
            val taskDesc = "Perform institutional research scan for ${role.title}"
            val prompt = AgentPromptTemplates.buildPragmaticPrompt(role, taskDesc, header)

            assertNotNull("Prompt should not be null for ${role.name}", prompt)
            assertTrue("Prompt should contain role title for ${role.name}", prompt.contains(role.title))
            assertTrue("Prompt should contain task description for ${role.name}", prompt.contains(taskDesc))
            assertTrue("Prompt should contain system directives", prompt.contains("MANDATORY NUMERICAL RIGOR"))
            assertTrue("Prompt should contain risk-first mandate", prompt.contains("RISK-FIRST MANDATE"))
        }
    }

    @Test
    fun `verify local agent synthesis responses for all intent types`() {
        // 1. Architecture / Scan query
        val archResp = GeminiApiClient.generateLocalAgentSynthesis("Explain 27 agent hierarchy and trigger re-scan")
        assertTrue(archResp.contains("27-AGENT MULTI-AGENT ARCHITECTURE OVERVIEW"))
        assertTrue(archResp.contains("Autonomous Research Desks"))

        // 2. Market hours / schedule query
        val timingResp = GeminiApiClient.generateLocalAgentSynthesis("When does market open and schedule?")
        assertTrue(timingResp.contains("INSTITUTIONAL TRADING SCHEDULE"))

        // 3. Order count / stats query
        val orderResp = GeminiApiClient.generateLocalAgentSynthesis("How many buy order and total order executed?")
        assertTrue(orderResp.contains("PORTFOLIO ORDER RECONCILIATION"))

        // 4. Tata Motors Risk & Stop Loss query
        val tataResp = GeminiApiClient.generateLocalAgentSynthesis("What is Tata Motors stop loss and risk?")
        assertTrue(tataResp.contains("TATAMOTORS"))
        assertTrue(tataResp.contains("₹935.00"))

        // 5. Suzlon Energy Risk & Stop Loss query
        val suzlonResp = GeminiApiClient.generateLocalAgentSynthesis("What is Suzlon stop loss and target?")
        assertTrue(suzlonResp.contains("SUZLON"))
        assertTrue(suzlonResp.contains("₹57.50"))

        // 6. Persistent Systems Risk query
        val persistentResp = GeminiApiClient.generateLocalAgentSynthesis("Persistent stop loss level?")
        assertTrue(persistentResp.contains("PERSISTENT"))
        assertTrue(persistentResp.contains("₹5,120.00"))

        // 7. Bharti Airtel research query
        val airtelResp = GeminiApiClient.generateLocalAgentSynthesis("Bharti airtel analysis")
        assertTrue(airtelResp.contains("Bharti Airtel"))

        // 8. BHEL capex research query
        val bhelResp = GeminiApiClient.generateLocalAgentSynthesis("BHEL order book and capex")
        assertTrue(bhelResp.contains("BHEL"))

        // 9. Nifty index query
        val niftyResp = GeminiApiClient.generateLocalAgentSynthesis("What is nifty index level?")
        assertTrue(niftyResp.contains("NIFTY 50"))

        // 10. Market crash / drop query
        val dropResp = GeminiApiClient.generateLocalAgentSynthesis("Market drop crash concern")
        assertTrue(dropResp.contains("MACRO VOLATILITY & CAPITAL PROTECTION STRATEGY"))

        // 11. Casual greeting / energetic query
        val casualResp = GeminiApiClient.generateLocalAgentSynthesis("hello let's go friend")
        assertTrue(casualResp.contains("HIGH-CONVICTION INSTITUTIONAL ALPHA OPPORTUNITIES"))

        // 12. General fallback query
        val fallbackResp = GeminiApiClient.generateLocalAgentSynthesis("General inquiry about portfolio")
        assertTrue(fallbackResp.contains("Chief Investment Assistant"))
    }

    @Test
    fun `verify system context state calculations`() {
        val context = SystemContext.getCurrentContext()
        assertNotNull(context.currentTime)
        assertNotNull(context.currentDate)
        assertNotNull(context.marketStatus)
        assertNotNull(context.marketSession)
        assertNotNull(context.marketTimingCountdown)

        val promptHeader = context.toFormattedPromptHeader()
        assertTrue(promptHeader.contains("BHARAT INVEST OS"))
        assertTrue(promptHeader.contains("NSE / BSE (IST)"))
    }

    @Test
    fun `verify CIO AI agent metadata and prompt schema`() {
        val cioRole = AgentRole.CIO
        assertEquals("Chief Investment Officer Agent", cioRole.title)
        assertEquals("Core Executive", cioRole.category)
        assertTrue(cioRole.description.contains("Final decision maker"))

        val context = SystemContext.getCurrentContext()
        val header = context.toFormattedPromptHeader()
        val cioPrompt = AgentPromptTemplates.buildPragmaticPrompt(
            cioRole,
            "Synthesize research from 27 agents",
            header
        )

        assertTrue(cioPrompt.contains("Chief Investment Officer (CIO)"))
        assertTrue(cioPrompt.contains("Executive Summary & Market Regimes State"))
        assertTrue(cioPrompt.contains("Top 1-3 High-Conviction Alpha Trades"))
        assertTrue(cioPrompt.contains("Key Risk Factors & Invalidation Boundaries"))
        assertTrue(cioPrompt.contains("Portfolio Capital Allocation % Guidance"))
    }

    @Test
    fun `verify CIO AI assistant prompt context building and rules`() {
        // Test CIO AI prompt constraints:
        // 1. Direct Start Rule (No boilerplate banners)
        // 2. Persona Rule (Quantitative precision, Senior MD voice)
        // 3. Trade Execution Schema (Entry, Target, Stop Loss, R:R >= 1:2.5)

        val sampleQuery = "What is our swing trade position on Tata Motors?"
        val localResp = GeminiApiClient.generateLocalAgentSynthesis(sampleQuery, historyText = "tata motors entry")

        assertFalse("CIO AI response must not start with boilerplate title banner", localResp.startsWith("GOLDMAN SACHS"))
        assertTrue("CIO AI response must contain stock symbol", localResp.contains("TATAMOTORS") || localResp.contains("TATA MOTORS"))
        assertTrue("CIO AI response must state execution entry", localResp.contains("Entry"))
        assertTrue("CIO AI response must state target price", localResp.contains("Target"))
        assertTrue("CIO AI response must state hard stop loss", localResp.contains("Stop Loss") || localResp.contains("Stop-Loss"))
    }
}


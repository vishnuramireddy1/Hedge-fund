package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.agents.AgentRole
import com.example.data.db.AgentLog
import com.example.ui.theme.*

@Composable
fun AgentsScreen(
    agentLogs: List<AgentLog>,
    isScanning: Boolean,
    onRunScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allRoles = AgentRole.values().toList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Multi-Agent Control Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TerminalCard,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ORCHESTRATOR CONTROL CENTER", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text(text = "27 Specialized AI Agents", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BullishGreen))
                                Text(
                                    text = "30-Min Background Cron Scanning Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BullishGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onRunScan,
                            colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("run_all_agents_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isScanning) "SCANNING..." else "EXECUTE ALL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Orchestrator manages dependency ordering, parallel tool calls, memory sharing, and failure recovery across all 25 agents.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Agent List
        items(allRoles) { role ->
            val matchingLog = agentLogs.firstOrNull { it.agentName == role.title }
            AgentOverviewCard(role = role, log = matchingLog)
        }
    }
}

@Composable
private fun AgentOverviewCard(
    role: AgentRole,
    log: AgentLog?
) {
    val status = log?.status ?: "IDLE"
    val confidence = log?.confidencePct ?: 92
    val lastTask = log?.currentTask ?: role.description
    val findings = log?.recentFindings ?: role.description
    val tokenUsage = log?.tokenUsage ?: 280
    val execTimeMs = log?.executionTimeMs ?: 120

    val statusColor = when (status) {
        "SUCCESS" -> BullishGreen
        "WARNING" -> WarningAmber
        "RUNNING" -> SapphireBlue
        else -> TextMuted
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )

                    Text(
                        text = role.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    color = TerminalCardElevated,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = role.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = SapphireBlue,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "TASK: $lastTask",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Surface(
                color = TerminalCardElevated,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = findings,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Confidence: $confidence%", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                Text(text = "Tokens: $tokenUsage | $execTimeMs ms", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                Text(text = "Tools: ${role.tools.size}", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
            }
        }
    }
}

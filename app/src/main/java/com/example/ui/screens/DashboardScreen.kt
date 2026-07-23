package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PortfolioHolding
import com.example.data.db.StockQuote
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun DashboardScreen(
    holdings: List<PortfolioHolding>,
    stockQuotes: List<StockQuote>,
    isScanning: Boolean,
    onRunScan: () -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate AUM metrics
    val investedValue = holdings.sumOf { it.avgPrice * it.quantity }
    val currentValue = holdings.sumOf { it.currentPrice * it.quantity }
    val cashReserve = 1445000.0 // ₹14.45 Lakhs Cash
    val totalAum = currentValue + cashReserve
    val dayGain = holdings.sumOf { (it.currentPrice - it.avgPrice) * it.quantity * 0.15 } // Estimated day gain
    val dayGainPct = (dayGain / totalAum) * 100

    val swingTotal = holdings.filter { it.strategyType.name == "SWING" }.sumOf { it.currentPrice * it.quantity }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Portfolio Highlight Card
        item {
            PortfolioHighlightCard(
                totalAum = totalAum,
                dayGain = dayGain,
                dayGainPct = dayGainPct,
                swingAllocation = swingTotal,
                cashReserve = cashReserve
            )
        }

        // CIO Insight Banner Card
        item {
            CioInsightBannerCard(
                isScanning = isScanning,
                onRunScan = onRunScan,
                onReviewThesis = { onNavigateTab(AppTab.CIO_ASSISTANT) }
            )
        }

        // Agent Processing Layer Status Grid
        item {
            AgentProcessingLayerCard(
                onViewAgents = { onNavigateTab(AppTab.AGENTS_25) }
            )
        }

        // Sector Momentum Radar
        item {
            SectorMomentumCard(
                onViewResearch = { onNavigateTab(AppTab.RESEARCH) }
            )
        }

        // Active Holdings Quick View
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE PORTFOLIO POSITIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "VIEW ALL (${holdings.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = SapphireBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateTab(AppTab.PORTFOLIO) }
                )
            }
        }

        items(holdings.take(3).size) { idx ->
            val h = holdings[idx]
            HoldingQuickCard(holding = h, onClick = { onNavigateTab(AppTab.PORTFOLIO) })
        }
    }
}

@Composable
private fun PortfolioHighlightCard(
    totalAum: Double,
    dayGain: Double,
    dayGainPct: Double,
    swingAllocation: Double,
    cashReserve: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PORTFOLIO AUM",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Surface(
                    color = BullishGreenGlow,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BullishGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "+${String.format("%.2f", dayGainPct)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = BullishGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "₹${String.format("%,.0f", totalAum)}",
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+₹${String.format("%,.0f", dayGain)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = BullishGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Divider(color = TerminalBorder, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "SWING ALLOCATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "₹${String.format("%.2f", swingAllocation / 100000)}L (${((swingAllocation / totalAum) * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "AVAILABLE CASH",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "₹${String.format("%.2f", cashReserve / 100000)}L (${((cashReserve / totalAum) * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CioInsightBannerCard(
    isScanning: Boolean,
    onRunScan: () -> Unit,
    onReviewThesis: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SapphireBlueGlow,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SapphireBlue.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(SapphireBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextContrast,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }

                Text(
                    text = "CHIEF INVESTMENT OFFICER INSIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    color = SapphireBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "Sector rotation detected in Renewable Energy & Auto. Technical Agent reports a 15% upside breakout in Suzlon Energy against strong order book fundamentals.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onReviewThesis,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("review_thesis_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = TerminalBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "REVIEW FULL THESIS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRunScan,
                    modifier = Modifier.testTag("run_scan_banner_button"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SapphireBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isScanning) "SCANNING..." else "TRIGGER 25-SCAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = SapphireBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentProcessingLayerCard(
    onViewAgents: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AGENT PROCESSING LAYER",
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = "22/25 ACTIVE",
                style = MaterialTheme.typography.labelSmall,
                color = SapphireBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onViewAgents() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AgentStatusCardItem(
                agentName = "Market Intelligence",
                task = "Scanning NSE 500",
                statusColor = BullishGreen,
                modifier = Modifier.weight(1f)
            )

            AgentStatusCardItem(
                agentName = "Macro Economy",
                task = "RBI Policy Analysis",
                statusColor = WarningAmber,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AgentStatusCardItem(
    agentName: String,
    task: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = TerminalCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Column {
                Text(
                    text = agentName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = task,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectorMomentumCard(
    onViewResearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SECTOR RELATIVE STRENGTH",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "HEATMAP",
                    style = MaterialTheme.typography.labelSmall,
                    color = SapphireBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewResearch() }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectorTag(name = "Auto", change = "+2.4%", isPositive = true, modifier = Modifier.weight(1f))
                SectorTag(name = "Cap Goods", change = "+1.8%", isPositive = true, modifier = Modifier.weight(1f))
                SectorTag(name = "Energy", change = "+1.2%", isPositive = true, modifier = Modifier.weight(1f))
                SectorTag(name = "Banking", change = "-0.4%", isPositive = false, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SectorTag(
    name: String,
    change: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = if (isPositive) BullishGreenGlow else BearishRedGlow,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPositive) BullishGreen.copy(alpha = 0.3f) else BearishRed.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = change,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPositive) BullishGreen else BearishRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HoldingQuickCard(
    holding: PortfolioHolding,
    onClick: () -> Unit
) {
    val gainLoss = (holding.currentPrice - holding.avgPrice) * holding.quantity
    val gainLossPct = ((holding.currentPrice - holding.avgPrice) / holding.avgPrice) * 100
    val isPositive = gainLoss >= 0

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = TerminalCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = holding.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = SapphireBlueGlow,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = holding.strategyType.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = SapphireBlue,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = "${holding.quantity} Qty @ ₹${holding.avgPrice} • ${holding.sector}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%,.2f", holding.currentPrice * holding.quantity)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = "${if (isPositive) "+" else ""}₹${String.format("%,.0f", gainLoss)} (${if (isPositive) "+" else ""}${String.format("%.1f", gainLossPct)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPositive) BullishGreen else BearishRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

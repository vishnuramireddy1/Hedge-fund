package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PortfolioHolding
import com.example.data.db.StrategyType
import com.example.ui.theme.*

@Composable
fun PortfolioScreen(
    holdings: List<PortfolioHolding>,
    onAddHolding: (String, String, String, Int, Double, StrategyType, Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStrategyFilter by remember { mutableStateOf<StrategyType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredHoldings = when (selectedStrategyFilter) {
        StrategyType.SWING -> holdings.filter { it.strategyType == StrategyType.SWING }
        StrategyType.LONG_TERM -> holdings.filter { it.strategyType == StrategyType.LONG_TERM }
        null -> holdings
    }

    val totalInvested = holdings.sumOf { it.avgPrice * it.quantity }
    val totalCurrent = holdings.sumOf { it.currentPrice * it.quantity }
    val totalUnrealizedGain = totalCurrent - totalInvested
    val totalUnrealizedPct = if (totalInvested > 0) (totalUnrealizedGain / totalInvested) * 100 else 0.0

    val weightedBeta = if (totalCurrent > 0) {
        holdings.sumOf { (it.currentPrice * it.quantity / totalCurrent) * it.beta }
    } else 1.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Analytics Summary Header Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TerminalCard,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PORTFOLIO ANALYTICS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = SapphireBlue,
                            contentColor = TextContrast,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("add_position_fab")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Position")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "CURRENT VALUE", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                            Text(text = "₹${String.format("%,.0f", totalCurrent)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "UNREALIZED P&L", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                            Text(
                                text = "${if (totalUnrealizedGain >= 0) "+" else ""}₹${String.format("%,.0f", totalUnrealizedGain)} (${String.format("%.1f", totalUnrealizedPct)}%)",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (totalUnrealizedGain >= 0) BullishGreen else BearishRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Divider(color = TerminalBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Invested: ₹${String.format("%,.0f", totalInvested)}", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Text(text = "Portfolio Beta: ${String.format("%.2f", weightedBeta)}", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    }
                }
            }
        }

        // Strategy Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StrategyFilterChip(
                    label = "ALL POSITIONS (${holdings.size})",
                    isSelected = selectedStrategyFilter == null,
                    onClick = { selectedStrategyFilter = null },
                    modifier = Modifier.weight(1f)
                )
                StrategyFilterChip(
                    label = "SWING (${holdings.count { it.strategyType == StrategyType.SWING }})",
                    isSelected = selectedStrategyFilter == StrategyType.SWING,
                    onClick = { selectedStrategyFilter = StrategyType.SWING },
                    modifier = Modifier.weight(1f)
                )
                StrategyFilterChip(
                    label = "LONG TERM (${holdings.count { it.strategyType == StrategyType.LONG_TERM }})",
                    isSelected = selectedStrategyFilter == StrategyType.LONG_TERM,
                    onClick = { selectedStrategyFilter = StrategyType.LONG_TERM },
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Holdings List
        items(filteredHoldings) { holding ->
            PortfolioHoldingDetailedCard(holding = holding)
        }
    }

    if (showAddDialog) {
        AddPositionDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { symbol, name, sector, qty, price, strategy, target, stopLoss, thesis ->
                onAddHolding(symbol, name, sector, qty, price, strategy, target, stopLoss, thesis)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StrategyFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = if (isSelected) SapphireBlue else TerminalCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SapphireBlue else TerminalBorder)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TextContrast else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PortfolioHoldingDetailedCard(holding: PortfolioHolding) {
    val gainLoss = (holding.currentPrice - holding.avgPrice) * holding.quantity
    val gainLossPct = ((holding.currentPrice - holding.avgPrice) / holding.avgPrice) * 100
    val isPositive = gainLoss >= 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TerminalCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            color = if (holding.strategyType == StrategyType.SWING) SapphireBlueGlow else BullishGreenGlow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = holding.strategyType.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (holding.strategyType == StrategyType.SWING) SapphireBlue else BullishGreen,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${holding.name} • ${holding.sector}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${String.format("%,.0f", holding.currentPrice * holding.quantity)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}₹${String.format("%,.0f", gainLoss)} (${if (isPositive) "+" else ""}${String.format("%.1f", gainLossPct)}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPositive) BullishGreen else BearishRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(color = TerminalBorder, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Avg: ₹${holding.avgPrice} | LTP: ₹${holding.currentPrice}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text(text = "Target: ₹${holding.targetPrice} | Stop: ₹${holding.stopLoss}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }

            Surface(
                color = TerminalCardElevated,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "THESIS: ${holding.thesis}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun AddPositionDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int, Double, StrategyType, Double, Double, String) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("Automobile") }
    var qty by remember { mutableStateOf("100") }
    var price by remember { mutableStateOf("500.0") }
    var isSwing by remember { mutableStateOf(true) }
    var target by remember { mutableStateOf("600.0") }
    var stopLoss by remember { mutableStateOf("450.0") }
    var thesis by remember { mutableStateOf("High relative strength breakout on NSE daily chart.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TerminalCard,
        titleContentColor = TextPrimary,
        title = {
            Text(
                text = "Add Position to Portfolio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Symbol (e.g. RELIANCE)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth().testTag("symbol_input")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Company Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("Quantity", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Avg Buy Price", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isSwing,
                        onClick = { isSwing = true },
                        label = { Text("SWING") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isSwing,
                        onClick = { isSwing = false },
                        label = { Text("LONG TERM") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = thesis,
                    onValueChange = { thesis = it },
                    label = { Text("Investment Thesis", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SapphireBlue, unfocusedBorderColor = TerminalBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isNotBlank()) {
                        onAdd(
                            symbol,
                            if (name.isBlank()) symbol else name,
                            sector,
                            qty.toIntOrNull() ?: 10,
                            price.toDoubleOrNull() ?: 100.0,
                            if (isSwing) StrategyType.SWING else StrategyType.LONG_TERM,
                            target.toDoubleOrNull() ?: 120.0,
                            stopLoss.toDoubleOrNull() ?: 90.0,
                            thesis
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue),
                modifier = Modifier.testTag("submit_position_button")
            ) {
                Text("SAVE POSITION", color = TextContrast)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextMuted)
            }
        }
    )
}

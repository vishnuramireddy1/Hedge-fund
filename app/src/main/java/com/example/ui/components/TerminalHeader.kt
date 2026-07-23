package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ai.orchestrator.SystemContext
import com.example.ui.theme.*

@Composable
fun TerminalHeader(
    systemContext: SystemContext,
    isScanning: Boolean,
    onRunMultiAgentScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status Bar Mockup
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NSE / BSE INDIA LIVE",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isScanning) SapphireBlue.copy(alpha = alpha) else BullishGreen
                        )
                )
                Text(
                    text = if (isScanning) "27 AGENTS SCANNING" else "30-MIN CRON ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isScanning) SapphireBlue else BullishGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${systemContext.currentTime} IST",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // Header Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PERSONAL HEDGE FUND",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Bharat Invest OS",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Market Status Badge
                Surface(
                    color = if (systemContext.marketStatus.contains("LIVE")) BullishGreenGlow else TerminalCardElevated,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (systemContext.marketStatus.contains("LIVE")) BullishGreen.copy(alpha = 0.4f) else TerminalBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (systemContext.marketStatus.contains("LIVE")) BullishGreen else WarningAmber)
                        )
                        Text(
                            text = systemContext.marketStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (systemContext.marketStatus.contains("LIVE")) BullishGreen else WarningAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Run Multi-Agent Scan Button
                IconButton(
                    onClick = onRunMultiAgentScan,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SapphireBlueGlow)
                        .border(1.dp, SapphireBlue.copy(alpha = 0.5f), CircleShape)
                        .testTag("run_scan_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Run Scan",
                        tint = SapphireBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun TerminalBottomNavBar(
    activeTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = TerminalCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Terminal",
                icon = Icons.Default.GridView,
                isSelected = activeTab == AppTab.DASHBOARD,
                onClick = { onTabSelected(AppTab.DASHBOARD) },
                testTag = "nav_dashboard"
            )

            NavItem(
                label = "Portfolio",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = activeTab == AppTab.PORTFOLIO,
                onClick = { onTabSelected(AppTab.PORTFOLIO) },
                testTag = "nav_portfolio"
            )

            NavItem(
                label = "CIO AI",
                icon = Icons.Default.Psychology,
                isSelected = activeTab == AppTab.CIO_ASSISTANT,
                onClick = { onTabSelected(AppTab.CIO_ASSISTANT) },
                testTag = "nav_cio_ai"
            )

            NavItem(
                label = "Research",
                icon = Icons.Default.Insights,
                isSelected = activeTab == AppTab.RESEARCH,
                onClick = { onTabSelected(AppTab.RESEARCH) },
                testTag = "nav_research"
            )

            NavItem(
                label = "25 Agents",
                icon = Icons.Default.Hub,
                isSelected = activeTab == AppTab.AGENTS_25,
                onClick = { onTabSelected(AppTab.AGENTS_25) },
                testTag = "nav_agents"
            )

            NavItem(
                label = "Journal",
                icon = Icons.Default.MenuBook,
                isSelected = activeTab == AppTab.JOURNAL,
                onClick = { onTabSelected(AppTab.JOURNAL) },
                testTag = "nav_journal"
            )

            NavItem(
                label = "Knowledge",
                icon = Icons.Default.FolderSpecial,
                isSelected = activeTab == AppTab.KNOWLEDGE,
                onClick = { onTabSelected(AppTab.KNOWLEDGE) },
                testTag = "nav_knowledge"
            )

            NavItem(
                label = "Security",
                icon = Icons.Default.Shield,
                isSelected = activeTab == AppTab.SECURITY,
                onClick = { onTabSelected(AppTab.SECURITY) },
                testTag = "nav_security"
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val containerBg = if (isSelected) SapphireBlueGlow else androidx.compose.ui.graphics.Color.Transparent
    val iconColor = if (isSelected) SapphireBlue else TextMuted
    val textColor = if (isSelected) TextPrimary else TextMuted

    Surface(
        onClick = onClick,
        modifier = Modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = containerBg,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SapphireBlue.copy(alpha = 0.3f)) else null
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}


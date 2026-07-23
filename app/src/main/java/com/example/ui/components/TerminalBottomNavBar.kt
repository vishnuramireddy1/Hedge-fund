package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
        modifier = modifier.fillMaxWidth(),
        color = TerminalCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
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
    val iconColor = if (isSelected) SapphireBlue else TextMuted
    val textColor = if (isSelected) TextPrimary else TextMuted

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(SapphireBlue)
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

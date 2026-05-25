package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalpro.Ui1.Navigation.Screen
import com.example.finalpro.Ui1.Navigation.bottomNavItems
import com.example.finalpro.Ui1.Theme.*

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute by navController.currentBackStackEntryAsState().let {
        val entry = it.value
        remember(entry) {
            derivedStateOf { entry?.destination?.route }
        }
    }

    NavigationBar(
        containerColor = BgSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor = GreenPrimary.copy(alpha = 0.08f),
                spotColor = GreenPrimary.copy(alpha = 0.12f)
            )
            .background(
                color = BgSurface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        item.icon, null,
                        modifier = Modifier.size(22.dp),
                        tint = if (selected) GreenPrimary else TextMuted
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) GreenPrimary else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = GreenLight,
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
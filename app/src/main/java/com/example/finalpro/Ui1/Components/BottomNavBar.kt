package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        androidx.compose.runtime.remember(entry) {
            androidx.compose.runtime.derivedStateOf { entry?.destination?.route }
        }
    }

    NavigationBar(
        containerColor = BgSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            BorderStroke(1.dp, Border),
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
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
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon = {
                    Icon(
                        item.icon, null,
                        modifier = Modifier.size(22.dp),
                        tint = if (selected) AccentPrimary else TextSecondary
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) AccentPrimary else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = AccentGlow
                )
            )
        }
    }
}

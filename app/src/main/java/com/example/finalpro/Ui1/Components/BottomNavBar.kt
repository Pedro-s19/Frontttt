package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalpro.Ui1.Navigation.bottomNavItems
import com.example.finalpro.Ui1.Theme.AccentGlow
import com.example.finalpro.Ui1.Theme.AccentPrimary
import com.example.finalpro.Ui1.Theme.BgSurface
import com.example.finalpro.Ui1.Theme.Border
import com.example.finalpro.Ui1.Theme.TextSecondary

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = BgSurface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(1.dp, Border, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, null, modifier = Modifier.size(22.dp), tint = if (selected) AccentPrimary else TextSecondary) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall, color = if (selected) AccentPrimary else TextSecondary) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = AccentGlow)
            )
        }
    }
}
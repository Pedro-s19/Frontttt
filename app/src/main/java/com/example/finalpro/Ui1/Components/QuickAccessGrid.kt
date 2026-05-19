package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Navigation.Screen
import com.example.finalpro.Ui1.Theme.AccentPrimary
import com.example.finalpro.Ui1.Theme.BgCard
import com.example.finalpro.Ui1.Theme.Border
import com.example.finalpro.Ui1.Theme.TextSecondary

@Composable
fun QuickAccessGrid(navController: NavController) {
    val items = listOf(
        "Gastos" to Icons.Rounded.ShoppingCart to Screen.Gastos.route,
        "Ingresos" to Icons.Rounded.AttachMoney to Screen.Ingresos.route,
        "Presupuesto" to Icons.Rounded.PieChart to Screen.Presupuesto.route,
        "Metas" to Icons.Rounded.EmojiEvents to Screen.Metas.route
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { (titleIconPair, route) ->
            val (title, icon) = titleIconPair
            Card(Modifier.weight(1f).aspectRatio(1f), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = BgCard), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(icon, null, tint = AccentPrimary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                }
            }
        }
    }
}
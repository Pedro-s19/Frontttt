package com.example.finalpro.Ui1.Screens.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown  // ✅ AutoMirrored
import androidx.compose.material.icons.automirrored.rounded.TrendingUp    // ✅ AutoMirrored
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.*
import com.example.finalpro.Ui1.Theme.*

@Composable
fun DashboardScreen(
    navController: NavController,
    vm: DashboardViewModel = hiltViewModel()
) {
    val resumen by vm.resumen.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bienvenido 👋", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("Resumen del mes", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    MonedaChip(sessionManager = vm.sessionManager)
                }
            }

            item {
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                } else {
                    resumen?.let { r ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                if (r.balance >= 0) Color(0xFF1A3A5C) else Color(0xFF3A1A1A),
                                                BgCard
                                            )
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .border(1.dp, Border, RoundedCornerShape(22.dp))
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Text("Balance del mes", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = formatMoney(r.balance, r.moneda),
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = if (r.balance >= 0) ColorIngreso else ColorGasto,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("↑ Ingresos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            Text(
                                                text = formatMoney(r.totalIngresos, r.moneda),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = ColorIngreso,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Column {
                                            Text("↓ Gastos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                            Text(
                                                text = formatMoney(r.totalGastos, r.moneda),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = ColorGasto,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                resumen?.let { r ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Ingresos",
                            value = formatMoney(r.totalIngresos, r.moneda),
                            icon = Icons.AutoMirrored.Rounded.TrendingUp,   // ✅ AutoMirrored
                            color = ColorIngreso
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Gastos",
                            value = formatMoney(r.totalGastos, r.moneda),
                            icon = Icons.AutoMirrored.Rounded.TrendingDown, // ✅ AutoMirrored
                            color = ColorGasto
                        )
                    }
                }
            }

            item {
                resumen?.let { r ->
                    if (r.porcentajePresupuestoUsado != null) {
                        PresupuestoResumenCard(
                            usado = r.porcentajePresupuestoUsado,
                            restante = r.presupuestoRestante ?: 0.0,
                            moneda = r.moneda
                        )
                    }
                }
            }

            item {
                Text("Acceso rápido", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            item {
                QuickAccessGrid(navController)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
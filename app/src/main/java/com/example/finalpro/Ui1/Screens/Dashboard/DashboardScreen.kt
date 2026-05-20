package com.example.finalpro.Ui1.Screens.Dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.*
import com.example.finalpro.Ui1.Navigation.Screen
import com.example.finalpro.Ui1.Theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    vm: DashboardViewModel = hiltViewModel()
) {
    val resumen by vm.resumen.collectAsState()
    val loading by vm.loading.collectAsState()
    val email by vm.sessionManager.getEmail().collectAsState(initial = "")
    val moneda by vm.sessionManager.getMoneda().collectAsState(initial = "COP")
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Recargar cuando cambia la moneda
    LaunchedEffect(moneda) { vm.cargarResumen() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BgSurface,
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                // Avatar y email
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        contentDescription = null,
                        tint = AccentPrimary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = email ?: "Usuario",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = "Cuenta activa", color = TextSecondary, fontSize = 13.sp)
                }
                Spacer(Modifier.height(24.dp))
                Divider(color = Border, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                // Selector de moneda en el drawer
                Text(
                    "Moneda preferida",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("COP", "USD", "EUR", "MXN").forEach { m ->
                        FilterChip(
                            selected = moneda == m,
                            onClick = { scope.launch { vm.sessionManager.saveMoneda(m) } },
                            label = { Text(m, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = BgCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = moneda == m,
                                borderColor = Border,
                                selectedBorderColor = AccentPrimary
                            )
                        )
                    }
                }

                Spacer(Modifier.weight(1f))
                Divider(color = Border, modifier = Modifier.padding(horizontal = 16.dp))
                NavigationDrawerItem(
                    label = { Text("Cerrar sesion", color = ColorGasto) },
                    icon = { Icon(Icons.Rounded.ExitToApp, null, tint = ColorGasto) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            vm.sessionManager.clearSession()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = BgPrimary,
            bottomBar = { BottomNavBar(navController) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
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
                            Text(
                                "Resumen del mes",
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Icono para abrir drawer (perfil / moneda / logout)
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Rounded.AccountCircle,
                                contentDescription = "Menu de cuenta",
                                tint = AccentPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                item {
                    // Chip de moneda visible en dashboard tambien
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Moneda: ", color = TextSecondary, fontSize = 13.sp)
                        Text(moneda, color = AccentPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                item {
                    if (loading) {
                        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
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
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = formatMoney(r.balance, r.moneda),
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = if (r.balance >= 0) ColorIngreso else ColorGasto,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text("Ingresos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                                Text(
                                                    text = formatMoney(r.totalIngresos, r.moneda),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = ColorIngreso,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Gastos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
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
                        } ?: run {
                            // Estado vacio - usuario nuevo sin datos
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = BgCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                            ) {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Sin datos este mes", color = TextSecondary, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(4.dp))
                                        Text("Agrega ingresos y gastos para ver tu resumen", color = TextMuted, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    resumen?.let { r ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Ingresos",
                                value = formatMoney(r.totalIngresos, r.moneda),
                                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                                color = ColorIngreso
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Gastos",
                                value = formatMoney(r.totalGastos, r.moneda),
                                icon = Icons.AutoMirrored.Rounded.TrendingDown,
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
                    Text("Acceso rapido", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                item { QuickAccessGrid(navController) }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
package com.example.finalpro.Ui1.Screens.Gastos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.AgregarGastoSheet
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.TransaccionItem
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    navController: NavController,
    vm: GastosViewModel = hiltViewModel()
) {
    val gastos by vm.gastos.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val loading by vm.loading.collectAsState()
    val ingresos by vm.ingresosMes.collectAsState()
    val alertas by vm.alertas.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Gastos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary),
                actions = {
                    val total = gastos.sumOf { it.monto }
                    Text(
                        text = formatMoney(total, gastos.firstOrNull()?.moneda ?: "COP"),
                        color = ColorGasto,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }, containerColor = AccentPrimary) {
                Icon(Icons.Rounded.Add, null, tint = androidx.compose.ui.graphics.Color.White)
            }
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Alerta
                if (alertas.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorWarning.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, ColorWarning)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("⚠️ Alertas", style = MaterialTheme.typography.titleSmall, color = ColorWarning, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                alertas.forEach { alerta ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("•", color = ColorWarning)
                                        Spacer(Modifier.width(6.dp))
                                        Text(text = alerta, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Barra de progreso gastos vs ingresos
                if (ingresos > 0) {
                    item {
                        val totalGastos = gastos.sumOf { it.monto }
                        val porcentaje = (totalGastos / ingresos).toFloat().coerceIn(0f, 1.2f)
                        val moneda = gastos.firstOrNull()?.moneda ?: "COP"
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gastado este mes: ${formatMoney(totalGastos, moneda)}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "${(porcentaje * 100).toInt()}% de tus ingresos",
                                    color = when {
                                        porcentaje > 0.9f -> ColorGasto
                                        porcentaje > 0.7f -> ColorWarning
                                        else -> AccentPrimary
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { porcentaje.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = when {
                                    porcentaje > 0.9f -> ColorGasto
                                    porcentaje > 0.7f -> ColorWarning
                                    else -> ColorIngreso
                                },
                                trackColor = Border,
                                strokeCap = StrokeCap.Round,
                            )
                            if (porcentaje > 1.0f) {
                                Text("Excedido en ${formatMoney(totalGastos - ingresos, moneda)}", color = ColorGasto, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }

                items(gastos, key = { it.id }) { gasto ->
                    TransaccionItem(
                        descripcion = gasto.descripcion ?: "Sin descripcion",
                        monto = "-${formatMoney(gasto.monto, gasto.moneda)}",
                        fecha = gasto.fecha,
                        categoria = gasto.categoria?.nombre ?: "General",
                        icono = gasto.categoria?.icono,
                        colorMonto = ColorGasto,
                        onDelete = { vm.eliminarGasto(gasto.id) },
                        onEdit = { monto, desc, fecha -> vm.actualizarGasto(gasto.id, monto, desc, fecha) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarGastoSheet(
            onDismiss = { showSheet = false },
            onConfirm = { monto, desc, fecha, catId ->
                vm.crearGasto(monto, desc, fecha, catId)
                showSheet = false
            },
            categorias = categorias,
            ingresoMensual = ingresos,
            gastoAcumulado = gastos.sumOf { it.monto }
        )
    }
}
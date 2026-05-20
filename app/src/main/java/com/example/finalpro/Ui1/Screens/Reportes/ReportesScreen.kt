package com.example.finalpro.Ui1.Screens.Reportes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.DonutChartSection
import com.example.finalpro.Ui1.Components.LineChartView
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    navController: NavController,
    vm: ReportesViewModel = hiltViewModel()
) {
    val tendencia by vm.tendencia.collectAsState()
    val distribucion by vm.distribucion.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Reportes", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Tendencia diaria de gastos",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    val t = tendencia
                    val hayDatosTendencia = t != null && t.valores.any { it > 0 }

                    if (hayDatosTendencia && t != null) {
                        LineChartView(etiquetas = t.etiquetas, valores = t.valores, moneda = t.moneda)
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Sin gastos este mes", color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Text("Registra gastos para ver la tendencia", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Distribucion por categoria",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    val d = distribucion
                    val hayDatosDonut = d != null && d.items.isNotEmpty()

                    if (hayDatosDonut && d != null) {
                        DonutChartSection(items = d.items, moneda = d.moneda)
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Sin datos por categoria", color = TextSecondary, fontWeight = FontWeight.Bold)
                                    Text("Agrega gastos con categorias para ver la distribucion", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorGasto.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "Error al cargar: $error",
                                color = ColorGasto,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
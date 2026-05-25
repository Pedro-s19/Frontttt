package com.example.finalpro.Ui1.Screens.Reportes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Data.Remote.Dto.Response.DistribucionCategoriaResponse
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.HeatmapCalendar
import com.example.finalpro.Ui1.Components.RadarChart
import com.example.finalpro.Ui1.Components.WaterfallChart
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    navController: NavController,
    vm: ReportesViewModel = hiltViewModel()
) {
    val tendencia    by vm.tendencia.collectAsState()
    val distribucion by vm.distribucion.collectAsState()
    val resumen      by vm.resumen.collectAsState()
    val gastosDiarios by vm.gastosDiarios.collectAsState()
    val loading      by vm.loading.collectAsState()
    val error        by vm.error.collectAsState()

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                // Tendencia diaria (barras)
                item {
                    Text("📊 Tendencia diaria de gastos",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val t = tendencia
                    if (t != null && t.valores.any { it > 0 }) {
                        BarChartView(etiquetas = t.etiquetas, valores = t.valores, moneda = t.moneda)
                    } else {
                        EmptyChartCard("Sin gastos este mes", "Registra gastos para ver la tendencia")
                    }
                }

                // Distribución por categoría (dona)
                item {
                    Text("🍩 Distribución por categoría",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val d = distribucion
                    if (d != null && d.items.isNotEmpty()) {
                        DonutChartView(items = d.items, moneda = d.moneda)
                    } else {
                        EmptyChartCard("Sin datos por categoría", "Agrega gastos con categorías")
                    }
                }

                // 🔥 Cascada financiera
                item {
                    val r = resumen
                    if (r != null) {
                        val totalGastos = r.totalGastos
                        WaterfallChart(
                            ingresos = r.totalIngresos,
                            gastosFijos = totalGastos * 0.4,
                            suscripciones = totalGastos * 0.2,
                            otrosGastos = totalGastos * 0.4,
                            ahorro = r.totalIngresos - totalGastos,
                            moneda = r.moneda
                        )
                    }
                }

                // 🔥 Radar de equilibrio
                item {
                    val d = distribucion
                    if (d != null && d.items.isNotEmpty()) {
                        val categorias = d.items.map { it.categoriaNombre }
                        val valores = d.items.map { it.total }
                        val ideal = List(d.items.size) { d.items.sumOf { it.total } / d.items.size }
                        RadarChart(
                            categorias = categorias,
                            valores = valores,
                            valoresIdeales = ideal,
                            moneda = d.moneda
                        )
                    }
                }

                // 🔥 Calendario heatmap
                item {
                    HeatmapCalendar(
                        gastosDiarios = gastosDiarios,
                        mes = vm.mes,
                        anio = vm.anio,
                        moneda = vm.moneda
                    )
                }

                if (error != null) {
                    item {
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ColorGasto.copy(alpha = 0.1f))
                        ) {
                            Text("Error: $error", color = ColorGasto, modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ----------------------------------------------------
// Funciones de gráficos existentes (sin cambios)
// ----------------------------------------------------
@Composable
fun BarChartView(etiquetas: List<String>, valores: List<Double>, moneda: String) {
    val maxValor = valores.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    val textMeasurer = rememberTextMeasurer()
    Card(Modifier.fillMaxWidth().height(240.dp), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard), border = BorderStroke(1.dp, Border)) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Gastos por día", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val w = size.width; val h = size.height
                val barCount = valores.size
                val totalGap = w * 0.3f
                val barWidth = if (barCount > 0) (w - totalGap) / barCount else 0f
                val gapWidth = if (barCount > 1) totalGap / (barCount - 1) else 0f
                val maxH = h * 0.85f
                for (i in 0..3) {
                    val y = h * (1f - i / 3f)
                    drawLine(Border, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                }
                valores.forEachIndexed { i, valor ->
                    val barH = (valor / maxValor * maxH).toFloat().coerceAtLeast(4f)
                    val x = i * (barWidth + gapWidth)
                    val top = h - barH
                    val color = ChartPalette[i % ChartPalette.size]
                    drawRoundRect(color.copy(alpha = 0.15f), Offset(x, 0f), Size(barWidth, h), CornerRadius(6f))
                    drawRoundRect(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.6f)), top, h),
                        Offset(x, top), Size(barWidth, barH), CornerRadius(6f))
                    if (valor > 0) {
                        val label = "${(valor / 1000).toInt()}k"
                        val result = textMeasurer.measure(label, TextStyle(color = color, fontSize = 9.sp))
                        val textX = x + barWidth / 2 - result.size.width / 2
                        val textY = (top - result.size.height - 2f).coerceAtLeast(0f)
                        drawText(textMeasurer, label, Offset(textX, textY),
                            TextStyle(color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    }
                }
            }
            val step = if (etiquetas.size > 10) etiquetas.size / 5 else 1
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                etiquetas.filterIndexed { i, _ -> i % step == 0 }.forEach { etiqueta ->
                    Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun DonutChartView(items: List<DistribucionCategoriaResponse.ItemCategoriaGasto>, moneda: String) {
    val colores = ChartPalette.take(items.size)
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard), border = BorderStroke(1.dp, Border)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(160.dp)) {
                        val strokeWidth = 32.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2f
                        val center = Offset(size.width / 2, size.height / 2)
                        var startAngle = -90f
                        items.forEachIndexed { i, item ->
                            val sweep = (item.porcentaje / 100f * 360f).toFloat()
                            drawArc(colores[i].copy(alpha = 0.2f), startAngle, sweep - 1f, false,
                                style = Stroke(width = strokeWidth + 6f, cap = StrokeCap.Round),
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2))
                            drawArc(colores[i], startAngle, sweep - 1f, false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2))
                            startAngle += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${items.size}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("categorías", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEachIndexed { i, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(colores[i]))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(item.categoriaNombre, style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${item.porcentaje.toInt()}% · ${formatMoneyShort(item.total, moneda)}",
                                    style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Border)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total gastado:", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Text(formatMoney(items.sumOf { it.total }, moneda), color = ColorGasto,
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun EmptyChartCard(titulo: String, subtitulo: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard), border = BorderStroke(1.dp, Border)) {
        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(titulo, color = TextSecondary, fontWeight = FontWeight.Bold)
                Text(subtitulo, color = TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun formatMoneyShort(monto: Double, moneda: String): String {
    val simbolos = mapOf("COP" to "$", "USD" to "US$", "EUR" to "€")
    val simbolo = simbolos[moneda] ?: moneda
    return when {
        monto >= 1_000_000 -> "$simbolo ${"%.1f".format(monto / 1_000_000)}M"
        monto >= 1_000     -> "$simbolo ${"%.0f".format(monto / 1_000)}k"
        else               -> "$simbolo ${"%.0f".format(monto)}"
    }
}
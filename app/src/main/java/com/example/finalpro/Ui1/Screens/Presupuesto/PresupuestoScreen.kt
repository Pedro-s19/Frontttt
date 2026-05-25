package com.example.finalpro.Ui1.Screens.Presupuesto

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Data.Remote.Dto.Response.CategoriaResponse
import com.example.finalpro.Data.Remote.Dto.Response.PresupuestoResponse
import com.example.finalpro.Ui1.Components.AmountTextField
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoScreen(
    navController: NavController,
    vm: PresupuestoViewModel = hiltViewModel()
) {
    val presupuestos by vm.presupuestos.collectAsState()
    val categorias   by vm.categorias.collectAsState()
    val loading      by vm.loading.collectAsState()
    val alertas      by vm.alertas.collectAsState()
    var showSheet    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargarDatos() }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Presupuestos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary),
                actions = {
                    IconButton(onClick = { vm.cargarDatos() }) {
                        Icon(Icons.Rounded.Refresh, null, tint = TextSecondary)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showSheet = true },
                containerColor = AccentPrimary,
                shape          = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Rounded.Add, null, tint = Color.White)
            }
        }
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Alertas
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("•", color = ColorWarning)
                                        Spacer(Modifier.width(6.dp))
                                        Text(text = alerta, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(4.dp)) }

                if (presupuestos.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(top = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💰", style = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp)))
                                Spacer(Modifier.height(8.dp))
                                Text("Sin presupuestos este mes", color = TextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text("Toca + para crear uno", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                items(items = presupuestos, key = { it.id }) { presupuesto ->
                    PresupuestoDetalleCard(
                        presupuesto = presupuesto,
                        onEdit      = { nuevoLimite -> vm.actualizarPresupuesto(presupuesto.id, nuevoLimite) },
                        onDelete    = { vm.eliminarPresupuesto(presupuesto.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarPresupuestoSheet(
            categorias = categorias,
            onDismiss  = { showSheet = false },
            onConfirm  = { catId, limite ->
                vm.crearPresupuesto(catId, limite)
                showSheet = false
            }
        )
    }
}

@Composable
fun PresupuestoDetalleCard(
    presupuesto: PresupuestoResponse,
    onEdit: (Double) -> Unit,
    onDelete: () -> Unit
) {
    val porcentaje = if (presupuesto.limiteMonto > 0)
        (presupuesto.gastadoMonto / presupuesto.limiteMonto).toFloat().coerceIn(0f, 1f)
    else 0f

    val barColor = when {
        porcentaje >= 1f   -> ColorGasto
        porcentaje >= 0.8f -> ColorWarning
        else               -> AccentPrimary
    }

    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(barColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.PieChart, null, tint = barColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            presupuesto.categoriaNombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${presupuesto.anio} · Mes ${presupuesto.mes}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
                Row {
                    IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.Edit, null, tint = AccentPrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Rounded.Delete, null, tint = ColorGasto, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Border)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(porcentaje)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(listOf(barColor, barColor.copy(alpha = 0.7f)))
                        )
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Gastado", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        formatMoney(presupuesto.gastadoMonto, presupuesto.moneda),
                        color      = barColor,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Usado", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "${(porcentaje * 100).toInt()}%",
                        color      = barColor,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Límite", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        formatMoney(presupuesto.limiteMonto, presupuesto.moneda),
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (porcentaje >= 1f) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorGasto.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Warning, null, tint = ColorGasto, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("¡Presupuesto superado!", color = ColorGasto, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            } else if (porcentaje >= 0.8f) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorWarning.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Info, null, tint = ColorWarning, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cerca del límite", color = ColorWarning, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

    if (showEditDialog) {
        EditarPresupuestoDialog(
            limiteActual = presupuesto.limiteMonto,
            onDismiss    = { showEditDialog = false },
            onConfirm    = { nuevoLimite ->
                onEdit(nuevoLimite)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditarPresupuestoDialog(
    limiteActual: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var texto by remember { mutableStateOf(limiteActual.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = BgCard,
        title = { Text("Editar límite", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            AmountTextField(
                value = texto,
                onValueChange = { texto = it },
                label = "Nuevo límite"
            )
        },
        confirmButton = {
            Button(
                onClick = { texto.toDoubleOrNull()?.let { onConfirm(it) } },
                colors  = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPresupuestoSheet(
    categorias: List<CategoriaResponse>,
    onDismiss: () -> Unit,
    onConfirm: (categoriaId: String, limite: Double) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(categorias.firstOrNull()) }
    var limiteText by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BgSurface) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("Nuevo presupuesto", style = MaterialTheme.typography.titleLarge,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = expandedDropdown, onExpandedChange = { expandedDropdown = it }) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "Selecciona una categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                    modifier = Modifier.background(BgCard)
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.icono ?: "📂"} ${cat.nombre}", color = TextPrimary) },
                            onClick = { categoriaSeleccionada = cat; expandedDropdown = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AmountTextField(
                value = limiteText,
                onValueChange = { limiteText = it },
                label = "Límite mensual",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val limite = limiteText.toDoubleOrNull()
                    val cat = categoriaSeleccionada
                    if (limite != null && cat != null) onConfirm(cat.id, limite)
                },
                enabled = limiteText.toDoubleOrNull() != null && categoriaSeleccionada != null,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
            ) {
                Text("Crear presupuesto", fontWeight = FontWeight.Bold)
            }
        }
    }
}
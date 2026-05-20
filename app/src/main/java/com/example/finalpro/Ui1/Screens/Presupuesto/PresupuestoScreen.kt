package com.example.finalpro.Ui1.Screens.Presupuesto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.PresupuestoProgressCard
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoScreen(
    navController: NavController,
    vm: PresupuestoViewModel = hiltViewModel()
) {
    val presupuestos by vm.presupuestos.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val loading by vm.loading.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Presupuestos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }, containerColor = AccentPrimary) {
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
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (presupuestos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Sin presupuestos este mes", color = TextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text("Toca + para crear uno", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                items(items = presupuestos, key = { it.id }) { presupuesto ->
                    PresupuestoProgressCard(
                        categoria = presupuesto.categoriaNombre,
                        limite = presupuesto.limiteMonto,
                        gastado = presupuesto.gastadoMonto,
                        moneda = presupuesto.moneda,
                        onEdit = { nuevoLimite: Double -> vm.actualizarPresupuesto(presupuesto.id, nuevoLimite) },
                        onDelete = { vm.eliminarPresupuesto(presupuesto.id, presupuesto.anio, presupuesto.mes) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarPresupuestoSheet(
            categorias = categorias,
            onDismiss = { showSheet = false },
            onConfirm = { catId, limite ->
                vm.crearPresupuesto(catId, limite)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPresupuestoSheet(
    categorias: List<com.example.finalpro.Data.Remote.Dto.Response.CategoriaResponse>,
    onDismiss: () -> Unit,
    onConfirm: (categoriaId: String, limite: Double) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(categorias.firstOrNull()) }
    var limiteText by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BgSurface) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("Nuevo presupuesto", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            // Selector de categoria
            ExposedDropdownMenuBox(expanded = expandedDropdown, onExpandedChange = { expandedDropdown = it }) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "Selecciona una categoria",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                ExposedDropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre, color = TextPrimary) },
                            onClick = { categoriaSeleccionada = cat; expandedDropdown = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = limiteText,
                onValueChange = { limiteText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Limite mensual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = Border,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
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
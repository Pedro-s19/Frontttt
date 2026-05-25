package com.example.finalpro.Ui1.Screens.Recurrentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.AmountTextField
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrentesScreen(navController: NavController, vm: RecurrentesViewModel = hiltViewModel()) {
    val recurrentes by vm.recurrentes.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val loading by vm.loading.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(title = { Text("Suscripciones y fijos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary))
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
                items(recurrentes, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        border = BorderStroke(1.dp, Border)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.nombre, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${item.categoriaNombre} · día ${item.diaMes}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                Text(formatMoney(item.monto, "COP"), color = AccentPrimary)
                            }
                            IconButton(onClick = { vm.eliminar(item.id) }) {
                                Icon(Icons.Rounded.Delete, null, tint = ColorGasto)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarRecurrenteSheet(categorias = categorias, onDismiss = { showSheet = false }) { nombre, monto, dia, catId ->
            vm.crear(nombre, monto, dia, catId)
            showSheet = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarRecurrenteSheet(
    categorias: List<com.example.finalpro.Data.Remote.Dto.Response.CategoriaResponse>,
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, monto: Double, dia: Int, categoriaId: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("1") }
    var selectedCategory by remember { mutableStateOf(categorias.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = BgSurface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Nuevo gasto recurrente", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPrimary, unfocusedBorderColor = Border), modifier = Modifier.fillMaxWidth())
            AmountTextField(value = monto, onValueChange = { monto = it }, label = "Monto", modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dia, onValueChange = { dia = it.filter { c -> c.isDigit() } }, label = { Text("Día del mes (1-28)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPrimary, unfocusedBorderColor = Border), modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedCategory?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentPrimary, unfocusedBorderColor = Border),
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat.nombre) }, onClick = { selectedCategory = cat; expanded = false })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(14.dp), modifier = Modifier.weight(1f).height(52.dp)) { Text("Cancelar") }
                Button(onClick = {
                    monto.toDoubleOrNull()?.let { m -> selectedCategory?.let { onConfirm(nombre, m, dia.toIntOrNull() ?: 1, it.id) } }
                }, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary), modifier = Modifier.weight(1f).height(52.dp)) { Text("Guardar") }
            }
        }
    }
}
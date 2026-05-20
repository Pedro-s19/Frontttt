package com.example.finalpro.Ui1.Screens.Gastos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                items(gastos, key = { it.id }) { gasto ->
                    TransaccionItem(
                        descripcion = gasto.descripcion ?: "Sin descripcion",
                        monto = "-${formatMoney(gasto.monto, gasto.moneda)}",
                        fecha = gasto.fecha,
                        categoria = gasto.categoria?.nombre ?: "General",
                        icono = gasto.categoria?.icono,
                        colorMonto = ColorGasto,
                        onDelete = { vm.eliminarGasto(gasto.id) },
                        onEdit = { monto, desc, fecha ->
                            vm.actualizarGasto(gasto.id, monto, desc, fecha)
                        }
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
            categorias = categorias
        )
    }
}
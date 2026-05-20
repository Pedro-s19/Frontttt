package com.example.finalpro.Ui1.Screens.Ingresos

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
import com.example.finalpro.Ui1.Components.AgregarIngresoSheet
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.TransaccionItem
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresosScreen(
    navController: NavController,
    vm: IngresosViewModel = hiltViewModel()
) {
    val ingresos by vm.ingresos.collectAsState()
    val loading by vm.loading.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Ingresos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary),
                actions = {
                    val total = ingresos.sumOf { it.monto }
                    Text(
                        text = formatMoney(total, ingresos.firstOrNull()?.moneda ?: "COP"),
                        color = ColorIngreso,
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
                items(items = ingresos, key = { it.id }) { ingreso ->
                    TransaccionItem(
                        descripcion = ingreso.descripcion ?: "Sin descripcion",
                        monto = "+${formatMoney(ingreso.monto, ingreso.moneda)}",
                        fecha = ingreso.fecha,
                        categoria = "Ingreso",
                        icono = "💵",
                        colorMonto = ColorIngreso,
                        onDelete = { vm.eliminarIngreso(ingreso.id) },
                        onEdit = { monto, desc, fecha ->
                            vm.actualizarIngreso(ingreso.id, monto, desc, fecha)
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarIngresoSheet(onDismiss = { showSheet = false }) { monto, desc, fecha ->
            vm.crearIngreso(monto, desc, fecha)
            showSheet = false
        }
    }
}
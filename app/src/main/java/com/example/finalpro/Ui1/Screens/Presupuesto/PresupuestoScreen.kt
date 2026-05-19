package com.example.finalpro.Ui1.Screens.Presupuesto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    val loading by vm.loading.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Presupuestos", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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
                items(
                    items = presupuestos,
                    key = { it.id }
                ) { presupuesto ->
                    // CORREGIDO: no uses llaves extra
                    PresupuestoProgressCard(
                        categoria = presupuesto.categoriaNombre,
                        limite = presupuesto.limiteMonto,
                        gastado = presupuesto.gastadoMonto,
                        moneda = presupuesto.moneda,
                        onEdit = { nuevoLimite: Double ->   // tipo explícito
                            vm.actualizarPresupuesto(presupuesto.id, nuevoLimite)
                        },
                        onDelete = {
                            vm.eliminarPresupuesto(presupuesto.id, presupuesto.anio, presupuesto.mes)
                        }
                    )
                }
            }
        }
    }
}
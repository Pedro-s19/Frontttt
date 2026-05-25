package com.example.finalpro.Ui1.Screens.Gastos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.AgregarGastoSheet
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.SaldoDisponibleBanner
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
    val saldo by vm.saldoDisponible.collectAsState()
    val alertas by vm.alertas.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var filtroCategoria by remember { mutableStateOf<String?>(null) }

    val gastosFiltrados = remember(gastos, filtroCategoria) {
        if (filtroCategoria == null) {
            gastos
        } else {
            gastos.filter { it.categoria?.nombre == filtroCategoria }
        }
    }

    // ✅ CORREGIDO
    val nombresCategoria = remember(gastos) {
        gastos.mapNotNull { it.categoria?.nombre }.distinct()
    }

    val moneda = gastos.firstOrNull()?.moneda ?: "COP"

    Scaffold(
        containerColor = BgPrimary,

        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface)
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {

                Text(
                    text = "Gastos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                SaldoDisponibleBanner(
                    saldo = saldo,
                    moneda = moneda
                )

                if (alertas.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ColorWarning.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            ColorWarning.copy(alpha = 0.4f)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = "⚠️ Alertas",
                                style = MaterialTheme.typography.titleSmall,
                                color = ColorWarning,
                                fontWeight = FontWeight.Bold
                            )

                            alertas.forEach { alerta ->

                                Text(
                                    text = "• $alerta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        },

        bottomBar = {
            BottomNavBar(navController)
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    showSheet = true
                },
                containerColor = GreenPrimary,
                shape = CircleShape
            ) {

                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

    ) { padding ->

        if (loading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = GreenPrimary
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (nombresCategoria.isNotEmpty()) {

                    item {

                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            item {

                                FilterChip(
                                    selected = filtroCategoria == null,

                                    onClick = {
                                        filtroCategoria = null
                                    },

                                    label = {
                                        Text(
                                            text = "Todos",
                                            fontSize = 12.sp
                                        )
                                    },

                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = BgSurface,
                                        labelColor = TextSecondary
                                    ),

                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = filtroCategoria == null,
                                        borderColor = Border,
                                        selectedBorderColor = GreenPrimary
                                    )
                                )
                            }

                            items(nombresCategoria) { nombre ->

                                FilterChip(
                                    selected = filtroCategoria == nombre,

                                    onClick = {
                                        filtroCategoria =
                                            if (filtroCategoria == nombre) {
                                                null
                                            } else {
                                                nombre
                                            }
                                    },

                                    label = {
                                        Text(
                                            text = nombre,
                                            fontSize = 12.sp
                                        )
                                    },

                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = BgSurface,
                                        labelColor = TextSecondary
                                    ),

                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = filtroCategoria == nombre,
                                        borderColor = Border,
                                        selectedBorderColor = GreenPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (gastosFiltrados.isEmpty()) {

                    item {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),

                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "🧾",
                                    fontSize = 32.sp
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Sin gastos registrados",
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Toca + para agregar uno",
                                    color = TextMuted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                items(
                    items = gastosFiltrados,
                    key = { it.id }
                ) { gasto ->

                    TransaccionItem(
                        descripcion = gasto.descripcion ?: "Sin descripción",

                        monto = "-${formatMoney(gasto.monto, gasto.moneda)}",

                        fecha = gasto.fecha,

                        categoria = gasto.categoria?.nombre ?: "General",

                        icono = gasto.categoria?.icono,

                        colorMonto = ColorGasto,

                        onDelete = {
                            vm.eliminarGasto(gasto.id)
                        },

                        onEdit = { monto, desc, fecha ->

                            vm.actualizarGasto(
                                gasto.id,
                                monto,
                                desc,
                                fecha
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showSheet) {

        AgregarGastoSheet(

            onDismiss = {
                showSheet = false
            },

            onConfirm = { monto, desc, fecha, catId ->

                vm.crearGasto(
                    monto,
                    desc,
                    fecha,
                    catId
                )

                showSheet = false
            },

            categorias = categorias,

            saldoDisponible = saldo,

            moneda = moneda
        )
    }
}
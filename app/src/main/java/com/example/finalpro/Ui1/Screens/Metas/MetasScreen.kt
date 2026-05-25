package com.example.finalpro.Ui1.Screens.Metas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.AgregarMetaSheet
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Components.MetaAhorroCard
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(
    navController: NavController,
    vm: MetasViewModel = hiltViewModel()
) {
    val metas   by vm.metas.collectAsState()
    val loading by vm.loading.collectAsState()
    val balance by vm.balance.collectAsState()
    val alertas by vm.alertas.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.cargarMetas() }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Metas de ahorro", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary),
                actions = {
                    IconButton(onClick = { vm.cargarMetas() }) {
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

                if (metas.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(top = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎯", style = MaterialTheme.typography.headlineLarge)
                                Spacer(Modifier.height(8.dp))
                                Text("Sin metas de ahorro", color = TextSecondary, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text("Toca + para crear una", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                items(items = metas, key = { it.id }) { meta ->
                    MetaAhorroCard(
                        meta     = meta,
                        onAbonar = { monto -> vm.agregarAhorro(meta.id, monto) },
                        onDelete = { vm.eliminarMeta(meta.id) },
                        onEditar = { nuevoObjetivo -> vm.editarObjetivo(meta.id, nuevoObjetivo) }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showSheet) {
        AgregarMetaSheet(
            onDismiss = { showSheet = false },
            onConfirm = { nombre, monto, fecha ->
                vm.crearMeta(nombre, monto, fecha)
                showSheet = false
            },
            balanceDisponible = balance
        )
    }
}

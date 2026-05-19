package com.example.finalpro.Ui1.Screens.Metas

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
    val metas by vm.metas.collectAsState()
    val loading by vm.loading.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Metas de ahorro", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        },
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = AccentPrimary
            ) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = metas,
                    key = { it.id }
                ) { meta ->
                    MetaAhorroCard(
                        meta = meta,
                        onAbonar = { monto -> vm.agregarAhorro(meta.id, monto) },
                        onDelete = { vm.eliminarMeta(meta.id) }
                    )
                }
            }
        }
    }

    if (showSheet) {
        AgregarMetaSheet(
            onDismiss = { showSheet = false }
        ) { nombre, monto, fecha ->
            vm.crearMeta(nombre, monto, fecha)
            showSheet = false
        }
    }
}
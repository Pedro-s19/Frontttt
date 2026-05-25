package com.example.finalpro.Ui1.Screens.Logros

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalpro.Ui1.Components.BottomNavBar
import com.example.finalpro.Ui1.Theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(navController: NavController, vm: LogrosViewModel = hiltViewModel()) {
    val logros by vm.logros.collectAsState()
    val puntos by vm.puntos.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        containerColor = BgPrimary,
        topBar = { TopAppBar(title = { Text("Logros y Puntos", fontWeight = FontWeight.Bold, color = TextPrimary) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentPrimary) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                item {
                    Text("Tienes $puntos puntos", style = MaterialTheme.typography.headlineSmall, color = AccentPrimary)
                    Spacer(Modifier.height(16.dp))
                }
                items(logros, key = { it.id }) { logro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (logro.desbloqueado) AccentPrimary.copy(alpha = 0.2f) else BgCard
                        ),
                        border = BorderStroke(1.dp, if (logro.desbloqueado) AccentPrimary else Border)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(logro.icono, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(logro.nombre, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(logro.descripcion, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("+${logro.puntos} pts", style = MaterialTheme.typography.labelSmall, color = if (logro.desbloqueado) AccentPrimary else TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
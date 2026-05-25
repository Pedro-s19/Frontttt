package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finalpro.Data.Remote.Dto.Response.MetaAhorroResponse
import com.example.finalpro.Ui1.Theme.*

@Composable
fun MetaAhorroCard(
    meta: MetaAhorroResponse,
    onAbonar: (Double) -> Unit,
    onDelete: () -> Unit,
    onEditar: (nuevoObjetivo: Double) -> Unit = {}
) {
    val progress = (meta.porcentajeCompletado / 100.0).toFloat().coerceIn(0f, 1f)
    val progressColor = when {
        progress >= 1f   -> ColorIngreso
        progress >= 0.6f -> AccentSecond
        progress >= 0.3f -> ColorWarning
        else             -> AccentPrimary
    }

    var abonoText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = BgCard),
        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Encabezado
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
                            .background(progressColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.EmojiEvents, null,
                            tint     = progressColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            meta.nombre,
                            style      = MaterialTheme.typography.titleMedium,
                            color      = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        meta.fechaLimite?.let {
                            Text("Límite: $it", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
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

            Spacer(Modifier.height(12.dp))

            // Barra de progreso
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Border)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(listOf(progressColor, progressColor.copy(alpha = 0.6f)))
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            // Montos
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ahorrado", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        formatMoney(meta.montoActual, meta.moneda),
                        color      = progressColor,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Progreso", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        "${meta.porcentajeCompletado.toInt()}%",
                        color      = progressColor,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Objetivo", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        formatMoney(meta.montoObjetivo, meta.moneda),
                        color      = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Meta lograda
            if (progress >= 1f) {
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorIngreso.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = ColorIngreso, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("¡Meta lograda! 🎉", color = ColorIngreso, fontWeight = FontWeight.Bold)
                }
            } else {
                // Campo para abonar
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    AmountTextField(
                        value = abonoText,
                        onValueChange = { abonoText = it },
                        label = "Monto a abonar",
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            abonoText.toDoubleOrNull()?.let { onAbonar(it); abonoText = "" }
                        },
                        enabled = abonoText.toDoubleOrNull() != null,
                        shape   = RoundedCornerShape(12.dp),
                        colors  = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                    ) {
                        Text("Abonar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Diálogo editar objetivo
    if (showEditDialog) {
        var nuevoObjetivo by remember { mutableStateOf(meta.montoObjetivo.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = BgCard,
            title = { Text("Editar objetivo", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                AmountTextField(
                    value = nuevoObjetivo,
                    onValueChange = { nuevoObjetivo = it },
                    label = "Nuevo monto objetivo"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        nuevoObjetivo.toDoubleOrNull()?.let { onEditar(it) }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        )
    }
}
package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.finalpro.Data.Remote.Dto.Response.MetaAhorroResponse
import com.example.finalpro.Ui1.Theme.*

@Composable
fun MetaAhorroCard(meta: MetaAhorroResponse, onAbonar: (Double) -> Unit, onDelete: () -> Unit) {
    val progress = (meta.porcentajeCompletado / 100.0).toFloat().coerceIn(0f, 1f)
    val progressColor = when {
        progress >= 1f   -> ColorIngreso
        progress >= 0.6f -> AccentSecond
        progress >= 0.3f -> ColorWarning
        else             -> AccentPrimary
    }
    var abonoText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(progressColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = progressColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = meta.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        meta.fechaLimite?.let {
                            Text(
                                text = "Límite: $it",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, null, tint = ColorGasto, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Border)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(progressColor, progressColor.copy(alpha = 0.6f))))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMoney(meta.montoActual, meta.moneda),
                    color = progressColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "${meta.porcentajeCompletado.toInt()}% · ${formatMoney(meta.montoObjetivo, meta.moneda)}",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo para abonar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = abonoText,
                    onValueChange = { abonoText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monto a abonar (COP)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Button(
                    onClick = {
                        abonoText.toDoubleOrNull()?.let { onAbonar(it); abonoText = "" }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                ) {
                    Text("Abonar")
                }
            }
        }
    }
}
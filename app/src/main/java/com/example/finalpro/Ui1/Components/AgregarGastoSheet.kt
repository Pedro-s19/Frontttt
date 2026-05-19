package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalpro.Data.Remote.Dto.Response.CategoriaResponse
import com.example.finalpro.Ui1.Screens.Auth.financeTextFieldColors
import com.example.finalpro.Ui1.Theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoSheet(
    onDismiss: () -> Unit,
    onConfirm: (monto: Double, descripcion: String, fecha: String, categoriaId: String) -> Unit,
    categorias: List<CategoriaResponse>
) {
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var selectedCategoria by remember { mutableStateOf<CategoriaResponse?>(categorias.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Agregar gasto", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Monto (COP)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = financeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                shape = RoundedCornerShape(14.dp),
                colors = financeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                label = { Text("Fecha") },
                readOnly = true,
                shape = RoundedCornerShape(14.dp),
                colors = financeTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { datePickerOpen = true }) {
                        Icon(Icons.Rounded.EditCalendar, null)
                    }
                }
            )

            if (datePickerOpen) {
                DatePickerDialog(
                    onDismiss = { datePickerOpen = false },
                    onConfirm = { fecha = it.toString(); datePickerOpen = false }
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategoria?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    leadingIcon = { Text(selectedCategoria?.icono ?: "📦") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(14.dp),
                    colors = financeTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)  // 🔁 Reemplazo del deprecated
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            leadingIcon = { Text(cat.icono ?: "📦") },
                            onClick = {
                                selectedCategoria = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        monto.toDoubleOrNull()?.let { m ->
                            selectedCategoria?.let {
                                onConfirm(m, descripcion, fecha, it.id)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onConfirm(
                            java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
package com.example.finalpro.Ui1.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalpro.Ui1.Screens.Auth.financeTextFieldColors
import com.example.finalpro.Ui1.Theme.AccentPrimary
import com.example.finalpro.Ui1.Theme.BgSurface
import com.example.finalpro.Ui1.Theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMetaSheet(
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, montoObjetivo: Double, fechaLimite: String?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var fechaLimite by remember { mutableStateOf("") }
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
            Text("Nueva meta de ahorro", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = financeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Monto objetivo (COP)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = financeTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaLimite,
                onValueChange = {},
                label = { Text("Fecha límite (opcional)") },
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
                    onConfirm = { fechaLimite = it.toString(); datePickerOpen = false }
                )
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
                        monto.toDoubleOrNull()?.let { onConfirm(nombre, it, fechaLimite.ifEmpty { null }) }
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
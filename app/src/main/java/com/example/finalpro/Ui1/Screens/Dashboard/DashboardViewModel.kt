package com.example.finalpro.Ui1.Screens.Dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Local.SessionManager
import com.example.finalpro.Data.Remote.Dto.Response.ResumenMensualResponse
import com.example.finalpro.Data.Repository.AlertaRepository
import com.example.finalpro.Data.Repository.ReporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reporteRepository: ReporteRepository,
    val sessionManager: SessionManager,
    private val alertaRepository: AlertaRepository
) : ViewModel() {

    private val _resumen = MutableStateFlow<ResumenMensualResponse?>(null)
    val resumen: StateFlow<ResumenMensualResponse?> = _resumen.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _alertas = MutableStateFlow<List<String>>(emptyList())
    val alertas: StateFlow<List<String>> = _alertas.asStateFlow()

    // Datos de los últimos 7 días con gastos
    private val _gastosSemana = MutableStateFlow<Map<String, Double>>(emptyMap())
    val gastosSemana: StateFlow<Map<String, Double>> = _gastosSemana.asStateFlow()

    init { cargarResumen() }

    fun cargarResumen() {
        viewModelScope.launch {
            _loading.value = true
            val hoy = LocalDate.now()
            // Resumen mensual
            reporteRepository.resumenMensual(hoy.year, hoy.monthValue)
                .onSuccess { _resumen.value = it }
            // Alertas
            alertaRepository.obtenerAlertas()
                .onSuccess { _alertas.value = it.alertas }
                .onFailure { _alertas.value = emptyList() }
            // Gastos diarios del mes
            reporteRepository.gastosDiarios(hoy.year, hoy.monthValue)
                .onSuccess { gastosMap ->
                    // Últimos 7 días
                    val ultimos7 = LinkedHashMap<String, Double>()
                    for (i in 6 downTo 0) {
                        val fecha = hoy.minusDays(i.toLong())
                        val gasto = gastosMap[fecha] ?: 0.0
                        ultimos7[fecha.dayOfMonth.toString()] = gasto
                    }
                    _gastosSemana.value = ultimos7
                }
                .onFailure { _gastosSemana.value = emptyMap() }
            _loading.value = false
        }
    }
}

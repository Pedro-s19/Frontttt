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

    private val _safeToSpend = MutableStateFlow(0.0)
    val safeToSpend: StateFlow<Double> = _safeToSpend.asStateFlow()

    private val _diasRestantes = MutableStateFlow(0)
    val diasRestantes: StateFlow<Int> = _diasRestantes.asStateFlow()


    private val _alertas = MutableStateFlow<List<String>>(emptyList())
    val alertas: StateFlow<List<String>> = _alertas.asStateFlow()

    init { cargarResumen() }

    fun cargarResumen() {
        viewModelScope.launch {
            _loading.value = true
            val ahora = LocalDate.now()
            reporteRepository.resumenMensual(ahora.year, ahora.monthValue)
                .onSuccess { res ->
                    _resumen.value = res
                    calcularSafeToSpend(res)
                }
            // Obtener alertas
            alertaRepository.obtenerAlertas()
                .onSuccess { _alertas.value = it.alertas }
                .onFailure { _alertas.value = emptyList() }
            _loading.value = false
        }
    }

    private fun calcularSafeToSpend(resumen: ResumenMensualResponse) {
        val ingresos = resumen.totalIngresos
        val gastos = resumen.totalGastos
        val metaAhorro = ingresos * 0.2
        val disponible = ingresos - gastos - metaAhorro
        _safeToSpend.value = disponible.coerceAtLeast(0.0)
        val hoy = LocalDate.now()
        val diasDelMes = hoy.lengthOfMonth()
        _diasRestantes.value = (diasDelMes - hoy.dayOfMonth).coerceAtLeast(0)
    }
}

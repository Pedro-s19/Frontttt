package com.example.finalpro.Ui1.Screens.Dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Local.SessionManager
import com.example.finalpro.Data.Remote.Dto.Response.ResumenMensualResponse
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
    private val reporteRepository: ReporteRepository, val sessionManager: SessionManager
) : ViewModel() {
    private val _resumen = MutableStateFlow<ResumenMensualResponse?>(null)
    val resumen: StateFlow<ResumenMensualResponse?> = _resumen.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargarResumen() }

    fun cargarResumen() {
        viewModelScope.launch {
            _loading.value = true
            val ahora = LocalDate.now()
            reporteRepository.resumenMensual(ahora.year, ahora.monthValue)
                .onSuccess { _resumen.value = it }
            _loading.value = false
        }
    }
}
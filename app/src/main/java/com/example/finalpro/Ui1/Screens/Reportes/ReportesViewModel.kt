package com.example.finalpro.Ui1.Screens.Reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Remote.Dto.Response.DistribucionCategoriaResponse
import com.example.finalpro.Data.Remote.Dto.Response.TendenciaResponse
import com.example.finalpro.Data.Repository.ReporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportesViewModel @Inject constructor(
    private val reporteRepository: ReporteRepository
) : ViewModel() {
    private val _tendencia = MutableStateFlow<TendenciaResponse?>(null)
    val tendencia: StateFlow<TendenciaResponse?> = _tendencia.asStateFlow()
    private val _distribucion = MutableStateFlow<DistribucionCategoriaResponse?>(null)
    val distribucion: StateFlow<DistribucionCategoriaResponse?> = _distribucion.asStateFlow()
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargar(LocalDate.now().year, LocalDate.now().monthValue) }

    fun cargar(anio: Int, mes: Int) {
        viewModelScope.launch {
            _loading.value = true
            reporteRepository.tendenciaDiaria(anio, mes).onSuccess { _tendencia.value = it }
            reporteRepository.distribucionCategorias(anio, mes).onSuccess { _distribucion.value = it }
            _loading.value = false
        }
    }
}
package com.example.finalpro.Ui1.Screens.Metas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Remote.Dto.Request.MetaAhorroRequest
import com.example.finalpro.Data.Remote.Dto.Response.MetaAhorroResponse
import com.example.finalpro.Data.Repository.AlertaRepository
import com.example.finalpro.Data.Repository.MetaAhorroRepository
import com.example.finalpro.Data.Repository.ReporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MetasViewModel @Inject constructor(
    private val metaRepository: MetaAhorroRepository,
    private val reporteRepository: ReporteRepository,
    private val alertaRepository: AlertaRepository
) : ViewModel() {

    private val _metas = MutableStateFlow<List<MetaAhorroResponse>>(emptyList())
    val metas: StateFlow<List<MetaAhorroResponse>> = _metas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _alertas = MutableStateFlow<List<String>>(emptyList())
    val alertas: StateFlow<List<String>> = _alertas.asStateFlow()

    init { cargarMetas() }

    fun cargarMetas() {
        viewModelScope.launch {
            _loading.value = true
            metaRepository.listar().onSuccess { _metas.value = it }
            val hoy = LocalDate.now()
            reporteRepository.resumenMensual(hoy.year, hoy.monthValue)
                .onSuccess {
                    _balance.value = it.balance
                }
            alertaRepository.obtenerAlertas()
                .onSuccess { _alertas.value = it.alertas }
            _loading.value = false
        }
    }

    fun crearMeta(nombre: String, montoObjetivo: Double, fechaLimite: String?) {
        viewModelScope.launch {
            metaRepository.crear(MetaAhorroRequest(nombre, montoObjetivo, fechaLimite))
                .onSuccess { cargarMetas() }
        }
    }

    fun agregarAhorro(id: String, montoCOP: Double) {
        viewModelScope.launch {
            metaRepository.agregarAhorro(id, montoCOP)
                .onSuccess { cargarMetas() }
        }
    }

    fun editarObjetivo(id: String, nuevoObjetivo: Double) {
        viewModelScope.launch {
            val meta = _metas.value.find { it.id == id } ?: return@launch
            metaRepository.actualizar(
                id,
                MetaAhorroRequest(meta.nombre, nuevoObjetivo, meta.fechaLimite)
            ).onSuccess { cargarMetas() }
        }
    }

    fun eliminarMeta(id: String) {
        viewModelScope.launch {
            metaRepository.eliminar(id).onSuccess { cargarMetas() }
        }
    }
}
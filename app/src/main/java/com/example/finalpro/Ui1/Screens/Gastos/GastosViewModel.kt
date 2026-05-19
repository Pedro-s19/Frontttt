package com.example.finalpro.Ui1.Screens.Gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Remote.Dto.Request.GastoRequest
import com.example.finalpro.Data.Remote.Dto.Response.CategoriaResponse
import com.example.finalpro.Data.Remote.Dto.Response.GastoResponse
import com.example.finalpro.Data.Repository.CategoriaRepository
import com.example.finalpro.Data.Repository.GastoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val gastoRepository: GastoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {
    private val _gastos = MutableStateFlow<List<GastoResponse>>(emptyList())
    val gastos: StateFlow<List<GastoResponse>> = _gastos.asStateFlow()
    private val _categorias = MutableStateFlow<List<CategoriaResponse>>(emptyList())
    val categorias: StateFlow<List<CategoriaResponse>> = _categorias.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargarDatos() }

    fun cargarDatos() {
        viewModelScope.launch {
            _loading.value = true
            gastoRepository.listar().onSuccess { _gastos.value = it }
            categoriaRepository.listar().onSuccess { _categorias.value = it }
            _loading.value = false
        }
    }

    fun crearGasto(monto: Double, descripcion: String, fecha: String, categoriaId: String) {
        viewModelScope.launch {
            gastoRepository.crear(GastoRequest(monto, descripcion, fecha, categoriaId)).onSuccess { cargarDatos() }
        }
    }

    fun eliminarGasto(id: String) {
        viewModelScope.launch { gastoRepository.eliminar(id).onSuccess { cargarDatos() } }
    }
}
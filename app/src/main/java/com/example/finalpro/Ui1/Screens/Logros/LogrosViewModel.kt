package com.example.finalpro.Ui1.Screens.Logros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalpro.Data.Remote.Dto.Response.LogroResponse
import com.example.finalpro.Data.Repository.GamificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogrosViewModel @Inject constructor(
    private val gamificacionRepository: GamificacionRepository
) : ViewModel() {
    private val _logros = MutableStateFlow<List<LogroResponse>>(emptyList())
    val logros: StateFlow<List<LogroResponse>> = _logros.asStateFlow()
    private val _puntos = MutableStateFlow(0)
    val puntos: StateFlow<Int> = _puntos.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _loading.value = true
            gamificacionRepository.listarLogros().onSuccess { _logros.value = it }
            gamificacionRepository.obtenerPuntos().onSuccess { _puntos.value = it }
            _loading.value = false
        }
    }
}
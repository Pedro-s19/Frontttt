package com.example.finalpro.Data.Repository

import com.example.finalpro.Data.Remote.Api.ReporteApi
import com.example.finalpro.Data.Remote.Dto.Response.DistribucionCategoriaResponse
import com.example.finalpro.Data.Remote.Dto.Response.ResumenMensualResponse
import com.example.finalpro.Data.Remote.Dto.Response.TendenciaResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepository @Inject constructor(
    private val api: ReporteApi
) {
    suspend fun tendenciaDiaria(anio: Int, mes: Int, moneda: String = "COP"): Result<TendenciaResponse> = runCatching {
        val resp = api.tendenciaDiaria(anio, mes, moneda)
        if (resp.isSuccessful) resp.body()!! else throw Exception("Error ${resp.code()}: ${resp.message()}")
    }

    suspend fun distribucionCategorias(anio: Int, mes: Int, moneda: String = "COP"): Result<DistribucionCategoriaResponse> = runCatching {
        val resp = api.distribucionCategorias(anio, mes, moneda)
        if (resp.isSuccessful) resp.body()!! else throw Exception("Error ${resp.code()}: ${resp.message()}")
    }

    suspend fun resumenMensual(anio: Int, mes: Int, moneda: String = "COP"): Result<ResumenMensualResponse> = runCatching {
        val resp = api.resumenMensual(anio, mes, moneda)
        if (resp.isSuccessful) resp.body()!! else throw Exception("Error ${resp.code()}: ${resp.message()}")
    }
}


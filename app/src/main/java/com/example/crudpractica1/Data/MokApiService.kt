package com.example.crudpractica1.Data


import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MokApiService {

    @GET("equipos")
    suspend fun obtenerEquipos(): List<Equipo>

    @POST("equipos")
    suspend fun crearEquipo(@Body equipo: Equipo): Equipo

    @PUT("equipos/{id}")
    suspend fun actualizarEquipo(@Path("id") id: String, @Body equipo: Equipo): Equipo

    @DELETE("equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: String): Equipo
}
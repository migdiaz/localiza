package com.example.localizaamigos.modelo

import retrofit2.Call
import retrofit2.http.GET

interface ServicioUsuario {
    //El siguiente método sirve para buscar todos los usuarios que ya guardste
    @GET("api/usuario/localizacion")
    fun buscarTodos(): Call<ArrayList<Usuario>>
}
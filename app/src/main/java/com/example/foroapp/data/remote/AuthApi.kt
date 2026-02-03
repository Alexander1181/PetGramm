package com.example.foroapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<String>

    @POST("api/auth/register")
    suspend fun register(@Body userData: Map<String, String>): Response<String>
}

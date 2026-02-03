package com.example.foroapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RemoteModule {

    // URL base por defecto (Auth Service) - puerto 8080
    // Cambia la IP aquí si es necesario
    private const val BASE_IP = "192.168.100.12"
    
    private const val AUTH_URL = "http://$BASE_IP:8080/"
    private const val POST_URL = "http://$BASE_IP:8081/"
    private const val COMMENT_URL = "http://$BASE_IP:8082/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Instancia para Auth (8080)
    private val authRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(AUTH_URL)
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Instancia para Posts (8081)
    private val postRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(POST_URL)
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Instancia para Comments (8082)
    private val commentRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(COMMENT_URL)
        .client(okHttp)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T {
        // Seleccionamos la instancia correcta según la clase de la API
        val retrofitInstance = when (service) {
            PostApi::class.java -> postRetrofit
            CommentApi::class.java -> commentRetrofit
            else -> authRetrofit // Por defecto AuthApi
        }
        return retrofitInstance.create(service)
    }
}

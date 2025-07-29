package com.example.deflatam_chatapp.data.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Provee una instancia configurada de OkHttpClient para uso en la aplicación.
 */
object OkHttpClientProvider {
    /**
     * Retorna una instancia de OkHttpClient.
     * @return OkHttpClient configurado.
     */
    fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS) // Tiempo de espera de lectura.
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera de conexión.
            .writeTimeout(30, TimeUnit.SECONDS) // Tiempo de espera de escritura.
            .build()
    }
}
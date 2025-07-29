package com.example.deflatam_chatapp.data.network

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * Provee una instancia singleton de OkHttpClient para la gestión de conexiones de red.
 */
object OkHttpClientProvider {

    @Volatile
    private var instance: OkHttpClient? = null

    /**
     * Retorna una instancia singleton de OkHttpClient.
     * @return Instancia de [OkHttpClient].
     */
    fun getClient(): OkHttpClient =
        instance ?: synchronized(this) {
            instance ?: buildClient().also { instance = it }
        }

    /**
     * Construye y configura el OkHttpClient.
     * @return Un [OkHttpClient] configurado.
     */
    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la lectura.
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la conexión.
            .writeTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la escritura.
            .build()
    }
}

package com.example.deflatam_chatapp.di


import com.google.gson.Gson
import com.example.deflatam_chatapp.data.datasource.websocket.WebSocketManager
import com.example.deflatam_chatapp.data.network.OkHttpClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer dependencias relacionadas con la red.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provee una instancia de OkHttpClient.
     * @return Instancia de [OkHttpClient].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClientProvider.getClient()
    }

    /**
     * Provee una instancia de WebSocketManager.
     * @param okHttpClient Cliente OkHttp.
     * @param gson Instancia de Gson.
     * @return Instancia de [WebSocketManager].
     */
    @Provides
    @Singleton
    fun provideWebSocketManager(okHttpClient: OkHttpClient, gson: Gson): WebSocketManager {
        return WebSocketManager(okHttpClient, gson)
    }
}

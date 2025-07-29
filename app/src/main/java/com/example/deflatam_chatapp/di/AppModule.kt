package com.example.deflatam_chatapp.di


import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer dependencias a nivel de aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provee el contexto de la aplicación.
     * @param context Contexto de la aplicación inyectado por Hilt.
     * @return El contexto de la aplicación.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    /**
     * Provee una instancia de Gson para serialización/deserialización de JSON.
     * @return Instancia de [Gson].
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}

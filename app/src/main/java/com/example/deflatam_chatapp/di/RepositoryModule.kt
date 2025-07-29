package com.example.deflatam_chatapp.di


import com.example.deflatam_chatapp.data.datasource.local.OfflineDataSource
import com.example.deflatam_chatapp.data.datasource.remote.AuthRemoteDataSource
import com.example.deflatam_chatapp.data.datasource.remote.ChatRoomRemoteDataSource
import com.example.deflatam_chatapp.data.datasource.remote.MessageRemoteDataSource
import com.example.deflatam_chatapp.data.datasource.websocket.WebSocketManager
import com.example.deflatam_chatapp.data.repository.AuthRepositoryImpl
import com.example.deflatam_chatapp.data.repository.ChatRoomRepositoryImpl
import com.example.deflatam_chatapp.data.repository.MessageRepositoryImpl
import com.example.deflatam_chatapp.domain.repository.AuthRepository
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer las implementaciones de los repositorios.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provee la implementación de AuthRepository.
     * @param authRemoteDataSource Fuente de datos remota de autenticación.
     * @param offlineDataSource Fuente de datos local para offline.
     * @return Implementación de [AuthRepository].
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        offlineDataSource: OfflineDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(authRemoteDataSource, offlineDataSource)
    }

    /**
     * Provee la implementación de ChatRoomRepository.
     * @param chatRoomRemoteDataSource Fuente de datos remota de salas de chat.
     * @param offlineDataSource Fuente de datos local para offline.
     * @return Implementación de [ChatRoomRepository].
     */
    @Provides
    @Singleton
    fun provideChatRoomRepository(
        chatRoomRemoteDataSource: ChatRoomRemoteDataSource,
        offlineDataSource: OfflineDataSource
    ): ChatRoomRepository {
        return ChatRoomRepositoryImpl(chatRoomRemoteDataSource, offlineDataSource)
    }

    /**
     * Provee la implementación de MessageRepository.
     * @param messageRemoteDataSource Fuente de datos remota de mensajes.
     * @param webSocketManager Gestor de WebSocket.
     * @param offlineDataSource Fuente de datos local para offline.
     * @return Implementación de [MessageRepository].
     */
    @Provides
    @Singleton
    fun provideMessageRepository(
        messageRemoteDataSource: MessageRemoteDataSource,
        webSocketManager: WebSocketManager,
        offlineDataSource: OfflineDataSource
    ): MessageRepository {
        return MessageRepositoryImpl(messageRemoteDataSource, webSocketManager, offlineDataSource)
    }
}

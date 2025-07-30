package com.example.deflatam_chatapp.di


import com.example.deflatam_chatapp.domain.repository.AuthRepository
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import com.example.deflatam_chatapp.domain.repository.MessageRepository
import com.example.deflatam_chatapp.domain.usecase.GetChatRoomsUseCase
import com.example.deflatam_chatapp.domain.usecase.LoginUserUseCase
import com.example.deflatam_chatapp.domain.usecase.RegisterUserUseCase
import com.example.deflatam_chatapp.domain.usecase.SendMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer los casos de uso.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * Provee el caso de uso para iniciar sesión.
     * @param authRepository Repositorio de autenticación.
     * @return Instancia de [LoginUserUseCase].
     */
    @Provides
    @Singleton
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }

    /**
     * Provee el caso de uso para registrar un usuario.
     * @param authRepository Repositorio de autenticación.
     * @return Instancia de [RegisterUserUseCase].
     */
    @Provides
    @Singleton
    fun provideRegisterUserUseCase(authRepository: AuthRepository): RegisterUserUseCase {
        return RegisterUserUseCase(authRepository)
    }
/*
    *//**
     * Provee el caso de uso para obtener el usuario actual.
     * @param authRepository Repositorio de autenticación.
     * @return Instancia de [GetCurrentUserUseCase].
     *//*
    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authRepository)
    }*/
/*
    *//**
     * Provee el caso de uso para cerrar sesión.
     * @param authRepository Repositorio de autenticación.
     * @return Instancia de [LogoutUserUseCase].
     *//*
    @Provides
    @Singleton
    fun provideLogoutUserUseCase(authRepository: AuthRepository): LogoutUserUseCase {
        return LogoutUserUseCase(authRepository)
    }*/

    /**
     * Provee el caso de uso para obtener salas de chat.
     * @param chatRoomRepository Repositorio de salas de chat.
     * @return Instancia de [GetChatRoomsUseCase].
     */
    @Provides
    @Singleton
    fun provideGetChatRoomsUseCase(chatRoomRepository: ChatRoomRepository): GetChatRoomsUseCase {
        return GetChatRoomsUseCase(chatRoomRepository)
    }
/*
    *//**
     * Provee el caso de uso para crear una sala de chat.
     * @param chatRoomRepository Repositorio de salas de chat.
     * @return Instancia de [CreateChatRoomUseCase].
     *//*
    @Provides
    @Singleton
    fun provideCreateChatRoomUseCase(chatRoomRepository: ChatRoomRepository): CreateChatRoomUseCase {
        return CreateChatRoomUseCase(chatRoomRepository)
    }*/

    /**
     * Provee el caso de uso para enviar un mensaje.
     * @param messageRepository Repositorio de mensajes.
     * @return Instancia de [SendMessageUseCase].
     */
    @Provides
    @Singleton
    fun provideSendMessageUseCase(messageRepository: MessageRepository): SendMessageUseCase {
        return SendMessageUseCase(messageRepository)
    }
/*
    *//**
     * Provee el caso de uso para obtener mensajes.
     * @param messageRepository Repositorio de mensajes.
     * @return Instancia de [GetMessagesUseCase].
     *//*
    @Provides
    @Singleton
    fun provideGetMessagesUseCase(messageRepository: MessageRepository): GetMessagesUseCase {
        return GetMessagesUseCase(messageRepository)
    }

    *//**
     * Provee el caso de uso para enviar un archivo.
     * @param messageRepository Repositorio de mensajes.
     * @return Instancia de [SendFileUseCase].
     *//*
    @Provides
    @Singleton
    fun provideSendFileUseCase(messageRepository: MessageRepository): SendFileUseCase {
        return SendFileUseCase(messageRepository)
    }

    *//**
     * Provee el caso de uso para actualizar el estado de un mensaje.
     * @param messageRepository Repositorio de mensajes.
     * @return Instancia de [UpdateMessageStatusUseCase].
     *//*
    @Provides
    @Singleton
    fun provideUpdateMessageStatusUseCase(messageRepository: MessageRepository): UpdateMessageStatusUseCase {
        return UpdateMessageStatusUseCase(messageRepository)
    }

    *//**
     * Provee el caso de uso para observar mensajes de WebSocket.
     * @param messageRepository Repositorio de mensajes.
     * @return Instancia de [ObserveWebSocketMessagesUseCase].
     *//*
    @Provides
    @Singleton
    fun provideObserveWebSocketMessagesUseCase(messageRepository: MessageRepository): ObserveWebSocketMessagesUseCase {
        return ObserveWebSocketMessagesUseCase(messageRepository)
    }*/
}

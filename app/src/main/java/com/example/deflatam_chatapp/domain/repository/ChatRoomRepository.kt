package com.example.deflatam_chatapp.domain.repository

import com.example.deflatam_chatapp.domain.model.ChatRoom
import kotlinx.coroutines.flow.Flow


/**
 * Interfaz de repositorio para las operaciones relacionadas con las salas de chat.
 */
interface ChatRoomRepository {
    /**
     * Obtiene un flujo de todas las salas de chat disponibles.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    fun getChatRooms(): Flow<List<ChatRoom>>

    /**
     * Crea una nueva sala de chat.
     * @param chatRoom La sala de chat a crear.
     */
    suspend fun createChatRoom(chatRoom: ChatRoom)
}

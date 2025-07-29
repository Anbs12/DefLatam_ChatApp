package com.example.deflatam_chatapp.domain.usecase

import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.repository.ChatRoomRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener la lista de salas de chat disponibles.
 */
class GetChatRoomsUseCase(private val chatRoomRepository: ChatRoomRepository) {
    /**
     * Ejecuta la operación para obtener salas de chat.
     * @return Un [Flow] que emite un [Result] con una lista de [ChatRoom].
     */
    operator fun invoke(): Flow<Result<List<ChatRoom>>> {
        return chatRoomRepository.getChatRooms()
    }
}
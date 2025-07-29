package com.example.deflatam_chatapp.domain.repository

import com.example.deflatam_chatapp.domain.model.ChatRoom
import kotlinx.coroutines.flow.Flow


/**
 * Interfaz para las operaciones relacionadas con las salas de chat.
 */
interface ChatRoomRepository {
    /**
     * Obtiene una lista de todas las salas de chat disponibles.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    fun getChatRooms(): Flow<Result<List<ChatRoom>>>

    /**
     * Crea una nueva sala de chat.
     * @param chatRoom La sala de chat a crear.
     * @return El objeto [ChatRoom] creado si la operación es exitosa.
     */
    suspend fun createChatRoom(chatRoom: ChatRoom): Result<ChatRoom>

    /**
     * Se une a una sala de chat específica.
     * @param roomId El ID de la sala a la que unirse.
     * @param userId El ID del usuario que se une.
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun joinChatRoom(roomId: String, userId: String): Result<Unit>

    /**
     * Abandona una sala de chat específica.
     * @param roomId El ID de la sala a abandonar.
     * @param userId El ID del usuario que abandona.
     * @return Un [Result] indicando el éxito o fallo de la operación.
     */
    suspend fun leaveChatRoom(roomId: String, userId: String): Result<Unit>
}

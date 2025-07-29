package com.example.deflatam_chatapp.data.datasource.local


import com.example.deflatam_chatapp.data.database.dao.ChatRoomDao
import com.example.deflatam_chatapp.data.database.dao.MessageDao
import com.example.deflatam_chatapp.data.database.dao.UserDao
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.domain.model.ChatRoom
import com.example.deflatam_chatapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fuente de datos local para la persistencia offline utilizando Room Database.
 */
@Singleton
class OfflineDataSource @Inject constructor(
    private val userDao: UserDao,
    private val chatRoomDao: ChatRoomDao,
    private val messageDao: MessageDao
) {
    /**
     * Guarda un usuario en la base de datos local.
     * @param user El usuario a guardar.
     */
    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }

    /**
     * Obtiene un usuario por su ID de la base de datos local.
     * @param userId El ID del usuario.
     * @return El usuario si se encuentra, null de lo contrario.
     */
    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    /**
     * Guarda una lista de salas de chat en la base de datos local.
     * @param chatRooms La lista de salas a guardar.
     */
    suspend fun saveChatRooms(chatRooms: List<ChatRoom>) {
        chatRoomDao.insertAllChatRooms(chatRooms)
    }

    /**
     * Obtiene un flujo de salas de chat desde la base de datos local.
     * @return Un [Flow] que emite la lista de [ChatRoom].
     */
    fun getChatRooms(): Flow<List<ChatRoom>> {
        return chatRoomDao.getAllChatRooms()
    }

    /**
     * Guarda una lista de mensajes para una sala específica en la base de datos local.
     * @param roomId El ID de la sala.
     * @param messages La lista de mensajes a guardar.
     */
    suspend fun saveMessages(roomId: String, messages: List<Message>) {
        messageDao.insertAllMessages(messages)
    }

    /**
     * Obtiene un flujo de mensajes para una sala específica desde la base de datos local.
     * @param roomId El ID de la sala.
     * @return Un [Flow] que emite la lista de [Message].
     */
    fun getMessages(roomId: String): Flow<List<Message>> {
        return messageDao.getMessagesForRoom(roomId)
    }

    /**
     * Añade un solo mensaje a la base de datos local de una sala.
     * @param message El mensaje a añadir.
     */
    suspend fun addMessage(message: Message) {
        messageDao.insertMessage(message)
    }

    /**
     * Actualiza el estado de un mensaje en la base de datos local.
     * @param messageId El ID del mensaje a actualizar.
     * @param newStatus El nuevo estado del mensaje.
     */
    suspend fun updateMessageStatus(messageId: String, newStatus: String) {
        // Primero, obtener el mensaje para actualizarlo
        val message = messageDao.getMessagesForRoom(messageId).map { it.find { msg -> msg.id == messageId } }.singleOrNull()
        message?.let {
            messageDao.updateMessage(it.copy(status = Message.MessageStatus.valueOf(newStatus)))
        }
    }

    /**
     * Limpia todos los datos almacenados en caché (base de datos local).
     */
    suspend fun clearAllData() {
        userDao.deleteAllUsers()
        chatRoomDao.deleteAllChatRooms()
        messageDao.deleteAllMessages()
    }
}

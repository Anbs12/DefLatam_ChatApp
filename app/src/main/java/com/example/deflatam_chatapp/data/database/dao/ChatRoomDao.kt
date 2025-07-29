package com.example.deflatam_chatapp.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deflatam_chatapp.domain.model.ChatRoom
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz DAO (Data Access Object) para operaciones con la tabla de salas de chat.
 */
@Dao
interface ChatRoomDao {
    /**
     * Inserta una lista de salas de chat en la base de datos o las reemplaza si ya existen.
     * @param chatRooms La lista de salas a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChatRooms(chatRooms: List<ChatRoom>)

    /**
     * Obtiene un flujo de todas las salas de chat ordenadas por nombre.
     * @return Un [Flow] que emite una lista de [ChatRoom].
     */
    @Query("SELECT * FROM chat_rooms ORDER BY name ASC")
    fun getAllChatRooms(): Flow<List<ChatRoom>>

    /**
     * Obtiene una sala de chat por su ID.
     * @param roomId El ID de la sala.
     * @return La sala de chat si se encuentra, null de lo contrario.
     */
    @Query("SELECT * FROM chat_rooms WHERE id = :roomId")
    suspend fun getChatRoomById(roomId: String): ChatRoom?

    /**
     * Elimina todas las salas de chat de la tabla.
     */
    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAllChatRooms()
}

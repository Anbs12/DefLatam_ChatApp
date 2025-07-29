package com.example.deflatam_chatapp.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deflatam_chatapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz DAO (Data Access Object) para operaciones con la tabla de mensajes.
 */
@Dao
interface MessageDao {
    /**
     * Inserta un mensaje en la base de datos o lo reemplaza si ya existe.
     * @param message El mensaje a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    /**
     * Inserta una lista de mensajes en la base de datos o los reemplaza si ya existen.
     * @param messages La lista de mensajes a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<Message>)

    /**
     * Obtiene un flujo de mensajes para una sala específica, ordenados por marca de tiempo.
     * @param roomId El ID de la sala de chat.
     * @return Un [Flow] que emite una lista de [Message].
     */
    @Query("SELECT * FROM messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<Message>>

    /**
     * Actualiza un mensaje existente en la base de datos.
     * @param message El mensaje con los datos actualizados.
     * @return El número de filas actualizadas (usualmente 1 si el mensaje existe).
     */
    @Update
    suspend fun updateMessage(message: Message)

    /**
     * Elimina todos los mensajes de una sala específica.
     * @param roomId El ID de la sala de la cual eliminar los mensajes.
     */
    @Query("DELETE FROM messages WHERE roomId = :roomId")
    suspend fun deleteMessagesForRoom(roomId: String)

    /**
     * Elimina todos los mensajes de la tabla.
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}

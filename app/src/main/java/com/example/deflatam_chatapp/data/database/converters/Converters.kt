package com.example.deflatam_chatapp.data.database.converters

import androidx.room.TypeConverter
import com.example.deflatam_chatapp.domain.model.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Provee TypeConverters para Room Database.
 */
class Converters {
    private val gson = Gson()

    /**
     * Convierte una lista de strings a una cadena JSON.
     * @param list La lista de strings a convertir.
     * @return La cadena JSON resultante.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    /**
     * Convierte una cadena JSON a una lista de strings.
     * @param value La cadena JSON a convertir.
     * @return La lista de strings resultante.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) {
            return null
        }
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    /**
     * Convierte un enum MessageType a una cadena.
     * @param type El enum MessageType a convertir.
     * @return La cadena que representa el enum.
     */
    @TypeConverter
    fun fromMessageType(type: Message.MessageType): String {
        return type.name
    }

    /**
     * Convierte una cadena a un enum MessageType.
     * @param value La cadena a convertir.
     * @return El enum MessageType resultante.
     */
    @TypeConverter
    fun toMessageType(value: String): Message.MessageType {
        return Message.MessageType.valueOf(value)
    }

    /**
     * Convierte un enum MessageStatus a una cadena.
     * @param status El enum MessageStatus a convertir.
     * @return La cadena que representa el enum.
     */
    @TypeConverter
    fun fromMessageStatus(status: Message.MessageStatus): String {
        return status.name
    }

    /**
     * Convierte una cadena a un enum MessageStatus.
     * @param value La cadena a convertir.
     * @return El enum MessageStatus resultante.
     */
    @TypeConverter
    fun toMessageStatus(value: String): Message.MessageStatus {
        return Message.MessageStatus.valueOf(value)
    }
}

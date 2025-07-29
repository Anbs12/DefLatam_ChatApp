package com.example.deflatam_chatapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un usuario en el sistema de chat.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // ID único del usuario, también clave primaria de Room.
    val username: String, // Nombre de usuario.
    val email: String, // Correo electrónico del usuario.
    val avatarUrl: String = "" // URL del avatar del usuario (opcional).
)
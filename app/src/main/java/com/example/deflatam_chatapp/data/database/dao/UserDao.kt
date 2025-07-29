package com.example.deflatam_chatapp.data.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deflatam_chatapp.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz DAO (Data Access Object) para operaciones con la tabla de usuarios.
 */
@Dao
interface UserDao {
    /**
     * Inserta un usuario en la base de datos o lo reemplaza si ya existe.
     * @param user El usuario a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    /**
     * Obtiene un usuario por su ID.
     * @param userId El ID del usuario.
     * @return El usuario si se encuentra, null de lo contrario.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    /**
     * Elimina todos los usuarios de la tabla.
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

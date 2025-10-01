package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User): Long

    // Devuelve el último usuario (por uid descendente)
    @Query("SELECT * FROM users ORDER BY uid DESC LIMIT 1")
    suspend fun getLastUser(): User?

    // Elimina por id; retorna cantidad de filas eliminadas
    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteById(uid: Int): Int
}

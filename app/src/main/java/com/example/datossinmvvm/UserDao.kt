package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    // Retornamos Long para obtener el rowId insertado (útil para mostrar el id)
    @Insert
    suspend fun insert(user: User): Long
}

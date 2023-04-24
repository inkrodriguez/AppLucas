package com.inkrodriguez.applucas

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@androidx.room.Dao
interface Dao{

    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Update
    suspend fun updateUser(userEntity: UserEntity)

    @Query("UPDATE UserEntity SET latitude = :latitude, longitude = :longitude WHERE email = :email")
    fun updateLastLocation(latitude: String, longitude: String, email: String)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)


}
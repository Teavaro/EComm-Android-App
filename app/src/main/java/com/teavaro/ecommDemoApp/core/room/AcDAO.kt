package com.teavaro.ecommDemoApp.core.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AcDAO {
    @Transaction
    @Query("SELECT * FROM ACEntity")
    fun getACWithItems(): List<ACWithItems>

    @Insert
    fun saveAC(ac: ACEntity)

    @Query("SELECT * FROM ACEntity")
    fun getAllAcs(): List<ACEntity>

    @Query("Delete from ACEntity")
    fun removeAllAc()
}
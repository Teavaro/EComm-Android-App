package com.teavaro.ecommDemoApp.core.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(ItemEntity::class),(ACEntity::class), (ACItemCrossRef::class)],
    version = 15)
abstract class AppDb : RoomDatabase() {

    abstract fun itemDao(): ItemDAO
    abstract fun acDao(): AcDAO
}
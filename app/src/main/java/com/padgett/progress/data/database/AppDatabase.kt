package com.padgett.progress.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Client::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
}
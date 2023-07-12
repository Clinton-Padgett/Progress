package com.padgett.progress

import android.app.Application
import androidx.room.Room
import com.padgett.progress.data.ClientRepoImpl
import com.padgett.progress.data.database.AppDatabase

class ProgressApplication : Application() {

    companion object {
        const val DATABASE_NAME = "progress-database"
    }

    override fun onCreate() {
        super.onCreate()

        Dependencies.appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

        Dependencies.clientRepo = ClientRepoImpl()
    }
}
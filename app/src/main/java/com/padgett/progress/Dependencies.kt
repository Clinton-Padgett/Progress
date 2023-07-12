package com.padgett.progress

import com.padgett.progress.data.database.AppDatabase
import com.padgett.progress.domain.ClientRepo

object Dependencies {
    lateinit var appDatabase: AppDatabase
    lateinit var clientRepo: ClientRepo
}
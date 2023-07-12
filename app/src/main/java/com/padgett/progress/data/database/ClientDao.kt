package com.padgett.progress.data.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM Client")
    fun getClients(): Flow<List<Client>>
}
package com.example.flowerpassword.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): HistoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryItem)

    @Delete
    suspend fun delete(item: HistoryItem)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}

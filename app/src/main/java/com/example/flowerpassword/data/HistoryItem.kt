package com.example.flowerpassword.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a saved code (key) for password generation history.
 */
@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val timestamp: Long = System.currentTimeMillis()
)

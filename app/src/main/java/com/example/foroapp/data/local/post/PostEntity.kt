package com.example.foroapp.data.local.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val author: String,
    val caption: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

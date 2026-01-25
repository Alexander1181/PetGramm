package com.example.foroapp.data.local.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.foroapp.data.local.Converters

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val author: String,
    val caption: String,
    @TypeConverters(Converters::class)
    val imageUrls: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

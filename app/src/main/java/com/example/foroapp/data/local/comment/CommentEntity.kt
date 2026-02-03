package com.example.foroapp.data.local.comment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val author: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

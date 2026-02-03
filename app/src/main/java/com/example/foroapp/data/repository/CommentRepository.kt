package com.example.foroapp.data.repository

import com.example.foroapp.data.local.comment.CommentEntity

class CommentRepository(
    private val api: com.example.foroapp.data.remote.CommentApi
) {
    suspend fun getCommentsForPost(postId: Long): List<CommentEntity> {
        return try {
            val response = api.getCommentsByPost(postId)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addComment(postId: Long, author: String, text: String) {
        try {
            api.createComment(
                CommentEntity(
                    postId = postId,
                    author = author,
                    text = text
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteComment(commentId: Long) {
        try {
            api.deleteComment(commentId)
        } catch (e: Exception) {
             e.printStackTrace()
        }
    }
}

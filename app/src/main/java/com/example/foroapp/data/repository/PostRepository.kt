package com.example.foroapp.data.repository

import com.example.foroapp.data.local.post.PostEntity
import kotlinx.coroutines.flow.Flow

class PostRepository(private val api: com.example.foroapp.data.remote.PostApi) {
    
    // Ahora es una función suspendida, no un Flow, porque viene de la red
    suspend fun getAll(): List<PostEntity> {
        return try {
            val response = api.getAllPosts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPostById(postId: Long): PostEntity? {
        return try {
            val response = api.getPostById(postId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insert(post: PostEntity) {
        try {
            api.createPost(post)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun update(post: PostEntity) {
        try {
            api.updatePost(post)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(post: PostEntity) {
        try {
            // Asumimos que PostEntity tiene un ID válido.
            // Si el ID es autogenerado por Room, podría ser 0 antes de guardarse en remoto.
            // Pero aquí suponemos que estamos borrando algo que ya vino del servidor con ID.
            if (post.id != 0L) {
               api.deletePost(post.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

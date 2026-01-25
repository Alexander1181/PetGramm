package com.example.foroapp.data.repository

import com.example.foroapp.data.local.post.PostDao
import com.example.foroapp.data.local.post.PostEntity
import kotlinx.coroutines.flow.Flow

class PostRepository(private val postDao: PostDao) {
    val allPosts: Flow<List<PostEntity>> = postDao.getAllPosts()

    suspend fun insert(post: PostEntity) {
        postDao.insertPost(post)
    }
}

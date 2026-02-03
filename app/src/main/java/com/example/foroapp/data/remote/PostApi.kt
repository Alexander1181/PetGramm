package com.example.foroapp.data.remote

import com.example.foroapp.data.local.post.PostEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApi {
    // Port 8081
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<PostEntity>>

    @POST("api/posts")
    suspend fun createPost(@Body post: PostEntity): Response<PostEntity>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<PostEntity>

    @retrofit2.http.PUT("api/posts")
    suspend fun updatePost(@Body post: PostEntity): Response<PostEntity>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>
}

package com.example.foroapp.data.remote

import com.example.foroapp.data.local.comment.CommentEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApi {
    // Port 8082
    @GET("api/comments/posts/{postId}")
    suspend fun getCommentsByPost(@Path("postId") postId: Long): Response<List<CommentEntity>>

    @POST("api/comments")
    suspend fun createComment(@Body comment: CommentEntity): Response<CommentEntity>

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") id: Long): Response<Unit>
}

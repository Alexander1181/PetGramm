package com.example.foroapp.data

import com.example.foroapp.model.Post
import com.example.foroapp.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MockRepository {

    private val currentUser = User(
        username = "alex_rescata",
        email = "alex@example.com",
        avatarUrl = "https://i.pravatar.cc/150?u=alex"
    )

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    init {
        // Seed initial data
        val initialPosts = listOf(
            Post(
                userId = "user_2",
                username = "RefugioEsperanza",
                userAvatar = "https://i.pravatar.cc/150?u=refugio",
                imageUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1",
                caption = "Max busca un hogar amoroso. Es un chico muy bueno! 🐶 #adopta",
                likes = 45
            ),
            Post(
                userId = "user_3",
                username = "GatitosUnidos",
                userAvatar = "https://i.pravatar.cc/150?u=gatos",
                imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba",
                caption = "Luna está lista para ser adoptada. 🐱",
                likes = 89
            )
        )
        _posts.value = initialPosts
    }

    fun getCurrentUser(): User = currentUser

    fun addPost(imageUrl: String, caption: String) {
        val newPost = Post(
            userId = currentUser.id,
            username = currentUser.username,
            userAvatar = currentUser.avatarUrl,
            imageUrl = imageUrl,
            caption = caption
        )
        // Add to top of list
        _posts.update { currentList ->
            listOf(newPost) + currentList
        }
    }
}

package com.example.foroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foroapp.data.local.PostPreferences
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.repository.PostRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

import com.example.foroapp.data.local.notification.NotificationEntity
import com.example.foroapp.data.repository.UserRepository
import com.example.foroapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PostViewModel(
    private val repository: PostRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val preferences: PostPreferences
) : ViewModel() {

    // Mapa de Nombre -> Rol para mostrar emblemas
    val userRoles: StateFlow<Map<String, String>> = userRepository.allUsers
        .map { users -> users.associate { user -> user.name to user.role } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _allPosts = MutableStateFlow<List<PostEntity>>(emptyList())
    val allPosts: StateFlow<List<PostEntity>> = _allPosts

    init {
        refreshPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _allPosts.value = repository.getAll()
        }
    }

    // --- Estado de la función Like (Persistente) ---
    val likedPosts: StateFlow<Set<Long>> = preferences.likedPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun createPost(author: String, caption: String, imageUrls: List<String>) {
        viewModelScope.launch {
            repository.insert(
                PostEntity(
                    author = author,
                    caption = caption,
                    imageUrls = imageUrls
                )
            )
            refreshPosts() // Refrescar lista tras crear
        }
    }

    fun deletePost(post: PostEntity, reason: String? = null) {
        viewModelScope.launch {
            repository.delete(post)
            
            if (!reason.isNullOrBlank()) {
                val notification = NotificationEntity(
                    userId = post.author, // Asumiendo que author es el ID o nombre único
                    message = "Tu publicación '${post.caption.take(20)}...' fue eliminada. Razón: $reason"
                )
                notificationRepository.insert(notification)
            }
            refreshPosts() // Refrescar tras eliminar
        }
    }

    // --- Lógica de la función Like ---
    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            val currentLikes = likedPosts.value
            val isLiked = postId in currentLikes

            val post = repository.getPostById(postId) ?: return@launch

            val updatedPost = post.copy(
                likes = if (isLiked) post.likes - 1 else post.likes + 1
            )

            repository.update(updatedPost)

            val newLikes = if (isLiked) {
                currentLikes - postId
            } else {
                currentLikes + postId
            }
            preferences.saveLikedPosts(newLikes)
            refreshPosts() // Refrescar tras dar like (para ver el contador actualizado desde servidor si hubieramos optimizado, aunque aquí es local+remoto hibrido)
        }
    }
}

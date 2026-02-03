package com.example.foroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foroapp.data.local.PostPreferences
import com.example.foroapp.data.repository.PostRepository
import com.example.foroapp.data.repository.UserRepository
import com.example.foroapp.data.repository.CommentRepository

import com.example.foroapp.data.repository.NotificationRepository
import com.example.foroapp.ui.viewmodel.NotificationViewModel

class AppViewModelFactory(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    private val preferences: PostPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(postRepository, userRepository, notificationRepository, preferences) as T
            }
            modelClass.isAssignableFrom(CommentViewModel::class.java) -> {
                CommentViewModel(commentRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

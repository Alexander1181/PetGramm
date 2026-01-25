package com.example.foroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.repository.PostRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    val allPosts: StateFlow<List<PostEntity>> = repository.allPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createPost(author: String, caption: String, imageUrl: String) {
        viewModelScope.launch {
            repository.insert(
                PostEntity(
                    author = author,
                    caption = caption,
                    imageUrl = imageUrl
                )
            )
        }
    }
}

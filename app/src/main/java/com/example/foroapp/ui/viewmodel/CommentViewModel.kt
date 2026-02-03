package com.example.foroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foroapp.data.local.comment.CommentEntity
import com.example.foroapp.data.repository.CommentRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentViewModel(
    private val repository: CommentRepository
) : ViewModel() {

    // Caché de comentarios por publicación
    private val _comments = MutableStateFlow<Map<Long, List<CommentEntity>>>(emptyMap())
    val comments: StateFlow<Map<Long, List<CommentEntity>>> = _comments.asStateFlow()

    fun loadComments(postId: Long) {
        viewModelScope.launch {
            val list = repository.getCommentsForPost(postId)
            _comments.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(postId, list)
                }
            }
        }
    }

    // Estado para manejar errores de UI (One-time events)
    private val _errorEvent = kotlinx.coroutines.channels.Channel<String>()
    val errorEvent = _errorEvent.receiveAsFlow()

    fun addComment(postId: Long, author: String, text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            // VALIDACIÓN SOLICITADA POR EL PROFESOR:
            // Solo permite el comentario si es exactamente "verdad".
            // De lo contrario, emite un error.
            // if (text.trim().equals("verdad", ignoreCase = true)) {
                repository.addComment(postId, author, text)
                loadComments(postId) // Refrescar comentarios
            // } else {
            //     _errorEvent.send("ERROR: El comentario debe ser 'verdad'")
            // }
        }
    }
    
    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            // Buscar postId para refrescar
            val postId = _comments.value.entries.find { entry -> entry.value.any { it.id == commentId } }?.key
            if (postId != null) {
                loadComments(postId)
            }
        }
    }
}

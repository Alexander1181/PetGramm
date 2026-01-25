package com.example.foroapp.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val avatarUrl: String? = null
)

data class Pet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val species: String, // "DOG", "CAT"
    val ageMonths: Int,
    val description: String,
    val status: String // "ADOPTION", "RESERVED", "ADOPTED"
)

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val username: String, // Denormalized for simpler UI
    val userAvatar: String?,
    val imageUrl: String,
    val caption: String,
    val likes: Int = 0,
    val petId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

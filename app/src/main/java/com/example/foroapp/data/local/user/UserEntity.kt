package com.example.foroapp.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

//anotación para indicar que es una tabla de BD
@Entity(tableName = "users")
data class UserEntity(
    //identifica la llave primaria
    @PrimaryKey(autoGenerate = true) //se crea de manera automática
    val id: Long = 0L,

    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val nickname: String? = null,
    val profilePictureUrl: String? = null,
    val isBanned: Boolean = false,
    val role: String = "user" // roles: "admin", "mod", "user"
)

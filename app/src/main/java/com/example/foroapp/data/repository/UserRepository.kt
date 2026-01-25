package com.example.foroapp.data.repository

import com.example.foroapp.data.local.user.UserDao
import com.example.foroapp.data.local.user.UserEntity

class UserRepository(
    //inyecciones a todos los DAO que va a utilizar
    private val userDao: UserDao
){
    //ejecutar el login o inicio de sesion
    suspend fun login(email: String, pass: String): Result<UserEntity>{
        //verificar si elcorreo existe
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == pass){
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    //ejecutar el registro de un usuario nuevo
    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long>{
        //verificar si ya existe el usuario (por correo)
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("El correo ya está en uso"))
        } else{
            val id = userDao.insertar(
                UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = pass
                )
            )
            return Result.success(id)
        }
    }

    //modificar un usuario o eliminar un usuario (investigar)
}

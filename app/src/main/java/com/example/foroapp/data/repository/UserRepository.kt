package com.example.foroapp.data.repository

import com.example.foroapp.data.local.user.UserDao
import com.example.foroapp.data.local.user.UserEntity

class UserRepository(
    //inyecciones a todos los DAO que va a utilizar
    private val userDao: UserDao
){
    val allUsers = userDao.getAll()
    
    // Crear API Cliente (Fast Track: Creación directa)
    private val authApi: com.example.foroapp.data.remote.AuthApi = 
        com.example.foroapp.data.remote.RemoteModule.create(com.example.foroapp.data.remote.AuthApi::class.java)

    //ejecutar el login o inicio de sesion
    suspend fun login(email: String, pass: String): Result<UserEntity>{
        try {
            // 1. INTENTO LOGUEARME EN EL BACKEND (Spring Boot)
            val apiResponse = authApi.login(mapOf("email" to email, "password" to pass))
            
            if (apiResponse.isSuccessful) {
                // 2. Si el backend dice SI, busco mis datos locales para mantener el Rol
                val localUser = userDao.getByEmail(email)
                return if (localUser != null) {
                   if (localUser.isBanned) {
                       Result.failure(Exception("Tu cuenta ha sido suspendida."))
                   } else {
                       Result.success(localUser)
                   }
                } else {
                    // Caso Raro: Existe en Backend pero no en local (ej: reinstalacion)
                    // Solución Rápida: Creamos un usuario temporal
                   Result.success(UserEntity(name = "Usuario Remoto", email = email, phone = "", password = pass))
                }
            } else {
                // Si el backend falla, intentamos local por si el servidor está apagado
                 val localUser = userDao.getByEmail(email)
                 return if (localUser != null && localUser.password == pass) {
                    Result.success(localUser)
                 } else {
                    Result.failure(IllegalArgumentException("Credenciales inválidas (Remoto: ${apiResponse.code()})"))
                 }
            }
        } catch (e: Exception) {
            // Si el servidor no responde (ej: no prendiste VS Code), usamos Local
            val localUser = userDao.getByEmail(email)
             return if (localUser != null && localUser.password == pass) {
                Result.success(localUser)
             } else {
                Result.failure(e)
             }
        }
    }

    //ejecutar el registro de un usuario nuevo
    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long>{
        //verificar si ya existe el usuario (por correo)
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("El correo ya está en uso"))
        } else{
            val id = userDao.insertUser(
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
    suspend fun update(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun changePassword(email: String, newPass: String) {
        userDao.updatePassword(email, newPass)
    }

    suspend fun recoverPassword(email: String): Result<String> {
        val user = userDao.getByEmail(email)
        return if (user != null) {
            // Mock recovery: In a real app, send email. Here just return success.
            Result.success("Se ha enviado un correo de recuperación a $email")
        } else {
            Result.failure(Exception("El correo no está registrado"))
        }
    }
}

package com.example.foroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.foroapp.data.local.PostPreferences
import com.example.foroapp.data.local.database.AppDatabase
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.data.repository.CommentRepository
import com.example.foroapp.data.repository.PostRepository
import com.example.foroapp.data.repository.UserRepository
import com.example.foroapp.navigation.AppNavGraph
import com.example.foroapp.ui.theme.ForoAppTheme
import com.example.foroapp.ui.viewmodel.AppViewModelFactory
import com.example.foroapp.ui.viewmodel.AuthViewModel
import com.example.foroapp.ui.viewmodel.CommentViewModel
import com.example.foroapp.ui.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val db = remember { AppDatabase.getInstance(context) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(db) {
        launch(Dispatchers.IO) {
            val userDao = db.userDao()
            if (userDao.count() == 0) {
                val userSeed = listOf(
                    UserEntity(name = "Admin", email = "a@a.cl", phone = "12345678", password = "Admin123!", nickname = "El Admin", profilePictureUrl = "https://images.unsplash.com/photo-1517849845537-4d257902454a", role = "admin"),
                    UserEntity(name = "Moderador", email = "m@m.cl", phone = "87654321", password = "Mod123!", nickname = "El Mod", profilePictureUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", role = "mod"),
                    UserEntity(name = "Cliente", email = "c@c.cl", phone = "12345678", password = "Cliente123!", profilePictureUrl = "https://images.unsplash.com/photo-1543466835-00a7927eba01", role = "user")
                )
                userDao.insertAllUsers(userSeed)
            }
            
/*
            // DESACTIVADO POR MICROSERVICIOS:
            // La base de datos local de posts ya no se usa para mostrar datos.
            val postDao = db.postDao()
            if (postDao.count() == 0) {
              // ...
            }
*/

            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    ForoAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val userRepository = remember { UserRepository(db.userDao()) }
                
                // MICROSERVICIOS: Inyectamos las APIs remotas usando RemoteModule
                val postApi = remember { com.example.foroapp.data.remote.RemoteModule.create(com.example.foroapp.data.remote.PostApi::class.java) }
                val postRepository = remember { PostRepository(postApi) }

                val commentApi = remember { com.example.foroapp.data.remote.RemoteModule.create(com.example.foroapp.data.remote.CommentApi::class.java) }
                val commentRepository = remember { CommentRepository(commentApi) }
                val notificationRepository = remember { com.example.foroapp.data.repository.NotificationRepository(db.notificationDao()) }
                val preferences = remember { PostPreferences(context) }

                val factory = remember { AppViewModelFactory(userRepository, postRepository, commentRepository, notificationRepository, preferences) }
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                val postViewModel: PostViewModel = viewModel(factory = factory)
                val commentViewModel: CommentViewModel = viewModel(factory = factory)
                val notificationViewModel: com.example.foroapp.ui.viewmodel.NotificationViewModel = viewModel(factory = factory)

                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    postViewModel = postViewModel,
                    commentViewModel = commentViewModel,
                    notificationViewModel = notificationViewModel
                )
            }
        }
    }
}
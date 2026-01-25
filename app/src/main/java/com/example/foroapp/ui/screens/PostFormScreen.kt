package com.example.foroapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foroapp.data.MockRepository
import com.example.foroapp.ui.navigation.Screen

@Composable
fun CreatePostScreen(navController: NavController) {
    // Retrieve passed URI
    val imageUriString = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("imageUri")

    var caption by remember { mutableStateOf("") }
    
    if (imageUriString == null) {
        // Fallback if no image
        Text("No imagen seleccionada")
        Button(onClick = { navController.popBackStack() }) { Text("Volver") }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Nueva Publicación", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AsyncImage(
            model = imageUriString,
            contentDescription = "Preview",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            label = { Text("Escribe un pie de foto...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                MockRepository.addPost(imageUriString, caption)
                // Navigate back to Home and clear stack up to home
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("PUBLICAR")
        }
    }
}

package com.example.foroapp.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Funciones auxiliares para la cámara
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir, "IMG_$timeStamp.jpg")
}

private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

data class PostData(val author: String, val caption: String, val imageUrl: String)

@Composable
fun PetPostCard(post: PostData) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // Bordes bien redondeados
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Más espacio
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.author.take(1),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = post.author,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Like logic placeholder
                    var isLiked by remember { mutableStateOf(false) }
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.FavoriteBorder else Icons.Default.FavoriteBorder, // Logic would change icon
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* Comment */ }) {
                        Icon(Icons.Default.Message, contentDescription = "Comment")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoCamera: () -> Unit = {}
) {
    val bg = MaterialTheme.colorScheme.background
    val dummyPosts = remember {
        listOf(
            PostData("Alex", "¡Nuestra nueva mascota!", "https://images.unsplash.com/photo-1543466835-00a7927eba01"),
            PostData("Maria", "Paseo por el parque", "https://images.unsplash.com/photo-1517849845537-4d257902454a"),
            PostData("Juan", "Hora de dormir", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba")
        )
    }

    Scaffold(
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = onGoCamera,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Muro de Mascotas",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(dummyPosts) { post ->
                    PetPostCard(post)
                }

                if (!isLoggedIn) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Únete a la comunidad",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(onClick = onGoLogin) { Text("Entrar") }
                                OutlinedButton(onClick = onGoRegister) { Text("Registrarse") }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.ui.viewmodel.PostViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PetPostCard(post: PostEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
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
                    .padding(16.dp),
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

            if (post.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrls.first())
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                    contentScale = ContentScale.Crop
                )
            }

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
                    var isLiked by remember { mutableStateOf(false) }
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Me gusta",
                            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* Comment */ }) {
                        Icon(Icons.Default.Message, contentDescription = "Comentar")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    postViewModel: PostViewModel,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoCamera: () -> Unit = {}
) {
    val bg = MaterialTheme.colorScheme.background
    val dbPosts by postViewModel.allPosts.collectAsStateWithLifecycle()

    // Muro global de la comunidad
    val communityPosts = remember {
        listOf(
            PostEntity(author = "Luna", caption = "¡Jugando en el parque! 🌳", imageUrls = listOf("https://images.unsplash.com/photo-1543466835-00a7927eba01")),
            PostEntity(author = "Firulais", caption = "Hora de la siesta... 💤", imageUrls = listOf("https://images.unsplash.com/photo-1517849845537-4d257902454a")),
            PostEntity(author = "Michi", caption = "Explorando la casa 🐱", imageUrls = listOf("https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba")),
            PostEntity(author = "Nala", caption = "¡Nueva pelota! 🎾", imageUrls = listOf("https://images.unsplash.com/photo-1537151608828-ea2b11777ee8"))
        )
    }

    // Combinamos las del usuario (DB) con las de la comunidad
    val displayPosts = remember(dbPosts) {
        dbPosts + communityPosts
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

                items(displayPosts) { post ->
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

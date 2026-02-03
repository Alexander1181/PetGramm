package com.example.foroapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.ui.viewmodel.AuthViewModel
import com.example.foroapp.ui.viewmodel.PostViewModel
import com.example.foroapp.ui.viewmodel.CommentViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    onGoToSettings: () -> Unit,
    onGoToCamera: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val allPosts by postViewModel.allPosts.collectAsStateWithLifecycle()
    val likedPosts by postViewModel.likedPosts.collectAsStateWithLifecycle()

    // Filtrar publicaciones para mostrar solo las del usuario actual
    val userPosts = allPosts.filter { it.author == currentUser?.name }
    
    // Estado de la hoja de comentarios
    var showCommentSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<Long?>(null) }
    
    if (showCommentSheet && selectedPostId != null) {
        CommentBottomSheet(
            postId = selectedPostId!!,
            commentViewModel = commentViewModel,
            currentUser = currentUser,
            onDismiss = { showCommentSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* El título está ahora en el encabezado */ },
                actions = {
                    IconButton(onClick = onGoToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado del perfil
            item {
                currentUser?.let { user ->
                    ProfileHeader(user = user)
                }
            }

            // Publicaciones o mensaje vacío
            if (userPosts.isEmpty()) {
                item {
                    EmptyProfileContent(onGoToCamera = onGoToCamera)
                }
            } else {
                items(userPosts, key = { it.id }) { post ->
                    // Agregamos padding aquí porque lo quitamos del LazyColumn para que el encabezado ocupe todo el ancho
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                         val isLiked = likedPosts.contains(post.id)
                        PetPostCard(
                            post = post,
                            isLiked = isLiked,
                            showAdminActions = true, // En el perfil, el usuario siempre puede borrar sus propios posts
                            onLikeClick = { postViewModel.toggleLike(post.id) },
                            onDeleteClick = { postViewModel.deletePost(post) },
                            onCommentClick = {
                                selectedPostId = post.id
                                showCommentSheet = true
                                commentViewModel.loadComments(post.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: UserEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Foto de perfil
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                 // Icono de respaldo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Nombre / Apodo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.nickname ?: user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyProfileContent(onGoToCamera: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Aún no has compartido ninguna foto",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onGoToCamera) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("¡Haz tu primera publicación!")
            }
        }
    }
}

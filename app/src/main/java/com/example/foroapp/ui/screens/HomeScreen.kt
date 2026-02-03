package com.example.foroapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.ui.viewmodel.AuthViewModel
import com.example.foroapp.ui.viewmodel.CommentViewModel
import com.example.foroapp.ui.viewmodel.PostViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetPostCard(
    post: PostEntity,
    isLiked: Boolean,
    showAdminActions: Boolean,
    onLikeClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onCommentClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var reasonOption by remember { mutableStateOf("Normas de comunidad") }
    var customReason by remember { mutableStateOf("") }
    val options = listOf("Normas de comunidad", "Contenido inapropiado", "Spam", "Otro")

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Publicación (Admin)") },
            text = {
                Column {
                    Text("Selecciona una razón para informar al usuario:")
                    Spacer(modifier = Modifier.height(8.dp))
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reasonOption = option }
                        ) {
                            RadioButton(
                                selected = (option == reasonOption),
                                onClick = { reasonOption = option }
                            )
                            Text(text = option, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    if (reasonOption == "Otro") {
                        OutlinedTextField(
                            value = customReason,
                            onValueChange = { customReason = it },
                            label = { Text("Escribe la razón") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalReason = if (reasonOption == "Otro") customReason else reasonOption
                        onDeleteClick(finalReason)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = reasonOption != "Otro" || customReason.isNotBlank()
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.author.take(1), color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(8.dp))
                Text(text = post.author, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                if (showAdminActions) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                            )
                        }
                    }
                }
            }

            if (post.imageUrls.isNotEmpty()) {
                val pagerState = rememberPagerState { post.imageUrls.size }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                ) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(post.imageUrls[page]).crossfade(true).build(),
                            contentDescription = "Imagen de la publicación ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (post.imageUrls.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(post.imageUrls.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = post.caption, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateContentSize()
                    ) {
                        IconButton(onClick = onLikeClick) {
                            AnimatedContent(targetState = isLiked, transitionSpec = { scaleIn() togetherWith scaleOut() }, label = "LikeAnimation") { liked ->
                                if (liked) {
                                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Quitar me gusta", tint = MaterialTheme.colorScheme.error)
                                } else {
                                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Me gusta")
                                }
                            }
                        }
                        if (post.likes > 0) {
                            Text(
                                text = "${post.likes}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onCommentClick) {
                        Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = "Comentar")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoCamera: () -> Unit = {},
    onCommentClick: (Long) -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val displayPosts by postViewModel.allPosts.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val likedPosts by postViewModel.likedPosts.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (isLoggedIn) {
                FloatingActionButton(onClick = onGoCamera, containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
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
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Text(
                        text = "Muro de Mascotas",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(displayPosts, key = { it.id }) { post ->
                    val isOwnPost = currentUser?.name == post.author
                    val isAdmin = currentUser?.name == "Admin"
                    val showAdminActions = isOwnPost || isAdmin

                    val isLiked = likedPosts.contains(post.id)

                    PetPostCard(
                        post = post,
                        isLiked = isLiked,
                        showAdminActions = showAdminActions,
                        onLikeClick = { postViewModel.toggleLike(post.id) },
                        onDeleteClick = { reason -> postViewModel.deletePost(post, reason) },
                        onCommentClick = { onCommentClick(post.id) }
                    )
                }

                if (!isLoggedIn) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Únete a la comunidad", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    postId: Long,
    commentViewModel: CommentViewModel,
    currentUser: UserEntity?,
    onDismiss: () -> Unit
) {
    val comments by commentViewModel.comments.collectAsStateWithLifecycle()
    val postComments = comments[postId] ?: emptyList()
    var newComment by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 200.dp, max = 500.dp)
        ) {
            Text("Comentarios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (postComments.isEmpty()) {
                    item { Text("No hay comentarios aún. ¡Sé el primero!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) }
                } else {
                    items(postComments) { comment ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(comment.author, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text("• hace un momento", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Spacer(Modifier.weight(1f))
                                if (currentUser?.name == "Admin" || currentUser?.name == comment.author) {
                                    IconButton(
                                        onClick = { commentViewModel.deleteComment(comment.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (currentUser != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                commentViewModel.addComment(postId, currentUser.name, newComment)
                                newComment = ""
                            }
                        },
                        enabled = newComment.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                Text("Inicia sesión para comentar", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

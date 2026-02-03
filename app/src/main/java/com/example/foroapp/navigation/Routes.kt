package com.example.foroapp.navigation

//clase sellada para evitar rutas mal escritas o duplicadas
sealed class Route(val path: String){
    data object Login: Route("login")
    data object Home: Route("home")
    data object Register: Route("register")
    data object Camera: Route("camera")
    data object CreatePost: Route("createPost")
    data object Comments: Route("comments/{postId}") {
        fun createRoute(postId: Long) = "comments/$postId"
    }
    data object Notifications: Route("notifications")
}

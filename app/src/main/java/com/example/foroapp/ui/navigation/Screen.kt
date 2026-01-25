package com.example.foroapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object CreatePost : Screen("create_post")
}

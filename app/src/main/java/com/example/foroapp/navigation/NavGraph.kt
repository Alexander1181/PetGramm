package com.example.foroapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foroapp.ui.components.AppDrawer
import com.example.foroapp.ui.components.AppTopBar
import com.example.foroapp.ui.components.defaultDrawerItems
import com.example.foroapp.ui.navigation.Screen
import com.example.foroapp.ui.screens.CreatePostScreen
import com.example.foroapp.ui.screens.HomeScreen
import com.example.foroapp.ui.screens.LoginScreen
import com.example.foroapp.ui.screens.ProfileScreen
import com.example.foroapp.ui.screens.RegisterScreen
import com.example.foroapp.ui.screens.SettingsScreen
import com.example.foroapp.ui.screens.CameraWrapperScreen
import com.example.foroapp.ui.viewmodel.AuthViewModel
import com.example.foroapp.ui.viewmodel.PostViewModel
import com.example.foroapp.ui.viewmodel.CommentViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController, 
    authViewModel: AuthViewModel,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel,
    notificationViewModel: com.example.foroapp.ui.viewmodel.NotificationViewModel
    ){
    //manejar el estado del drawer (menu lateral desplegable)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    //uso de corutina para manipular el cierre/apertura del drawer
    val scope = rememberCoroutineScope()

    // Observar el estado de sesión
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    //Helpers de navegaciones (para poder reutilizarlos)
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) } //ir al Home
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } //ir al Registro
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) } //ir al Login
    val goCamera: () -> Unit = { navController.navigate(Route.Camera.path) } //ir a la Cámara
    val goProfile: () -> Unit = { navController.navigate(Screen.Profile.route) } //ir al Perfil
    val goSettings: () -> Unit = { navController.navigate(Screen.Settings.route) } //ir a Ajustes
    val logout: () -> Unit = {
        authViewModel.logout()
        goLogin()
    }

    //contenedor principal para nuestro menu lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { //contenido del menu
            AppDrawer( //llamamos al component AppDrawer
                currentRoute = null,
                userName = currentUser?.name,
                items = defaultDrawerItems(
                    isLoggedIn = isLoggedIn,
                    onHome = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goHome() // helper de redireccion
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goLogin() // helper de redireccion
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } //cierra el menu lateral
                        goRegister() // helper de redireccion
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        logout()
                    },
                    onProfile = {
                        scope.launch { drawerState.close() }
                        goProfile()
                    }
                )

            )
        }
    ) {
        //dibujamos la ubicacion del topbar y las screen de mi app
        Scaffold( //contendor que me permite crear topBar
            topBar = { //barra de navegación superior
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, //abre el drawer
                    onHome = goHome,
                    onRegister = goRegister,
                    onLogin = goLogin,
                    isLoggedIn = isLoggedIn,
                    onLogout = logout,
                    onNotifications = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Route.Notifications.path) 
                    }
                )
            }
        ) { innerPadding -> //padding para evitar solapar contenidos
            //construir el contenedor para mostrar los destinos de las navegaciones
            NavHost(
                navController = navController, //controlador de navegacion
                startDestination = Route.Login.path, // ARREGLO: Iniciar en Login, no en Home
                modifier = Modifier.padding(innerPadding) //respeta el espacio del topBar
            ){
                //screen aceptadas para dibujar
                composable(Route.Home.path){
                    HomeScreen(
                        isLoggedIn = isLoggedIn,
                        authViewModel = authViewModel,
                        postViewModel = postViewModel,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoCamera = goCamera,
                        onCommentClick = { postId -> navController.navigate(Route.Comments.createRoute(postId)) }
                    )
                }
                composable(Route.Camera.path) {
                    CameraWrapperScreen(navController = navController)
                }
                composable(Route.CreatePost.path) {
                    val imageUris = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<List<String>>("imageUris")
                    
                    CreatePostScreen(
                        imageUris = imageUris,
                        authViewModel = authViewModel,
                        postViewModel = postViewModel,
                        onPostSuccess = {
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.Comments.path) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId")?.toLongOrNull() ?: 0L
                    com.example.foroapp.ui.screens.CommentsScreen(
                        postId = postId,
                        commentViewModel = commentViewModel,
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Route.Login.path){
                    LoginScreen(
                        vm = authViewModel,
                        onLoginOkNavigateHome = goHome, // ARREGLO: Navegar a Home al tener éxito
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path){
                    RegisterScreen(
                        vm = authViewModel,
                        onRegisterOkNavigate = goLogin, // Al registrarse, ir al login
                        onGoLogin = goLogin
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        postViewModel = postViewModel,
                        commentViewModel = commentViewModel,
                        onGoToSettings = goSettings,
                        onGoToCamera = goCamera
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(authViewModel = authViewModel)
                }
                composable(Route.Notifications.path) {
                    com.example.foroapp.ui.screens.NotificationScreen(
                        authViewModel = authViewModel,
                        notificationViewModel = notificationViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

}

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
import com.example.foroapp.ui.screen.HomeScreen
import com.example.foroapp.ui.screen.LoginScreenVm
import com.example.foroapp.ui.screen.RegisterScreenVm
import com.example.foroapp.ui.screens.CameraWrapperScreen
import com.example.foroapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController, //libreria para manipular navegación (controlar navegacion)
    authViewModel: AuthViewModel
    ){
    //manejar el estado del drawer (menu lateral desplegable)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    //uso de corutina para manipular el cierre/apertura del drawer
    val scope = rememberCoroutineScope()

    // Observar el estado de sesión
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    //Helpers de navegaciones (para poder reutilizarlos)
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) } //ir al Home
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } //ir al Registro
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) } //ir al Login
    val goCamera: () -> Unit = { navController.navigate(Route.Camera.path) } //ir a la Cámara
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
                    onLogout = logout
                )
            }
        ) { innerPadding -> //padding para evitar solapar contenidos
            //construir el contenedor para mostrar los destinos de las navegaciones
            NavHost(
                navController = navController, //controlador de navegacion
                startDestination = Route.Login.path, // FIX: Start at Login, not Home
                modifier = Modifier.padding(innerPadding) //respeta el espacio del topBar
            ){
                //screen aceptadas para dibujar
                composable(Route.Home.path){
                    HomeScreen(
                        isLoggedIn = isLoggedIn,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoCamera = goCamera
                    )
                }
                composable(Route.Camera.path) {
                    CameraWrapperScreen(navController = navController)
                }
                composable(Route.Login.path){
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = goHome, // FIX: Navigate to Home on success
                        onGoRegister = goRegister
                    )
                }
                composable(Route.Register.path){
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisterOkNavigate = goLogin, // Al registrarse, ir al login
                        onGoLogin = goLogin
                    )
                }
            }
        }
    }

}

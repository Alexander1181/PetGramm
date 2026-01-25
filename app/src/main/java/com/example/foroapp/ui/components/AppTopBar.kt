package com.example.foroapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit, //abre el  menu desplegable
    onHome: () -> Unit, //redirige al home
    onLogin: () -> Unit, //redirige al login
    onRegister: () -> Unit, //redirige al registro
    isLoggedIn: Boolean,
    onLogout: () -> Unit
){
    //creamos una variable que recuerde el estado
    //del menu desplegable (menu de 3 puntos en el topBar)
    var showMenu by remember { mutableStateOf(false) }

    //barra alineada en el centro del topBar
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "ForoApp",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1, //cantidad de lineas en que se puede mostrar el texto
                overflow = TextOverflow.Ellipsis //agrega ... si no se puede mostrar el texto completo

            )
        },
        //icono de hamburguesa para el menu desplegable
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = onHome) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }
            if (!isLoggedIn) {
                IconButton(onClick = onLogin) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Login")
                }
                IconButton(onClick = onRegister) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Register")
                }
            } else {
                IconButton(onClick = onLogout) {
                    Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Logout")
                }
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Ver Más")
            }
            DropdownMenu(
                expanded = showMenu, //si esta abierto o cerrado
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Ir al Home")},
                    onClick = { showMenu = false; onHome() }
                )
                if (!isLoggedIn) {
                    DropdownMenuItem(
                        text = { Text("Ir al Inicio de Sesión")},
                        onClick = { showMenu = false; onLogin() }
                    )
                    DropdownMenuItem(
                        text = { Text("Ir al Registro")},
                        onClick = { showMenu = false; onRegister() }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión")},
                        onClick = { showMenu = false; onLogout() }
                    )
                }

            }
        }
    )


}

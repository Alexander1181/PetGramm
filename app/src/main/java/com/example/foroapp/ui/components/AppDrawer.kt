package com.example.foroapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector


//pequeña clase para la estructura de las opciones del menu
data class DrawerItem(
    val label: String, //texto del item del menú
    val icon: ImageVector, //icono del item del menú
    val onClick: () -> Unit //indicar la función de redireccion del item del menú
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>, //lista con los items del menu a mostrar
    modifier: Modifier = Modifier //modificador de diseño para el modal del menu
){
    //crea la ventana modal para el menu lateral desplegable
    ModalDrawerSheet(
        modifier = modifier
    ){
        //dibujar todos los items del menu
        //recordando que vienen en una lista
        items.forEach { item -> //guarda en la variable item cada elemento que consiga en la lista
            NavigationDrawerItem( //muestra los item del menu de la lista con diseño
                label = { Text( item.label ) },
                selected = false, //identifica si el item del menu esta seleccionado de manera automática
                onClick = item.onClick, //evento a ejecutar la funcion del item del menu
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier,
                colors = NavigationDrawerItemDefaults.colors() //estilo por defecto
            )
        }
    }
}

//funcion para rellenar la lista de items del menu
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List <DrawerItem> = listOf(
    DrawerItem("Ir a la Página Principal", Icons.Filled.Home, onHome),
    DrawerItem("Ir al Inicio de sesión", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Ir al Registro", Icons.Filled.Person, onRegister)
)

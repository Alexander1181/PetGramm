package com.example.foroapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


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
    userName: String? = null,
    modifier: Modifier = Modifier //modificador de diseño para el modal del menu
){
    //crea la ventana modal para el menu lateral desplegable
    ModalDrawerSheet(
        modifier = modifier
    ){
        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userName ?: "Invitado",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = if (userName != null) "Miembro de Pet'sGramm" else "Inicia sesión para participar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
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
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onLogout: () -> Unit
): List <DrawerItem> {
    val items = mutableListOf<DrawerItem>()
    
    items.add(DrawerItem("Ir a la Página Principal", Icons.Filled.Home, onHome))
    
    if (isLoggedIn) {
        items.add(DrawerItem("Cerrar Sesión", Icons.Filled.ExitToApp, onLogout))
    } else {
        items.add(DrawerItem("Ir al Inicio de sesión", Icons.Filled.AccountCircle, onLogin))
        items.add(DrawerItem("Ir al Registro", Icons.Filled.Person, onRegister))
    }
    
    return items
}

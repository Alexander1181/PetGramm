package com.example.foroapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.HorizontalDivider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.ui.viewmodel.AuthViewModel

import com.example.foroapp.domain.validation.validateNameLettersOnly
import com.example.foroapp.domain.validation.validatePhoneDigitsOnly

@Composable
fun SettingsScreen(authViewModel: AuthViewModel) {
    val currentUserState by authViewModel.currentUser.collectAsStateWithLifecycle()
    val currentUser: com.example.foroapp.data.local.user.UserEntity? = currentUserState
    val context = LocalContext.current

    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var editablePhone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var editableNickname by remember(currentUser) { mutableStateOf(currentUser?.nickname ?: "") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ajustes de Perfil", style = MaterialTheme.typography.headlineMedium)

        // Correo electrónico (Solo lectura)
        OutlinedTextField(
            value = currentUser?.email ?: "",
            onValueChange = {},
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Nombre (Editable)
        OutlinedTextField(
            value = name,
            onValueChange = { 
                name = it
                nameError = validateNameLettersOnly(it)
            },
            label = { Text("Nombre Completo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if(nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

        // Apodo (Editable)
        OutlinedTextField(
            value = editableNickname,
            onValueChange = { editableNickname = it },
            label = { Text("Apodo (opcional)") },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        // Teléfono (Editable)
        OutlinedTextField(
            value = editablePhone,
            onValueChange = { 
                editablePhone = it
                phoneError = validatePhoneDigitsOnly(it)
            },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            isError = phoneError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if(phoneError != null) Text(phoneError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

        Button(
            onClick = { 
                if (nameError == null && phoneError == null && name.isNotBlank() && editablePhone.isNotBlank()) {
                     authViewModel.updateProfile(name, editablePhone, editableNickname)
                     Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor corrija los errores", Toast.LENGTH_SHORT).show()
                }
            },
            // Enable button only if there are changes and no errors
            enabled = (name != currentUser?.name || editablePhone != currentUser?.phone || editableNickname != currentUser?.nickname),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Change Password Section
        Text("Seguridad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        ChangePasswordSection(authViewModel)
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        
        // About Section
        Text("Acerca de", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        AboutSection()

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ChangePasswordSection(vm: AuthViewModel) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cambiar Contraseña", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = oldPass, onValueChange = { oldPass = it },
                label = { Text("Contraseña Actual") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newPass, onValueChange = { newPass = it },
                label = { Text("Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPass, onValueChange = { confirmPass = it },
                label = { Text("Confirmar Nueva Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.changePassword(oldPass, newPass, confirmPass, 
                        onSuccess = { 
                            msg = "Contraseña actualizada correctamente" 
                            isError = false
                            oldPass = ""; newPass = ""; confirmPass = ""
                        },
                        onError = {
                            msg = it
                            isError = true
                        }
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Actualizar")
            }
            if (msg != null) {
                Text(
                    text = msg!!, 
                    color = if(isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AboutSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Versión de la App", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("PetGram v1.0.0", style = MaterialTheme.typography.bodyMedium)
            Text("Desarrollado con ❤️ para mascotas.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

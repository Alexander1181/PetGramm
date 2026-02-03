package com.example.foroapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foroapp.ui.viewmodel.AuthViewModel
import com.example.foroapp.domain.model.Country
import com.example.foroapp.domain.model.supportedCountries
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    onRegisterOkNavigate: () -> Unit,
    onGoLogin: () -> Unit
){
    val state by vm.register.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if(state.success){
            // Sugerencia: Limpiar el estado de registro si fuera necesario
            onRegisterOkNavigate()
        }
    }

    RegisterScreen(
        name = state.name,
        phone = state.phone,
        email = state.email,
        pass = state.pass,
        confirm = state.confirm,
        nameError = state.nameError,
        phoneError = state.phoneError,
        emailError = state.emailError,
        passError = state.passError,
        confirmError = state.confirmError,
        errorMsg = state.errorMsg,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        onNameChange = vm::onNameChange,
        onPhoneChange = vm::onPhoneChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onCountryChange = vm::onCountryChange,
        selectedCountry = state.selectedCountry,
        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

@Composable
fun RegisterScreen(
    name: String,
    phone: String,
    email: String,
    pass: String,
    confirm: String,
    nameError: String?,
    phoneError: String?,
    emailError: String?,
    passError: String?,
    confirmError: String?,
    errorMsg: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onCountryChange: (Country) -> Unit,
    selectedCountry: Country,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
){
    val bg = MaterialTheme.colorScheme.tertiaryContainer
    val scrollState = rememberScrollState()

    // Control de visibilidad de contraseñas
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState), // Scroll por si el teclado tapa campos
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.width(16.dp))

            // NOMBRE
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre Completo") },
                singleLine = true,
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if(nameError != null) Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))

            Spacer(Modifier.width(8.dp))
 
            // TELEFONO con Selector de País
            var expanded by remember { mutableStateOf(false) }

            Row(modifier = Modifier.fillMaxWidth()) {
                // Selector de País
                Box(modifier = Modifier.weight(0.35f)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart)
                    ) {
                        Text("${selectedCountry.flag} ${selectedCountry.prefix}")
                        Icon(Icons.Filled.ArrowDropDown, "Select Country")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        supportedCountries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text("${country.flag} ${country.name} (${country.prefix})") },
                                onClick = {
                                    onCountryChange(country)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))
                
                // Campo de Texto del Teléfono
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Teléfono (${selectedCountry.digits} dígs)") },
                    singleLine = true,
                    isError = phoneError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(0.65f)
                )
            }
            if(phoneError != null) Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
             
             // ANTES ERA:
            // TELEFONO

            // CORREO
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo Electrónico") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if(emailError != null) Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))

            // COTRASEÑA
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passError != null,
                visualTransformation = if(showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(imageVector = if(showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if(passError != null) Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))

            // CONFIRMAR CONTRASEÑA
            OutlinedTextField(
                value = confirm,
                onValueChange = onConfirmChange,
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                isError = confirmError != null,
                visualTransformation = if(showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(imageVector = if(showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if(confirmError != null) Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(16.dp))

            // BOTON REGISTRAR
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if(isSubmitting){
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrando...")
                } else {
                    Text("Registrar")
                }
            }
            if(errorMsg != null){
                Spacer(Modifier.width(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta, Iniciar Sesión")
            }
        }
    }
}

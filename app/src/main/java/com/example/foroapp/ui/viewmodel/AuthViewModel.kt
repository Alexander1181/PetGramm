package com.example.foroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foroapp.domain.validation.validateConfirm
import com.example.foroapp.domain.validation.validateEmail
import com.example.foroapp.domain.validation.validateNameLettersOnly
import com.example.foroapp.domain.validation.validatePhoneDigitsOnly
import com.example.foroapp.domain.validation.validateStringPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.foroapp.data.repository.UserRepository
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.domain.model.Country
import com.example.foroapp.domain.model.supportedCountries
import com.example.foroapp.domain.validation.validatePhoneLength

//elementos para manipular los estados de mis formularios
data class LoginUiState(
    //campos del formulario
    val email: String = "",
    val pass: String = "",
    //mostrar errores de cada campo del formulario
    val emailError: String? = null,
    val passError: String? = null,
    //variable para error global del formulario. Ejemplo: credenciales incorrectas
    val errorMsg: String? = null,
    //variables para manejar los estados del formulario
    val isSubmitting: Boolean = false, //saber si el formulario se esta ejecutando
    val canSubmit: Boolean = false, //saber si el botón está activo
    val success: Boolean= false //saber si el formulario se ejcuto de manera correcta
)

data class RegisterUiState(
    //campos del formulario
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",
    val selectedCountry: Country = supportedCountries.first(),
    //mostrar errores de cada campo del formulario
    val nameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    //variable para error global del formulario. Ejemplo: correo ya registrado
    val errorMsg: String? = null,
    //variables para manejar los estados del formulario
    val isSubmitting: Boolean = false, //saber si el formulario se esta ejecutando
    val canSubmit: Boolean = false, //saber si el botón está activo
    val success: Boolean= false //saber si el formulario se ejcuto de manera correcta
)



class AuthViewModel(
    //el repositorio que va a usar
    private val repository: UserRepository
): ViewModel(){
    //persistir la coleccion de usuarios para todas las diferentes instancias de este archivo

    //variables para manejar los flujos de estado desde la UI
    private val _login = MutableStateFlow(LoginUiState()) //estado interno para el login
    val login: StateFlow<LoginUiState> = _login //copia de la anterior para que se pueda ver sus datos

    private val _register = MutableStateFlow(RegisterUiState()) //estado interno para el registro
    val register: StateFlow<RegisterUiState> = _register //copia de la anterior para que se pueda ver sus datos

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    //funciones de manejo de los formularios
    //Login

    //para habilitar/deshabilitar boton iniciar sesion
    private fun recomputeLoginCanSubmit(){
        val s = _login.value //obtenemos todos los datos actuales del formulario
        val can = s.emailError == null && s.pass.isNotBlank() && s.email.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    //unir las validaciones a sus respectivos campos
    fun onLoginEmailChange(value: String){
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }
    fun onLoginPassChange(value: String){
        // FIXED BUG: changed email = value to pass = value
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    fun submitLogin(){
        val s = _login.value //guardo os datos actuales del formulario
        //verifico si ya se esta ejecutando para no ejecutar denuevo
        if(!s.canSubmit || s.isSubmitting) return
        //ejecuto una corutina
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            
            //buscar en memoria si los datos son correctos
            val  result = repository.login(s.email.trim(), s.pass)

            //actualizamos los datos del state del formulario
            _login.update {
                if(result.isSuccess){
                    val user = result.getOrNull()
                    _isLoggedIn.value = true
                    _currentUser.value = user
                    it.copy(isSubmitting = false,success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false,success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }

            }

        }
    }

    //limpiar campos al finalizar formulario
    fun clearLoginResult(){
        _login.update { it.copy(success = false, errorMsg = "") }
    }

    //REGISTRO
    //para habilitar/deshabilitar boton registrar
    private fun recomputeRegisterCanSubmit(){
        val s = _register.value //obtenemos todos los datos actuales del formulario
        val noErrors = listOf(s.nameError,s.emailError,s.phoneError,
            s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank()
                && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }
    fun onNameChange(value: String){
        val x = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(name = x, nameError = validateNameLettersOnly(x))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterEmailChange(value:String){
        _register.update {
            it.copy(email = value, emailError = validateEmail(value))
        }
        recomputeRegisterCanSubmit()
    }
    fun onCountryChange(country: Country){
        _register.update { it.copy(selectedCountry = country) }
        // Re-validar el teléfono si cambia el país
        val currentPhone = _register.value.phone
        if(currentPhone.isNotEmpty()){
            val error = validatePhoneLength(currentPhone, country.digits)
            _register.update { it.copy(phoneError = error) }
        }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String){
        val digitsOnly = value.filter { it.isDigit() }
        val country = _register.value.selectedCountry
        _register.update {
            it.copy(
                phone = digitsOnly, 
                phoneError = validatePhoneLength(digitsOnly, country.digits) ?: validatePhoneDigitsOnly(digitsOnly)
            )
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterPassChange(value: String){
        _register.update { it.copy(pass = value, passError = validateStringPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass,it.confirm))}
        recomputeRegisterCanSubmit()
    }
    fun onConfirmChange(value: String){
        _register.update {
            it.copy(confirm = value, confirmError = validateConfirm(it.pass, value))
        }
        recomputeRegisterCanSubmit()
    }
    fun submitRegister(){
        val s = _register.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            
            //registramos
            val result = repository.register(
                name = s.name,
                email = s.email,
                phone = s.phone,
                pass = s.pass
            )

            _register.update {
                if(result.isSuccess){
                    it.copy(isSubmitting = false,success = true, errorMsg = null)
                } else {
                    it.copy(isSubmitting = false,success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }
        }
    }
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _login.value = LoginUiState()
        _register.value = RegisterUiState()
    }

    fun updateProfile(name: String, phone: String, nickname: String?) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val updatedUser = user.copy(name = name, phone = phone, nickname = nickname)
                repository.update(updatedUser)
                _currentUser.value = updatedUser // Update the UI immediately
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String, confirmPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                if (user.password != oldPass) {
                    onError("La contraseña actual no es correcta.")
                    return@launch
                }
                if (newPass != confirmPass) {
                    onError("Las nuevas contraseñas no coinciden.")
                    return@launch
                }
                val passError = validateStringPassword(newPass)
                if (passError != null) {
                    onError(passError)
                    return@launch
                }
                
                repository.changePassword(user.email, newPass)
                // Update local user with new pass
                _currentUser.value = user.copy(password = newPass)
                onSuccess()
            }
        }
    }

    fun recoverPassword(email: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.recoverPassword(email)
            onResult(res.getOrElse { it.message ?: "Error" })
        }
    }
}

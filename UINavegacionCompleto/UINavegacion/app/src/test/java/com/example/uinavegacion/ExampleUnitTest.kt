package com.example.uinavegacion

import com.example.uinavegacion.domain.validation.validateConfirm
import com.example.uinavegacion.domain.validation.validateEmail
import com.example.uinavegacion.domain.validation.validateNameLettersOnly
import com.example.uinavegacion.domain.validation.validatePhoneDigitsOnly
import com.example.uinavegacion.domain.validation.validateStringPassword
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias locales para las funciones de validación.
 * Se ejecutan en la JVM local (no se necesita emulador).
 */
class ValidatorsTest {

    // --- Nombre ---
    @Test
    fun nombreVacio_retornaError() {
        assertEquals("El nombre es obligatorio", validateNameLettersOnly(""))
    }

    @Test
    fun nombreConNumeros_retornaError() {
        assertEquals("Solo se aceptan letras y espacios", validateNameLettersOnly("Juan123"))
    }

    @Test
    fun nombreValido_retornaNull() {
        assertNull(validateNameLettersOnly("Juan Pérez"))
    }

    // --- Email ---
    @Test
    fun emailVacio_retornaError() {
        assertEquals("El correo es obligatorio", validateEmail(""))
    }

    @Test
    fun emailInvalido_retornaError() {
        assertEquals("Formato de correo inválido", validateEmail("juan"))
        assertEquals("Formato de correo inválido", validateEmail("juan@"))
        assertEquals("Formato de correo inválido", validateEmail("@gmail.com"))
    }

    @Test
    fun emailValido_retornaNull() {
        assertNull(validateEmail("juan@example.com"))
    }

    // --- Teléfono ---
    @Test
    fun telefonoNoNumerico_retornaError() {
        assertEquals("Solo deben ser números", validatePhoneDigitsOnly("1234abc"))
    }

    @Test
    fun telefonoCorto_retornaError() {
        assertEquals("Debe tener entre 8 y 15 numeros", validatePhoneDigitsOnly("123"))
    }

    @Test
    fun telefonoValido_retornaNull() {
        assertNull(validatePhoneDigitsOnly("12345678"))
    }

    // --- Contraseña ---
    @Test
    fun passwordCorta_retornaError() {
        assertEquals("La contraseña debe tener más de 8 carácteres", validateStringPassword("Pass1!"))
    }

    @Test
    fun passwordSinMayuscula_retornaError() {
        assertEquals("Debe tener al menos 1 mayúscula", validateStringPassword("password1!"))
    }

    @Test
    fun passwordSinMinuscula_retornaError() {
        assertEquals("Debe tener al menos 1 minúscula", validateStringPassword("PASSWORD1!"))
    }

    @Test
    fun passwordSinNumero_retornaError() {
        assertEquals("Debe tener al menos 1 número", validateStringPassword("Password!"))
    }

    @Test
    fun passwordSinSimbolo_retornaError() {
        assertEquals("Debe tener al menos 1 símbolo", validateStringPassword("Password123"))
    }

    @Test
    fun passwordValida_retornaNull() {
        assertNull(validateStringPassword("Password123!"))
    }

    // --- Confirmar Contraseña ---
    @Test
    fun confirmPasswordDiferente_retornaError() {
        assertEquals("Las contraseñas deben ser iguales", validateConfirm("123", "124"))
    }

    @Test
    fun confirmPasswordIgual_retornaNull() {
        assertNull(validateConfirm("123", "123"))
    }
}
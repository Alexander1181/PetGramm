package com.petgram.auth_service.controller;

import com.petgram.auth_service.dto.UserRequest;
import com.petgram.auth_service.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth Management", description = "Endpoints para registro e inicio de sesión de usuarios")
public class AuthController {

    // Simulación de base de datos en memoria (simple)
    private Map<String, String> usersDb = new HashMap<>();

    @Operation(summary = "Registrar nuevo usuario", description = "Registra un usuario con correo y contraseña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "El usuario ya existe o datos inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest userData) {
        String email = userData.getEmail();
        String password = userData.getPassword();

        if (usersDb.containsKey(email)) {
            return ResponseEntity.badRequest().body(new AuthResponse("Error: El usuario ya existe", null));
        }
        usersDb.put(email, password);
        return ResponseEntity.ok(new AuthResponse("Usuario registrado con éxito: " + email, null));
    }

    @Operation(summary = "Iniciar Sesión", description = "Valida credenciales y retorna un token simulado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest loginData) {
        String email = loginData.getEmail();
        String password = loginData.getPassword();

        if (usersDb.containsKey(email) && usersDb.get(email).equals(password)) {
            // En un caso real, aquí devolveríamos un Token JWT
            return ResponseEntity.ok(new AuthResponse("Login Exitoso", "LOGIN_EXITOSO_TOKEN_FALSO_12345"));
        }

        return ResponseEntity.status(401).body(new AuthResponse("Error: Credenciales inválidas", null));
    }
}
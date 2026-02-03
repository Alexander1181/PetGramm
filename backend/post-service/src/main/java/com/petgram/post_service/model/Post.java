package com.petgram.post_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Título del post", example = "Mi mascota feliz")
    private String title;

    @Schema(description = "Contenido o descripción del post", example = "Hoy paseamos por el parque y fue genial.")
    private String content;

    @Schema(description = "Autor del post", example = "juan.perez@email.com")
    private String authorName; // Simplificado para microservicio, guardamos el nombre o email

    @Schema(description = "URL de la imagen", example = "https://example.com/imagen.jpg")
    private String imageUrl;

    @Schema(description = "Cantidad de likes", example = "10")
    private int likes;
}

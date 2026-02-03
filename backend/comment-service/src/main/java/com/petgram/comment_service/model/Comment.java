package com.petgram.comment_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID del post al que pertenece", example = "10")
    private Long postId; // foreign key lógica al servicio de posts

    @Schema(description = "Autor del comentario", example = "maria.gomez@email.com")
    private String authorName;

    @Schema(description = "Contenido del comentario", example = "¡Qué bonita foto!")
    private String content;

    @Schema(description = "Timestamp de creación", example = "1678886400000")
    private long timestamp;
}

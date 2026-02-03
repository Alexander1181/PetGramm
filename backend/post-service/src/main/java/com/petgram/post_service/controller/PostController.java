package com.petgram.post_service.controller;

import com.petgram.post_service.model.Post;
import com.petgram.post_service.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Management", description = "Endpoints para crear, listar y gestionar posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Operation(summary = "Obtener todos los posts", description = "Retorna una lista de todos los posts disponibles.")
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Operation(summary = "Crear Nuevo Post", description = "Guarda un nuevo post en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @Operation(summary = "Obtener Post por ID", description = "Retorna un post específico buscando por su ID.")
    @GetMapping("/{id}")
    public Post getPostById(@Parameter(description = "ID único del post a buscar") @PathVariable Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Operation(summary = "Actualizar Post", description = "Actualiza el contenido de un post existente.")
    @PutMapping
    public Post updatePost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @Operation(summary = "Eliminar Post", description = "Elimina un post de la base de datos por su ID.")
    @DeleteMapping("/{id}")
    public void deletePost(@Parameter(description = "ID único del post a eliminar") @PathVariable Long id) {
        postRepository.deleteById(id);
    }
}

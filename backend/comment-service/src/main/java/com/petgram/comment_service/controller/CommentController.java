package com.petgram.comment_service.controller;

import com.petgram.comment_service.model.Comment;
import com.petgram.comment_service.repository.CommentRepository;
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
@RequestMapping("/api/comments")
@Tag(name = "Comment Management", description = "Endpoints para gestionar comentarios en los posts")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Operation(summary = "Obtener comentarios por Post", description = "Lista todos los comentarios asociados a un Post específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentarios encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)))
    })
    @GetMapping("/posts/{postId}")
    public List<Comment> getCommentsByPost(
            @Parameter(description = "ID del post del cual buscar comentarios") @PathVariable Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Operation(summary = "Crear Comentario", description = "Añade un nuevo comentario a un post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)))
    })
    @PostMapping
    public Comment createComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del comentario") @RequestBody Comment comment) {
        comment.setTimestamp(System.currentTimeMillis());
        return commentRepository.save(comment);
    }

    @Operation(summary = "Eliminar Comentario", description = "Elimina un comentario por su ID.")
    @DeleteMapping("/{id}")
    public void deleteComment(@Parameter(description = "ID del comentario a eliminar") @PathVariable Long id) {
        commentRepository.deleteById(id);
    }
}

package com.TodoList.TodoList_Backend.controller;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;
import com.TodoList.TodoList_Backend.response.ApiResponse;
import com.TodoList.TodoList_Backend.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Gestion des Tâches", description = "API CRUD pour la gestion de la To-Do List")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @Operation(summary = "Créer une tâche")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tâche créée avec succès",
                    content = @Content(schema = @Schema(implementation = TodoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Titre déjà existant")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> create(@Valid @RequestBody TodoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tâche créée avec succès", todoService.create(dto)));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les tâches")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<ApiResponse<List<TodoResponseDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Tâches récupérées avec succès", todoService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une tâche par son ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche trouvée",
                    content = @Content(schema = @Schema(implementation = TodoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> getById(
            @Parameter(description = "UUID de la tâche", required = true) @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(todoService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une tâche")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche modifiée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Titre déjà existant")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> update(
            @Parameter(description = "UUID de la tâche", required = true) @PathVariable UUID id,
            @Valid @RequestBody TodoRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Tâche modifiée avec succès", todoService.update(id, dto)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Marquer une tâche comme terminée")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> updateStatut(
            @Parameter(description = "UUID de la tâche", required = true) @PathVariable UUID id,
            @Valid @RequestBody TodoStatusDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour avec succès", todoService.updateStatut(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une tâche")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche supprimée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "UUID de la tâche", required = true) @PathVariable UUID id) {
        todoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tâche supprimée avec succès", null));
    }
}

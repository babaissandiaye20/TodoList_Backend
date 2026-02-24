package com.TodoList.TodoList_Backend.controller;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import com.TodoList.TodoList_Backend.helper.TodoHelper;
import com.TodoList.TodoList_Backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Gestion des Tâches", description = "API CRUD pour la gestion de la To-Do List")
public class TodoController {

    private final TodoHelper todoHelper;

    // ============================================
    // POST - CRÉER UNE TÂCHE
    // ============================================

    @PostMapping
    @Operation(summary = "Créer une tâche", description = "Ajoute une nouvelle tâche avec statut EN_COURS par défaut")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tâche créée avec succès",
                    content = @Content(schema = @Schema(implementation = TodoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Titre déjà existant")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> create(
            @Valid @RequestBody TodoRequestDto dto) {
        TodoResponseDto response = todoHelper.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tâche créée avec succès", response));
    }

    // ============================================
    // GET - LISTER TOUTES LES TÂCHES
    // ============================================

    @GetMapping
    @Operation(
            summary = "Lister toutes les tâches",
            description = "Retourne toutes les tâches avec filtres optionnels par statut et/ou plage de dates de création"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<ApiResponse<List<TodoResponseDto>>> getAll(
            @Parameter(description = "Filtrer par statut : EN_COURS ou TERMINEE")
            @RequestParam(required = false) TodoStatus statut,
            @Parameter(description = "Date de début (format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin (format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        List<TodoResponseDto> todos = (statut != null || dateDebut != null || dateFin != null)
                ? todoHelper.getAllWithFilters(statut, dateDebut, dateFin)
                : todoHelper.getAll();

        return ResponseEntity.ok(ApiResponse.success("Tâches récupérées avec succès", todos));
    }

    // ============================================
    // GET - TÂCHES EN RETARD
    // ============================================

    @GetMapping("/en-retard")
    @Operation(
            summary = "Tâches en retard",
            description = "Retourne les tâches EN_COURS dont la date prévue de fin est dépassée"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<ApiResponse<List<TodoResponseDto>>> getEnRetard() {
        return ResponseEntity.ok(ApiResponse.success("Tâches en retard récupérées", todoHelper.getAllEnRetard()));
    }

    // ============================================
    // GET - RÉCUPÉRER UNE TÂCHE PAR ID
    // ============================================

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une tâche", description = "Retourne une tâche par son identifiant UUID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche trouvée",
                    content = @Content(schema = @Schema(implementation = TodoResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> getById(
            @Parameter(description = "UUID de la tâche", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(todoHelper.getById(id)));
    }

    // ============================================
    // PUT - MODIFIER UNE TÂCHE
    // ============================================

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une tâche", description = "Met à jour le titre et la description d'une tâche")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche modifiée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Titre déjà existant")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> update(
            @Parameter(description = "UUID de la tâche", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody TodoRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Tâche modifiée avec succès", todoHelper.update(id, dto)));
    }

    // ============================================
    // PATCH - CHANGER LE STATUT
    // ============================================

    @PatchMapping("/{id}/status")
    @Operation(summary = "Changer le statut", description = "Marque une tâche comme EN_COURS ou TERMINEE")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Statut invalide"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<TodoResponseDto>> updateStatut(
            @Parameter(description = "UUID de la tâche", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody TodoStatusDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Statut mis à jour avec succès", todoHelper.updateStatut(id, dto)));
    }

    // ============================================
    // DELETE - SUPPRIMER UNE TÂCHE
    // ============================================

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une tâche", description = "Supprime définitivement une tâche par son UUID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tâche supprimée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "UUID de la tâche", required = true)
            @PathVariable UUID id) {
        todoHelper.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tâche supprimée avec succès", null));
    }
}

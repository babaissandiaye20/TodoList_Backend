package com.TodoList.TodoList_Backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * Réponse API standardisée pour les réponses SUCCÈS.
 * Assure une cohérence dans les réponses JSON.
 *
 * Pour les erreurs, utiliser ErrorResponse (classe séparée).
 *
 * @param <T> Type des données retournées
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;

    private int status;

    private String message;

    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String errorCode;

    // ============================================
    // MÉTHODES FACTORY POUR SUCCÈS
    // ============================================

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Opération réussie")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message("Ressource créée avec succès")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<Page<T>> paginated(Page<T> page) {
        return ApiResponse.<Page<T>>builder()
                .success(true)
                .status(200)
                .message("Opération réussie")
                .data(page)
                .build();
    }

    public static <T> ApiResponse<Page<T>> paginated(String message, Page<T> page) {
        return ApiResponse.<Page<T>>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(page)
                .build();
    }
}

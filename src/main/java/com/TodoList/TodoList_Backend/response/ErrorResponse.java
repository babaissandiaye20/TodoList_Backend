package com.TodoList.TodoList_Backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private int status;
    private String message;
    private String errorCode;
    private String detail;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private List<FieldError> errors;

    // ============================================
    // MÉTHODES FACTORY
    // ============================================

    public static ErrorResponse error(String message, int status, String errorCode) {
        return ErrorResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static ErrorResponse error(String message, String detail, int status, String errorCode) {
        return ErrorResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .detail(detail)
                .errorCode(errorCode)
                .build();
    }

    public static ErrorResponse notFound(String resourceName) {
        return ErrorResponse.builder()
                .success(false)
                .status(404)
                .message(resourceName + " non trouvé(e)")
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    public static ErrorResponse conflict(String resourceName) {
        return ErrorResponse.builder()
                .success(false)
                .status(409)
                .message(resourceName + " existe déjà")
                .errorCode("RESOURCE_ALREADY_EXISTS")
                .build();
    }

    public static ErrorResponse badRequest(String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(400)
                .message(message)
                .errorCode("BAD_REQUEST")
                .build();
    }

    public static ErrorResponse internalError(String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(500)
                .message(message)
                .errorCode("INTERNAL_ERROR")
                .build();
    }

    public static ErrorResponse validationError(String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .success(false)
                .status(400)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .errors(errors)
                .build();
    }

    // ============================================
    // INNER CLASS - ERREUR DE CHAMP
    // ============================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}

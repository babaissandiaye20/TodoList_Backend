package com.TodoList.TodoList_Backend.exception;

import com.TodoList.TodoList_Backend.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions.
 *
 * D'après la documentation Spring Boot (Context7) :
 * "You can define a class annotated with @ControllerAdvice to customize
 * the JSON document returned for a particular controller and/or exception type."
 *
 * Codes HTTP gérés :
 * - 400 : Requête invalide / erreurs de validation
 * - 404 : Ressource non trouvée
 * - 409 : Ressource déjà existante (conflit)
 * - 500 : Erreur interne
 */
@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================================
    // 404 - RESSOURCE NON TROUVÉE
    // ============================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Ressource non trouvée: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.notFound(ex.getResourceName()));
    }

    // ============================================
    // 409 - RESSOURCE DÉJÀ EXISTANTE
    // ============================================

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.warn("Ressource déjà existante: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.error(ex.getMessage(), 409, "RESOURCE_ALREADY_EXISTS"));
    }

    // ============================================
    // 400 - REQUÊTE INVALIDE
    // ============================================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        log.warn("Requête invalide: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.badRequest(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        log.warn("Erreurs de validation: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.validationError("Erreurs de validation", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argument invalide: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.badRequest(ex.getMessage()));
    }

    // ============================================
    // 500 - ERREURS INTERNES
    // ============================================

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalError(InternalErrorException ex) {
        log.error("Erreur interne: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.internalError(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);

        String detail = ex.getMessage();
        if (detail == null || detail.trim().isEmpty()) {
            detail = "Une erreur serveur interne est survenue.";
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error("Erreur inattendue", detail, 500, "INTERNAL_ERROR"));
    }

    // ============================================
    // HELPERS
    // ============================================

    private ErrorResponse.FieldError mapFieldError(FieldError springError) {
        return ErrorResponse.FieldError.builder()
                .field(springError.getField())
                .message(springError.getDefaultMessage())
                .rejectedValue(springError.getRejectedValue())
                .build();
    }
}

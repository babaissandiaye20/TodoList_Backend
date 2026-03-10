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

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private List<FieldError> errors;

    public static ErrorResponse notFound(String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(404)
                .message(message)
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    public static ErrorResponse conflict(String message) {
        return ErrorResponse.builder()
                .success(false)
                .status(409)
                .message(message)
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

    public static ErrorResponse validationError(String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .success(false)
                .status(400)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .errors(errors)
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

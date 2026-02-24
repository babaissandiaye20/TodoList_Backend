package com.TodoList.TodoList_Backend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class InternalErrorException extends RuntimeException {

    private String errorCode = "INTERNAL_ERROR";
    private int status = 500;
    private String details;

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InternalErrorException(String message, String errorCode, int status, String details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }
}

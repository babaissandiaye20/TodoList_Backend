package com.TodoList.TodoList_Backend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ResourceAlreadyExistsException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    private String errorCode = "RESOURCE_ALREADY_EXISTS";
    private int status = 409;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existe déjà avec %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceAlreadyExistsException(String message, String errorCode, int status) {
        super(message);
        this.resourceName = "Unknown";
        this.fieldName = "unknown";
        this.fieldValue = null;
        this.errorCode = errorCode;
        this.status = status;
    }
}

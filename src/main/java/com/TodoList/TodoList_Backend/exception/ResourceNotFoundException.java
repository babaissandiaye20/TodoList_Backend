package com.TodoList.TodoList_Backend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    private int status = 404;
    private String errorCode = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, Object fieldValue) {
        super(String.format("%s non trouvé avec la valeur: '%s'", resourceName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = "id";
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé avec %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

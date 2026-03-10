package com.TodoList.TodoList_Backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(resourceName + " non trouvé avec l'identifiant: " + id);
    }
}

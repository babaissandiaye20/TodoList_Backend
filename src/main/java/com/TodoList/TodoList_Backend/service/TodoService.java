package com.TodoList.TodoList_Backend.service;

import com.TodoList.TodoList_Backend.entity.Todo;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TodoService {

    Todo save(Todo todo);

    Todo findById(UUID id);

    List<Todo> findAll();

    List<Todo> findAllByStatut(TodoStatus statut);

    List<Todo> findAllWithFilters(TodoStatus statut, LocalDate dateDebut, LocalDate dateFin);

    List<Todo> findAllEnRetard();

    Todo update(UUID id, Todo todo);

    Todo updateStatut(UUID id, TodoStatus statut);

    void delete(UUID id);

    boolean existsByTitre(String titre);

    boolean existsByTitreAndIdNot(String titre, UUID id);
}

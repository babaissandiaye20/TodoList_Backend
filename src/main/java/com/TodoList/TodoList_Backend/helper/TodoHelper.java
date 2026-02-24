package com.TodoList.TodoList_Backend.helper;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TodoHelper {

    TodoResponseDto create(TodoRequestDto dto);

    TodoResponseDto getById(UUID id);

    List<TodoResponseDto> getAll();

    List<TodoResponseDto> getAllByStatut(TodoStatus statut);

    List<TodoResponseDto> getAllWithFilters(TodoStatus statut, LocalDate dateDebut, LocalDate dateFin);

    List<TodoResponseDto> getAllEnRetard();

    TodoResponseDto update(UUID id, TodoRequestDto dto);

    TodoResponseDto updateStatut(UUID id, TodoStatusDto dto);

    void delete(UUID id);
}

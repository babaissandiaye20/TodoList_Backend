package com.TodoList.TodoList_Backend.service;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;

import java.util.List;
import java.util.UUID;

public interface TodoService {

    TodoResponseDto create(TodoRequestDto dto);

    TodoResponseDto getById(UUID id);

    List<TodoResponseDto> getAll();

    TodoResponseDto update(UUID id, TodoRequestDto dto);

    TodoResponseDto updateStatut(UUID id, TodoStatusDto dto);

    void delete(UUID id);
}

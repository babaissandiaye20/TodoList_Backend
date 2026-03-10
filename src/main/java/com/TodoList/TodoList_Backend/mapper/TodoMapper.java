package com.TodoList.TodoList_Backend.mapper;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.entity.Todo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequestDto dto) {
        return Todo.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .build();
    }

    public TodoResponseDto toResponseDto(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .titre(todo.getTitre())
                .description(todo.getDescription())
                .statut(todo.getStatut())
                .build();
    }

    public List<TodoResponseDto> toResponseDtoList(List<Todo> todos) {
        return todos.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}

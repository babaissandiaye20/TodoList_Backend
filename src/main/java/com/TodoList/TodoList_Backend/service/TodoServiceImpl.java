package com.TodoList.TodoList_Backend.service;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;
import com.TodoList.TodoList_Backend.entity.Todo;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import com.TodoList.TodoList_Backend.exception.BadRequestException;
import com.TodoList.TodoList_Backend.exception.ResourceAlreadyExistsException;
import com.TodoList.TodoList_Backend.exception.ResourceNotFoundException;
import com.TodoList.TodoList_Backend.mapper.TodoMapper;
import com.TodoList.TodoList_Backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    @Transactional
    public TodoResponseDto create(TodoRequestDto dto) {
        if (todoRepository.existsByTitre(dto.getTitre())) {
            throw new ResourceAlreadyExistsException(
                    "Une tâche avec le titre '" + dto.getTitre() + "' existe déjà");
        }
        Todo todo = todoMapper.toEntity(dto);
        return todoMapper.toResponseDto(todoRepository.save(todo));
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponseDto getById(UUID id) {
        return todoMapper.toResponseDto(findTodoById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponseDto> getAll() {
        return todoMapper.toResponseDtoList(todoRepository.findAllTodos());
    }

    @Override
    @Transactional
    public TodoResponseDto update(UUID id, TodoRequestDto dto) {
        if (todoRepository.existsByTitreAndIdNot(dto.getTitre(), id)) {
            throw new ResourceAlreadyExistsException(
                    "Une tâche avec le titre '" + dto.getTitre() + "' existe déjà");
        }
        Todo existing = findTodoById(id);
        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        return todoMapper.toResponseDto(todoRepository.save(existing));
    }

    @Override
    @Transactional
    public TodoResponseDto updateStatut(UUID id, TodoStatusDto dto) {
        Todo existing = findTodoById(id);

        if (existing.getStatut() == dto.getStatut()) {
            throw new BadRequestException(
                    "La tâche est déjà au statut " + dto.getStatut());
        }

        existing.setStatut(dto.getStatut());
        return todoMapper.toResponseDto(todoRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Todo todo = findTodoById(id);
        if (todo.getStatut() != TodoStatus.TERMINEE) {
            throw new BadRequestException("Seules les tâches terminées peuvent être supprimées");
        }
        todoRepository.delete(todo);
    }

    private Todo findTodoById(UUID id) {
        return todoRepository.findTodoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", id));
    }
}

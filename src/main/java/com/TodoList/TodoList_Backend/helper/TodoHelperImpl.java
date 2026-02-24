package com.TodoList.TodoList_Backend.helper;

import com.TodoList.TodoList_Backend.dto.TodoRequestDto;
import com.TodoList.TodoList_Backend.dto.TodoResponseDto;
import com.TodoList.TodoList_Backend.dto.TodoStatusDto;
import com.TodoList.TodoList_Backend.entity.Todo;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import com.TodoList.TodoList_Backend.exception.BadRequestException;
import com.TodoList.TodoList_Backend.exception.ResourceAlreadyExistsException;
import com.TodoList.TodoList_Backend.mapper.TodoMapper;
import com.TodoList.TodoList_Backend.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoHelperImpl implements TodoHelper {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    @Override
    public TodoResponseDto create(TodoRequestDto dto) {
        log.debug("Création d'une tâche: {}", dto.getTitre());

        if (todoService.existsByTitre(dto.getTitre())) {
            throw new ResourceAlreadyExistsException(
                    "Une tâche avec le titre '" + dto.getTitre() + "' existe déjà",
                    "RESOURCE_ALREADY_EXISTS",
                    409);
        }

        // Validation de la cohérence des dates
        validateDates(dto.getDateDebut(), dto.getDatePrevueFin());

        Todo todo = todoMapper.toEntity(dto);
        if (todo.getDateDebut() == null) {
            todo.setDateDebut(LocalDate.now());
        }
        Todo saved = todoService.save(todo);
        return todoMapper.toResponseDto(saved);
    }

    @Override
    public TodoResponseDto getById(UUID id) {
        log.debug("Récupération de la tâche: {}", id);
        return todoMapper.toResponseDto(todoService.findById(id));
    }

    @Override
    public List<TodoResponseDto> getAll() {
        log.debug("Récupération de toutes les tâches");
        return todoMapper.toResponseDtoList(todoService.findAll());
    }

    @Override
    public List<TodoResponseDto> getAllByStatut(TodoStatus statut) {
        log.debug("Récupération des tâches par statut: {}", statut);
        return todoMapper.toResponseDtoList(todoService.findAllByStatut(statut));
    }

    @Override
    public List<TodoResponseDto> getAllWithFilters(TodoStatus statut, LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Filtrage des tâches - statut: {}, dateDebut: {}, dateFin: {}", statut, dateDebut, dateFin);
        return todoMapper.toResponseDtoList(todoService.findAllWithFilters(statut, dateDebut, dateFin));
    }

    @Override
    public List<TodoResponseDto> getAllEnRetard() {
        log.debug("Récupération des tâches en retard");
        return todoMapper.toResponseDtoList(todoService.findAllEnRetard());
    }

    @Override
    public TodoResponseDto update(UUID id, TodoRequestDto dto) {
        log.debug("Mise à jour de la tâche: {}", id);

        if (todoService.existsByTitreAndIdNot(dto.getTitre(), id)) {
            throw new ResourceAlreadyExistsException(
                    "Une tâche avec le titre '" + dto.getTitre() + "' existe déjà",
                    "RESOURCE_ALREADY_EXISTS",
                    409);
        }

        // Validation de la cohérence des dates
        validateDates(dto.getDateDebut(), dto.getDatePrevueFin());

        Todo todo = todoMapper.toEntity(dto);
        Todo updated = todoService.update(id, todo);
        return todoMapper.toResponseDto(updated);
    }

    @Override
    public TodoResponseDto updateStatut(UUID id, TodoStatusDto dto) {
        log.debug("Changement de statut de la tâche {} → {}", id, dto.getStatut());
        Todo updated = todoService.updateStatut(id, dto.getStatut());
        return todoMapper.toResponseDto(updated);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Suppression de la tâche: {}", id);
        Todo todo = todoService.findById(id);
        if (todo.getStatut() != TodoStatus.TERMINEE) {
            throw new BadRequestException("Seules les tâches terminées peuvent être supprimées");
        }
        todoService.delete(id);
    }

    /**
     * Valide la cohérence des dates.
     * La date de début ne doit pas être postérieure à la date prévue de fin.
     */
    private void validateDates(LocalDate dateDebut, LocalDate datePrevueFin) {
        if (dateDebut != null && datePrevueFin != null && dateDebut.isAfter(datePrevueFin)) {
            throw new BadRequestException(
                    "La date de début (" + dateDebut + ") ne peut pas être postérieure à la date prévue de fin ("
                            + datePrevueFin + ")",
                    "INVALID_DATE_RANGE");
        }
    }
}

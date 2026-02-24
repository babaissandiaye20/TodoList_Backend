package com.TodoList.TodoList_Backend.service;

import com.TodoList.TodoList_Backend.entity.Todo;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import com.TodoList.TodoList_Backend.exception.ResourceNotFoundException;
import com.TodoList.TodoList_Backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public Todo save(Todo todo) {
        log.debug("Sauvegarde d'une nouvelle tâche: {}", todo.getTitre());
        return todoRepository.save(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public Todo findById(UUID id) {
        log.debug("Recherche de la tâche avec id: {}", id);
        return todoRepository.findTodoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> findAll() {
        log.debug("Récupération de toutes les tâches");
        return todoRepository.findAllTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> findAllByStatut(TodoStatus statut) {
        log.debug("Récupération des tâches par statut: {}", statut);
        return todoRepository.findAllByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> findAllWithFilters(TodoStatus statut, LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Filtrage des tâches - statut: {}, dateDebut: {}, dateFin: {}", statut, dateDebut, dateFin);
        return todoRepository.findAllWithFilters(statut, dateDebut, dateFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Todo> findAllEnRetard() {
        log.debug("Récupération des tâches en retard");
        return todoRepository.findAllEnRetard(LocalDate.now());
    }

    @Override
    @Transactional
    public Todo update(UUID id, Todo todo) {
        log.debug("Mise à jour de la tâche avec id: {}", id);
        Todo existing = findById(id);
        existing.setTitre(todo.getTitre());
        existing.setDescription(todo.getDescription());
        return todoRepository.save(existing);
    }

    @Override
    @Transactional
    public Todo updateStatut(UUID id, TodoStatus statut) {
        log.debug("Changement de statut de la tâche {} vers {}", id, statut);
        Todo existing = findById(id);
        existing.setStatut(statut);
        return todoRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Suppression de la tâche avec id: {}", id);
        Todo existing = findById(id);
        todoRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitre(String titre) {
        return todoRepository.existsByTitre(titre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitreAndIdNot(String titre, UUID id) {
        return todoRepository.existsByTitreAndIdNot(titre, id);
    }
}

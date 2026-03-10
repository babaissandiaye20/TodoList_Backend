package com.TodoList.TodoList_Backend.repository;

import com.TodoList.TodoList_Backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    @Query("SELECT t FROM Todo t")
    List<Todo> findAllTodos();

    @Query("SELECT t FROM Todo t WHERE t.id = :id")
    Optional<Todo> findTodoById(@Param("id") UUID id);

    @Query("SELECT COUNT(t) > 0 FROM Todo t WHERE t.titre = :titre")
    boolean existsByTitre(@Param("titre") String titre);

    @Query("SELECT COUNT(t) > 0 FROM Todo t WHERE t.titre = :titre AND t.id <> :id")
    boolean existsByTitreAndIdNot(@Param("titre") String titre, @Param("id") UUID id);
}

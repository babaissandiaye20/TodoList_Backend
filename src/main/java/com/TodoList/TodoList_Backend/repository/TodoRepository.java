package com.TodoList.TodoList_Backend.repository;

import com.TodoList.TodoList_Backend.entity.Todo;
import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    @Query("SELECT t FROM Todo t")
    List<Todo> findAllTodos();

    @Query("SELECT t FROM Todo t WHERE t.id = :id")
    Optional<Todo> findTodoById(@Param("id") UUID id);

    @Query("SELECT t FROM Todo t WHERE t.statut = :statut")
    List<Todo> findAllByStatut(@Param("statut") TodoStatus statut);

    /**
     * Filtre combiné : statut et/ou plage de dates de création.
     * Les paramètres sont optionnels grâce aux conditions IS NULL.
     */
    @Query("""
            SELECT t FROM Todo t
            WHERE (:statut IS NULL OR t.statut = :statut)
            AND (:dateDebut IS NULL OR t.dateCreation >= :dateDebut)
            AND (:dateFin IS NULL OR t.dateCreation <= :dateFin)
            """)
    List<Todo> findAllWithFilters(
            @Param("statut") TodoStatus statut,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );

    /**
     * Tâches EN_COURS dont la datePrevueFin est dépassée (en retard).
     */
    @Query("""
            SELECT t FROM Todo t
            WHERE t.statut = 'EN_COURS'
            AND t.datePrevueFin IS NOT NULL
            AND t.datePrevueFin < :today
            """)
    List<Todo> findAllEnRetard(@Param("today") LocalDate today);

    @Query("SELECT COUNT(t) > 0 FROM Todo t WHERE t.titre = :titre")
    boolean existsByTitre(@Param("titre") String titre);

    @Query("SELECT COUNT(t) > 0 FROM Todo t WHERE t.titre = :titre AND t.id <> :id")
    boolean existsByTitreAndIdNot(@Param("titre") String titre, @Param("id") UUID id);
}

package com.TodoList.TodoList_Backend.entity;

import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String titre;

    @Column(nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TodoStatus statut = TodoStatus.EN_COURS;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = true)
    private LocalDate dateDebut;

    @Column(nullable = true)
    private LocalDate datePrevueFin;
}

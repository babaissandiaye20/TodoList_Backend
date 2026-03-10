package com.TodoList.TodoList_Backend.dto;

import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {

    private UUID id;
    private String titre;
    private String description;
    private TodoStatus statut;
}

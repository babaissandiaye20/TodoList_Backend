package com.TodoList.TodoList_Backend.dto;

import com.TodoList.TodoList_Backend.entity.enums.TodoStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatusDto {

    @NotNull(message = "Le statut est obligatoire")
    private TodoStatus statut;
}

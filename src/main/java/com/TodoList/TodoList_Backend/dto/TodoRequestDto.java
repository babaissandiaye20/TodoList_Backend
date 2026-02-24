package com.TodoList.TodoList_Backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequestDto {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 100 caractères")
    private String titre;

    private String description;

    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur")
    private LocalDate dateDebut;

    @FutureOrPresent(message = "La date prévue de fin doit être aujourd'hui ou dans le futur")
    private LocalDate datePrevueFin;
}

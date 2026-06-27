package cl.duoc.ms_quest.dto;


import cl.duoc.ms_quest.model.QuestType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class QuestRequestDto {
    @NotBlank(message = "Debe ingresar un titulo")
    private String title;

    @NotBlank(message = "Debe ingresar una descripcion")
    private String description;

    @NotNull(message = "Debe ingresar un tipo de quest")
    private QuestType questType;

    @Min(value = 1, message = "El minimo de objetivos debe ser 1")
    private int objective;

    @Min(value = 0, message = "La experencia no debe ser negativa")
    private int expReward;

    @Min(value = 0, message = "Las recompensas no deben ser negativas")
    private int coinReward;
}
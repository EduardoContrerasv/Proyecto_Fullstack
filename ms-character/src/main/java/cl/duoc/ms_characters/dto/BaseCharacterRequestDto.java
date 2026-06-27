package cl.duoc.ms_characters.dto;

import cl.duoc.ms_characters.enums.CharacterArchetype;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BaseCharacterRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private CharacterArchetype characterArchetype;
    @Min(1)
    private int baseHealth;

    @Min(1)
    private int baseAttack;

    @Min(1)
    private int baseDefense;

    @NotNull
    private Long defaultCosmeticItemId;
}
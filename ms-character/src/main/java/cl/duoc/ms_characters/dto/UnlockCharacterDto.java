package cl.duoc.ms_characters.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnlockCharacterDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long baseCharacterId;
}

package cl.duoc.ms_inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsumeRequestDto {
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID del item es obligatorio")
    private Long itemId;

    @Min(value = 1, message = "La cantidad a consumir debe ser al menos 1")
    private int quantity;
}

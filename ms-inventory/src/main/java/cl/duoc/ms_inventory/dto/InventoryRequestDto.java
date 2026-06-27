package cl.duoc.ms_inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestDto {

    @NotNull(message = "Debe ingresar el ID del usuario")
    private Long userId;

    @NotNull(message = "Debe ingresar el ID del item")
    private Long itemId;

    @Min(value = 1, message = "Cantidad debe ser al menos 1")
    private int quantity;
}
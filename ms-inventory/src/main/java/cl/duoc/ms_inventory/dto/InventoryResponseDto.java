package cl.duoc.ms_inventory.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDto {

    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    @NotBlank
    private String username;
    @NotNull
    private Long itemId;
    @NotBlank
    private String itemName;
    @NotBlank
    private String itemType;
    @NotNull
    private int quantity;
}

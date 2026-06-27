package cl.duoc.ms_shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long shopItemId;
    @Min(1)
    private int quantity;
}

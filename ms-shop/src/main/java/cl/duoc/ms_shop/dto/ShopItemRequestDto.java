package cl.duoc.ms_shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import cl.duoc.ms_shop.enums.CurrencyType;
@Data
public class ShopItemRequestDto {
    @NotNull
    private Long itemId;
    @Min(1)
    private int price;
    @NotNull
    private CurrencyType currencyType;
}

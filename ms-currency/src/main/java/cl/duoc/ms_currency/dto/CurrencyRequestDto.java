package cl.duoc.ms_currency.dto;

import cl.duoc.ms_currency.enums.CurrencyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CurrencyRequestDto {
    @NotNull(message = "El tipo de moneda no puede ser nulo")
    private CurrencyType currencyType;
    @NotNull(message = "La cantidad no puede ser nula")
    @Min(1)
    private int amount;
}

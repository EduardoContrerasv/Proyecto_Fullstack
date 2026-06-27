package cl.duoc.ms_currency.dto;

import cl.duoc.ms_currency.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponseDto {
        private CurrencyType currencyType;
        private int amount;
}

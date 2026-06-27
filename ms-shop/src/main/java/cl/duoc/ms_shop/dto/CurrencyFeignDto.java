package cl.duoc.ms_shop.dto;

import cl.duoc.ms_shop.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyFeignDto {

    private CurrencyType currencyType;
    private int amount;
}

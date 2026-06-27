package cl.duoc.ms_currency.service;

import cl.duoc.ms_currency.dto.CurrencyRequestDto;
import cl.duoc.ms_currency.dto.CurrencyResponseDto;
import cl.duoc.ms_currency.enums.CurrencyType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CurrencyService {
    void addCurrency(Long userId, CurrencyRequestDto dto);
    void deductCurrency(Long userId, CurrencyRequestDto dto);
    List<CurrencyResponseDto> getAllBalances(Long userId);
    int getSpecificBalance(Long userId, CurrencyType currencyType);
}

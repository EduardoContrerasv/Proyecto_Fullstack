package cl.duoc.ms_currency.controller;

import cl.duoc.ms_currency.dto.CurrencyRequestDto;
import cl.duoc.ms_currency.dto.CurrencyResponseDto;
import cl.duoc.ms_currency.enums.CurrencyType;
import cl.duoc.ms_currency.service.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService service;

    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addCurrency(@PathVariable Long userId, @Valid @RequestBody CurrencyRequestDto dto) {
        service.addCurrency(userId, dto);
        return ResponseEntity.ok("Se han añadido " + dto.getAmount() + " " + dto.getCurrencyType() + " al usuario " + userId);
    }

    @PostMapping("/deduct/{userId}")
    public ResponseEntity<String> deductCurrency(@PathVariable Long userId, @Valid @RequestBody CurrencyRequestDto dto) {
        service.deductCurrency(userId, dto);
        return ResponseEntity.ok("Se han descontado " + dto.getAmount() + " " + dto.getCurrencyType() + " del usuario " + userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CurrencyResponseDto>> getAllBalances(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getAllBalances(userId));
    }

    @GetMapping("/{userId}/{currencyType}")
    public ResponseEntity<Integer> getSpecificBalance(@PathVariable Long userId, @PathVariable CurrencyType currencyType) {
        return ResponseEntity.ok(service.getSpecificBalance(userId, currencyType));
    }
}
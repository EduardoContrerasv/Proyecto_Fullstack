package cl.duoc.ms_currency.service.impl;

import cl.duoc.ms_currency.dto.CurrencyRequestDto;
import cl.duoc.ms_currency.enums.CurrencyType;
import cl.duoc.ms_currency.model.UserCurrency;
import cl.duoc.ms_currency.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRepository repository;

    @InjectMocks
    private CurrencyServiceImpl service;

    private CurrencyRequestDto req(CurrencyType type, int amount) {
        CurrencyRequestDto dto = new CurrencyRequestDto();
        dto.setCurrencyType(type);
        dto.setAmount(amount);
        return dto;
    }

    @Test
    void addCurrency_walletNoExiste_creaYAcredita() {
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.GOLD)).thenReturn(Optional.empty());

        service.addCurrency(1L, req(CurrencyType.GOLD, 100));

        ArgumentCaptor<UserCurrency> captor = ArgumentCaptor.forClass(UserCurrency.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(100);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getCurrencyType()).isEqualTo(CurrencyType.GOLD);
    }

    @Test
    void addCurrency_walletExiste_acumulaSaldo() {
        UserCurrency wallet = new UserCurrency();
        wallet.setUserId(1L);
        wallet.setCurrencyType(CurrencyType.GOLD);
        wallet.setAmount(100);
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.GOLD)).thenReturn(Optional.of(wallet));

        service.addCurrency(1L, req(CurrencyType.GOLD, 50));

        assertThat(wallet.getAmount()).isEqualTo(150);
        verify(repository).save(wallet);
    }

    @Test
    void deductCurrency_fondosSuficientes_descuenta() {
        UserCurrency wallet = new UserCurrency();
        wallet.setUserId(1L);
        wallet.setCurrencyType(CurrencyType.GOLD);
        wallet.setAmount(100);
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.GOLD)).thenReturn(Optional.of(wallet));

        service.deductCurrency(1L, req(CurrencyType.GOLD, 30));

        assertThat(wallet.getAmount()).isEqualTo(70);
        verify(repository).save(wallet);
    }

    @Test
    void deductCurrency_fondosInsuficientes_lanzaExcepcionYNoGuarda() {
        UserCurrency wallet = new UserCurrency();
        wallet.setAmount(20);
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.GOLD)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> service.deductCurrency(1L, req(CurrencyType.GOLD, 100)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("insuficientes");
        verify(repository, never()).save(any());
    }

    @Test
    void deductCurrency_walletNoExiste_lanza404() {
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.AMETHYST)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deductCurrency(1L, req(CurrencyType.AMETHYST, 10)))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getSpecificBalance_devuelveMontoOceroSiNoExiste() {
        UserCurrency wallet = new UserCurrency();
        wallet.setAmount(70);
        when(repository.findByUserIdAndCurrencyType(1L, CurrencyType.GOLD)).thenReturn(Optional.of(wallet));
        assertThat(service.getSpecificBalance(1L, CurrencyType.GOLD)).isEqualTo(70);

        when(repository.findByUserIdAndCurrencyType(2L, CurrencyType.GOLD)).thenReturn(Optional.empty());
        assertThat(service.getSpecificBalance(2L, CurrencyType.GOLD)).isEqualTo(0);
    }
}

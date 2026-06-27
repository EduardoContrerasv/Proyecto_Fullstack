package cl.duoc.ms_shop.service.impl;

import cl.duoc.ms_shop.client.CurrencyClient;
import cl.duoc.ms_shop.client.InventoryClient;
import cl.duoc.ms_shop.client.ItemClient;
import cl.duoc.ms_shop.dto.CurrencyFeignDto;
import cl.duoc.ms_shop.dto.ItemFeignDto;
import cl.duoc.ms_shop.dto.PurchaseRequestDto;
import cl.duoc.ms_shop.dto.ShopCatalogResponseDto;
import cl.duoc.ms_shop.dto.ShopItemRequestDto;
import cl.duoc.ms_shop.enums.CurrencyType;
import cl.duoc.ms_shop.model.ShopItem;
import cl.duoc.ms_shop.repository.ShopItemRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock private ShopItemRepository repository;
    @Mock private CurrencyClient currencyClient;
    @Mock private InventoryClient inventoryClient;
    @Mock private ItemClient itemClient;
    @InjectMocks private ShopServiceImpl service;

    private ShopItem listing() {
        ShopItem s = new ShopItem();
        s.setItemId(10L);
        s.setPrice(50);
        s.setCurrencyType(CurrencyType.GOLD);
        return s;
    }

    private PurchaseRequestDto purchase(int qty) {
        PurchaseRequestDto d = new PurchaseRequestDto();
        d.setUserId(1L);
        d.setShopItemId(5L);
        d.setQuantity(qty);
        return d;
    }

    private ItemFeignDto item(String name) {
        ItemFeignDto i = new ItemFeignDto();
        i.setName(name);
        return i;
    }

    // ─── createShopListing ────────────────────────────────────────────

    @Test
    void createShopListing_guardaEnRepositorioYRetornaMensaje() {
        ShopItemRequestDto dto = new ShopItemRequestDto();
        dto.setItemId(5L);
        dto.setPrice(100);
        dto.setCurrencyType(CurrencyType.GOLD);
        when(repository.save(any(ShopItem.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = service.createShopListing(dto);

        verify(repository).save(any(ShopItem.class));
        assertThat(result).contains("tienda");
    }

    @Test
    void createShopListing_asignaAtributosCorrectamente() {
        ShopItemRequestDto dto = new ShopItemRequestDto();
        dto.setItemId(7L);
        dto.setPrice(200);
        dto.setCurrencyType(CurrencyType.FRACTAL);
        when(repository.save(any(ShopItem.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createShopListing(dto);

        ArgumentCaptor<ShopItem> captor = ArgumentCaptor.forClass(ShopItem.class);
        verify(repository).save(captor.capture());
        ShopItem saved = captor.getValue();
        assertThat(saved.getItemId()).isEqualTo(7L);
        assertThat(saved.getPrice()).isEqualTo(200);
        assertThat(saved.getCurrencyType()).isEqualTo(CurrencyType.FRACTAL);
    }

    // ─── purchaseItem ─────────────────────────────────────────────────

    @Test
    void compraExitosa_descuentaMonedaYEntregaItem() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Espada"));
        when(currencyClient.deductCurrency(eq(1L), any())).thenReturn("descontado");

        String res = service.purchaseItem(purchase(2));

        verify(currencyClient).deductCurrency(eq(1L), any());
        verify(inventoryClient).addItem(any());
        verify(currencyClient, never()).addCurrency(anyLong(), any());
        assertThat(res).contains("Espada");
    }

    @Test
    void compraExitosa_calcularCostoTotal() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Escudo"));
        when(currencyClient.deductCurrency(eq(1L), any())).thenReturn("ok");

        service.purchaseItem(purchase(3));

        ArgumentCaptor<CurrencyFeignDto> captor = ArgumentCaptor.forClass(CurrencyFeignDto.class);
        verify(currencyClient).deductCurrency(eq(1L), captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualTo(150); // 50 * 3
    }

    @Test
    void compra_cantidadInvalida_lanzaIllegalArgument() {
        assertThatThrownBy(() -> service.purchaseItem(purchase(0)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(currencyClient, inventoryClient, repository, itemClient);
    }

    @Test
    void compra_cantidadNegativa_lanzaIllegalArgument() {
        assertThatThrownBy(() -> service.purchaseItem(purchase(-5)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(currencyClient, inventoryClient, repository, itemClient);
    }

    @Test
    void compra_listingNoExiste_lanza404() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(currencyClient, inventoryClient, itemClient);
    }

    @Test
    void compra_itemNoExisteEnMsItem_lanza404() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        doThrow(mock(FeignException.NotFound.class)).when(itemClient).getItemById(10L);

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(currencyClient, inventoryClient);
    }

    @Test
    void compra_fondosInsuficientes_lanza400() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Espada"));
        doThrow(mock(FeignException.BadRequest.class)).when(currencyClient).deductCurrency(eq(1L), any());

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Fondos");
        verifyNoInteractions(inventoryClient);
    }

    @Test
    void compra_fallaEntrega_reembolsaLaMoneda() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Espada"));
        when(currencyClient.deductCurrency(eq(1L), any())).thenReturn("descontado");
        doThrow(mock(FeignException.class)).when(inventoryClient).addItem(any());

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(RuntimeException.class);

        verify(currencyClient).addCurrency(eq(1L), any());
    }

    @Test
    void compra_fallaEntregaYReembolso_lanzaErrorGrave() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Espada"));
        when(currencyClient.deductCurrency(eq(1L), any())).thenReturn("descontado");
        doThrow(mock(FeignException.class)).when(inventoryClient).addItem(any());
        doThrow(mock(FeignException.class)).when(currencyClient).addCurrency(eq(1L), any());

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("soporte");
    }

    @Test
    void compra_billeteraNoExiste_lanza400() {
        when(repository.findById(5L)).thenReturn(Optional.of(listing()));
        when(itemClient.getItemById(10L)).thenReturn(item("Espada"));
        doThrow(mock(FeignException.NotFound.class)).when(currencyClient).deductCurrency(eq(1L), any());

        assertThatThrownBy(() -> service.purchaseItem(purchase(1)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("billetera");
    }

    // ─── getCatalog ───────────────────────────────────────────────────

    @Test
    void getCatalog_retornaListaMapeada() {
        ShopItem s1 = new ShopItem(1L, 10L, 50, CurrencyType.GOLD);
        ShopItem s2 = new ShopItem(2L, 20L, 100, CurrencyType.FRACTAL);
        when(repository.findAll()).thenReturn(List.of(s1, s2));

        List<ShopCatalogResponseDto> result = service.getCatalog();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getShopItemId()).isEqualTo(1L);
        assertThat(result.get(0).getPrice()).isEqualTo(50);
        assertThat(result.get(0).getCurrencyType()).isEqualTo("GOLD");
        assertThat(result.get(1).getShopItemId()).isEqualTo(2L);
        assertThat(result.get(1).getCurrencyType()).isEqualTo("FRACTAL");
    }

    @Test
    void getCatalog_listaVacia_retornaListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<ShopCatalogResponseDto> result = service.getCatalog();

        assertThat(result).isEmpty();
    }
}

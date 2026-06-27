package cl.duoc.ms_inventory.service.impl;

import cl.duoc.ms_inventory.client.ItemFeignClient;
import cl.duoc.ms_inventory.client.UserFeignClient;
import cl.duoc.ms_inventory.dto.ConsumeRequestDto;
import cl.duoc.ms_inventory.dto.InventoryRequestDto;
import cl.duoc.ms_inventory.dto.ItemDto;
import cl.duoc.ms_inventory.dto.UserDto;
import cl.duoc.ms_inventory.model.Inventory;
import cl.duoc.ms_inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock private InventoryRepository repository;
    @Mock private ItemFeignClient itemClient;
    @Mock private UserFeignClient userClient;
    @InjectMocks private InventoryServiceImpl service;

    private UserDto user() {
        UserDto u = new UserDto();
        u.setId(1L);
        u.setUsername("ana");
        return u;
    }

    private ItemDto item(String type, String name) {
        ItemDto i = new ItemDto();
        i.setId(10L);
        i.setName(name);
        i.setItemType(type);
        return i;
    }

    @Test
    void addItem_cosmeticoYaPoseido_rechaza() {
        when(userClient.getUserById(1L)).thenReturn(user());
        when(itemClient.getItemById(10L)).thenReturn(item("COSMETIC", "Skin"));
        when(repository.findByUserIdAndItemId(1L, 10L)).thenReturn(Optional.of(new Inventory()));

        assertThatThrownBy(() -> service.addItem(new InventoryRequestDto(1L, 10L, 1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cosmético");
    }

    @Test
    void addItem_consumibleSobreLimite_rechaza() {
        when(userClient.getUserById(1L)).thenReturn(user());
        when(itemClient.getItemById(10L)).thenReturn(item("CONSUMABLE", "Pocion"));
        when(repository.findByUserIdAndItemId(1L, 10L)).thenReturn(Optional.empty());
        when(repository.countByUserIdAndItemType(1L, "CONSUMABLE")).thenReturn(19);

        assertThatThrownBy(() -> service.addItem(new InventoryRequestDto(1L, 10L, 5)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("lleno");
    }

    @Test
    void addItem_equipoApilable_acumulaCantidad() {
        when(userClient.getUserById(1L)).thenReturn(user());
        when(itemClient.getItemById(10L)).thenReturn(item("WEAPON", "Espada"));
        Inventory existing = new Inventory();
        existing.setUserId(1L);
        existing.setItemId(10L);
        existing.setItemType("WEAPON");
        existing.setQuantity(5);
        when(repository.findByUserIdAndItemId(1L, 10L)).thenReturn(Optional.of(existing));
        when(repository.countByUserIdAndItemTypeIn(eq(1L), anyList())).thenReturn(5);
        when(repository.save(existing)).thenReturn(existing);

        service.addItem(new InventoryRequestDto(1L, 10L, 3));

        assertThat(existing.getQuantity()).isEqualTo(8);
        verify(repository).save(existing);
    }

    @Test
    void addItem_cantidadInvalida_lanzaIllegalArgument() {
        assertThatThrownBy(() -> service.addItem(new InventoryRequestDto(1L, 10L, 0)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(userClient, itemClient, repository);
    }

    @Test
    void consumeItem_stockInsuficiente_rechaza() {
        when(itemClient.getItemById(10L)).thenReturn(item("CONSUMABLE", "Pocion"));
        Inventory existing = new Inventory();
        existing.setQuantity(2);
        when(repository.findByUserIdAndItemId(1L, 10L)).thenReturn(Optional.of(existing));

        ConsumeRequestDto dto = new ConsumeRequestDto();
        dto.setUserId(1L);
        dto.setItemId(10L);
        dto.setQuantity(5);

        assertThatThrownBy(() -> service.consumeItem(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("insuficientes");
    }

    @Test
    void consumeItem_dejaCantidadCero_eliminaLaFila() {
        when(itemClient.getItemById(10L)).thenReturn(item("CONSUMABLE", "Pocion"));
        Inventory existing = new Inventory();
        existing.setQuantity(3);
        when(repository.findByUserIdAndItemId(1L, 10L)).thenReturn(Optional.of(existing));

        ConsumeRequestDto dto = new ConsumeRequestDto();
        dto.setUserId(1L);
        dto.setItemId(10L);
        dto.setQuantity(3);

        service.consumeItem(dto);

        verify(repository).delete(existing);
        verify(repository, never()).save(any());
    }
}

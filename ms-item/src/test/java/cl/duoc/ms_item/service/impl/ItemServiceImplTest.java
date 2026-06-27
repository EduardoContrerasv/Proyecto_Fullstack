package cl.duoc.ms_item.service.impl;

import cl.duoc.ms_item.dto.ItemRequestDto;
import cl.duoc.ms_item.dto.ItemResponseDto;
import cl.duoc.ms_item.enums.ItemType;
import cl.duoc.ms_item.enums.Rarity;
import cl.duoc.ms_item.model.Item;
import cl.duoc.ms_item.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock private ItemRepository repository;
    @InjectMocks private ItemServiceImpl service;

    private ItemRequestDto req(String name, int price) {
        return new ItemRequestDto(name, "descripcion", price, 1, ItemType.WEAPON, Rarity.COMMON);
    }

    @Test
    void createItem_valido_persisteYMapea() {
        when(repository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        ItemResponseDto res = service.createItem(req("Espada", 100));

        assertThat(res.getName()).isEqualTo("Espada");
        verify(repository).save(any(Item.class));
    }

    @Test
    void createItem_precioNegativo_rechaza() {
        assertThatThrownBy(() -> service.createItem(req("Espada", -5)))
                .isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updatePrice_precioNegativo_rechaza() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Item()));

        assertThatThrownBy(() -> service.updatePrice(1L, -10))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void findById_inexistente_lanza404() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L))
                .isInstanceOf(ResponseStatusException.class);
    }
}

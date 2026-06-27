package cl.duoc.ms_characters.service.impl;

import cl.duoc.ms_characters.client.InventoryClient;
import cl.duoc.ms_characters.client.ItemClient;
import cl.duoc.ms_characters.client.UserFeignClient;
import cl.duoc.ms_characters.dto.BaseCharacterRequestDto;
import cl.duoc.ms_characters.dto.EquipItemDto;
import cl.duoc.ms_characters.dto.ItemFeignDto;
import cl.duoc.ms_characters.dto.UnlockCharacterDto;
import cl.duoc.ms_characters.dto.UserFeignDto;
import cl.duoc.ms_characters.enums.CharacterArchetype;
import cl.duoc.ms_characters.enums.EquipmentSlot;
import cl.duoc.ms_characters.model.BaseCharacter;
import cl.duoc.ms_characters.model.UserCharacter;
import cl.duoc.ms_characters.repository.BaseCharacterRepository;
import cl.duoc.ms_characters.repository.UserCharacterRepository;
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
class CharacterServiceImplTest {

    @Mock private BaseCharacterRepository baseCharacterRepository;
    @Mock private UserCharacterRepository userCharacterRepository;
    @Mock private UserFeignClient userFeignClient;
    @Mock private InventoryClient inventoryClient;
    @Mock private ItemClient itemClient;
    @InjectMocks private CharacterServiceImpl service;

    private BaseCharacterRequestDto baseReq(CharacterArchetype arq) {
        BaseCharacterRequestDto dto = new BaseCharacterRequestDto();
        dto.setName("Hero");
        dto.setCharacterArchetype(arq);
        dto.setBaseHealth(100);
        dto.setBaseAttack(100);
        dto.setBaseDefense(100);
        dto.setDefaultCosmeticItemId(1L);
        return dto;
    }

    @Test
    void createBaseCharacter_arquetipoAttack_aplicaMultiplicadores() {
        when(baseCharacterRepository.findByName("Hero")).thenReturn(Optional.empty());
        when(baseCharacterRepository.save(any(BaseCharacter.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createBaseCharacter(baseReq(CharacterArchetype.ATTACK));

        ArgumentCaptor<BaseCharacter> captor = ArgumentCaptor.forClass(BaseCharacter.class);
        verify(baseCharacterRepository).save(captor.capture());
        BaseCharacter saved = captor.getValue();
        assertThat(saved.getBaseAttack()).isEqualTo(120);   // 100 * 1.2
        assertThat(saved.getBaseHealth()).isEqualTo(90);    // 100 * 0.9
        assertThat(saved.getBaseDefense()).isEqualTo(70);   // 100 * 0.7
    }

    @Test
    void createBaseCharacter_nombreDuplicado_lanza409() {
        when(baseCharacterRepository.findByName("Hero")).thenReturn(Optional.of(new BaseCharacter()));

        assertThatThrownBy(() -> service.createBaseCharacter(baseReq(CharacterArchetype.SUPPORT)))
                .isInstanceOf(ResponseStatusException.class);
        verify(baseCharacterRepository, never()).save(any());
    }

    @Test
    void unlock_heroeYaPoseido_lanza409() {
        UnlockCharacterDto dto = new UnlockCharacterDto();
        dto.setUserId(1L);
        dto.setBaseCharacterId(2L);
        when(userFeignClient.getUserById(1L)).thenReturn(mock(UserFeignDto.class));
        when(baseCharacterRepository.findById(2L)).thenReturn(Optional.of(new BaseCharacter()));
        when(userCharacterRepository.existsByUserIdAndBaseCharacterId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.unlockCharacterForUser(dto))
                .isInstanceOf(ResponseStatusException.class);
        verify(userCharacterRepository, never()).save(any());
    }

    @Test
    void unlock_heroeInexistente_lanza404() {
        UnlockCharacterDto dto = new UnlockCharacterDto();
        dto.setUserId(1L);
        dto.setBaseCharacterId(99L);
        when(userFeignClient.getUserById(1L)).thenReturn(mock(UserFeignDto.class));
        when(baseCharacterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.unlockCharacterForUser(dto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void equipItem_slotIncorrecto_rechaza() {
        EquipItemDto dto = new EquipItemDto();
        dto.setUserId(1L);
        dto.setUserCharacterId(2L);
        dto.setItemId(5L);
        dto.setSlot(EquipmentSlot.WEAPON);

        when(userCharacterRepository.findByUserIdAndBaseCharacterId(1L, 2L))
                .thenReturn(Optional.of(new UserCharacter()));
        when(inventoryClient.checkHasItem(1L, 5L)).thenReturn(true);
        ItemFeignDto item = new ItemFeignDto();
        item.setItemType("ARMOR");
        when(itemClient.getItemById(5L)).thenReturn(item);

        assertThatThrownBy(() -> service.equipItem(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("arma");
    }
}

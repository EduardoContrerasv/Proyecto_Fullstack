package cl.duoc.ms_inventory.service;
import cl.duoc.ms_inventory.dto.ConsumeRequestDto;
import cl.duoc.ms_inventory.dto.InventoryRequestDto;
import cl.duoc.ms_inventory.dto.InventoryResponseDto;
import cl.duoc.ms_inventory.dto.SimpleInventoryResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface InventoryService {

    List<InventoryResponseDto> getInventoryByUserId(Long userId);
    InventoryResponseDto addItem(InventoryRequestDto dto);
    String consumeItem(ConsumeRequestDto dto);
    SimpleInventoryResponseDto getSpecificItem(Long userId, Long itemId);
    boolean checkHasItem(Long userId, Long itemId);
}
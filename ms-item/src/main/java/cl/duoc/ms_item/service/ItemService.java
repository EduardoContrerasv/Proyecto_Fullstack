package cl.duoc.ms_item.service;

import cl.duoc.ms_item.dto.ItemRequestDto;
import cl.duoc.ms_item.dto.ItemResponseDto;
import cl.duoc.ms_item.enums.Rarity;

import java.util.List;

public interface ItemService {

    ItemResponseDto findByName(String name);
    List<ItemResponseDto> findByLevel(int level);
    ItemResponseDto updateDescription(Long id, String description);
    ItemResponseDto updatePrice(Long id, int price);
    ItemResponseDto updateRarity(Long id, Rarity rarity);
    List<ItemResponseDto> findAll();
    ItemResponseDto findById(Long id);
    List<ItemResponseDto> findByRarity(String rarity);
    List<ItemResponseDto> findByType(String itemType);
    ItemResponseDto createItem(ItemRequestDto dto);
    ItemResponseDto updateItem(Long id, ItemRequestDto dto);
    void deleteItem(Long id);
}

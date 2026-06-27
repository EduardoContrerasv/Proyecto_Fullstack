package cl.duoc.ms_item.dto;

import cl.duoc.ms_item.enums.ItemType;
import cl.duoc.ms_item.enums.Rarity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private int price;
    private int requiredLevel;
    private ItemType itemType;
    private Rarity rarity;
}
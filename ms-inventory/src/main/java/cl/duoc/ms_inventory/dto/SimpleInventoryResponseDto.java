package cl.duoc.ms_inventory.dto;

import lombok.Data;

@Data
public class SimpleInventoryResponseDto {
    private Long id;
    private Long userId;
    private Long itemId;
    private String itemType;
    private int quantity;
}

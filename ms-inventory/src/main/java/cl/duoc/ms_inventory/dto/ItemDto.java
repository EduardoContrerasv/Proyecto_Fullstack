package cl.duoc.ms_inventory.dto;

import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String itemType;
}

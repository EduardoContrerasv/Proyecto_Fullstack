package cl.duoc.ms_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryFeignDto {

    private Long userId;
    private Long itemId;
    private int quantity;
}

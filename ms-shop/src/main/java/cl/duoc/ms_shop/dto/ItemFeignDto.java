package cl.duoc.ms_shop.dto;

import lombok.Data;

@Data
public class ItemFeignDto {
    private Long id;
    private String name;
    private int price;
    private String itemType;
}
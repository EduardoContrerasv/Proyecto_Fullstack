package cl.duoc.ms_shop.dto;

import lombok.Data;

@Data
public class ShopCatalogResponseDto {
    private Long shopItemId;   // The ID of the listing (to send to your purchase method)
    private Long itemId;       // The actual item they will get
    private int price;
    private String currencyType;
}
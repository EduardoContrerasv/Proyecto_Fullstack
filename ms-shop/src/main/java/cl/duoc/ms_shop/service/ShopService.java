package cl.duoc.ms_shop.service;

import cl.duoc.ms_shop.dto.PurchaseRequestDto;
import cl.duoc.ms_shop.dto.ShopCatalogResponseDto;
import cl.duoc.ms_shop.dto.ShopItemRequestDto;

import java.util.List;

public interface ShopService {
    String createShopListing(ShopItemRequestDto dto);
    String purchaseItem(PurchaseRequestDto dto);
    List<ShopCatalogResponseDto> getCatalog();
}

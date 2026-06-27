package cl.duoc.ms_shop.controller;

import cl.duoc.ms_shop.dto.PurchaseRequestDto;
import cl.duoc.ms_shop.dto.ShopCatalogResponseDto;
import cl.duoc.ms_shop.dto.ShopItemRequestDto;
import cl.duoc.ms_shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService service;

    @PostMapping("/admin/create")
    public ResponseEntity<String> createShopListing(@Valid @RequestBody ShopItemRequestDto dto) {
        return ResponseEntity.ok(service.createShopListing(dto));
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseItem(@Valid @RequestBody PurchaseRequestDto dto) {
        return ResponseEntity.ok(service.purchaseItem(dto));
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<ShopCatalogResponseDto>> getShopCatalog() {
        return ResponseEntity.ok(service.getCatalog());
    }
}
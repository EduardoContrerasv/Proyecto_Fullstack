package cl.duoc.ms_shop.service.impl;

import cl.duoc.ms_shop.client.CurrencyClient;
import cl.duoc.ms_shop.client.InventoryClient;
import cl.duoc.ms_shop.client.ItemClient;
import cl.duoc.ms_shop.dto.*;
import cl.duoc.ms_shop.model.ShopItem;
import cl.duoc.ms_shop.repository.ShopItemRepository;
import cl.duoc.ms_shop.service.ShopService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopItemRepository repository;
    private final CurrencyClient currencyClient;
    private final InventoryClient inventoryClient;
    private final ItemClient itemClient;

    @Override
    public String createShopListing(ShopItemRequestDto dto) {
        log.info("createShopListing");
        ShopItem listing = new ShopItem();
        listing.setItemId(dto.getItemId());
        listing.setPrice(dto.getPrice());
        listing.setCurrencyType(dto.getCurrencyType());

        repository.save(listing);
        return "Item añadido a tienda";
    }

    @Override
    public String purchaseItem(PurchaseRequestDto dto) {
        log.info("purchaseItem");

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        ShopItem listing = repository.findById(dto.getShopItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El item no existe en la tienda."));

        String itemName;
        try {
            ItemFeignDto item = itemClient.getItemById(listing.getItemId());
            itemName = item.getName();
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El item de esta tienda no existe en ms-item.");
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-item: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo verificar el item con ms-item.");
        }

        int totalCost = listing.getPrice() * dto.getQuantity();

        String currencyMessage;
        try {
            currencyMessage = currencyClient.deductCurrency(
                    dto.getUserId(),
                    new CurrencyFeignDto(listing.getCurrencyType(), totalCost));
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El usuario no tiene billetera de " + listing.getCurrencyType() + ".");
        } catch (FeignException.BadRequest e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fondos insuficientes para esta compra.");
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-currency al descontar al usuario {}: {}", dto.getUserId(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo procesar el pago con ms-currency.");
        }

        try {
            inventoryClient.addItem(new InventoryFeignDto(
                    dto.getUserId(),
                    listing.getItemId(),
                    dto.getQuantity()));
        } catch (FeignException e) {
            log.error("Fallo la entrega al inventario del usuario {}. Reembolsando {}: {}",
                    dto.getUserId(), totalCost, e.getMessage());
            try {
                currencyClient.addCurrency(
                        dto.getUserId(),
                        new CurrencyFeignDto(listing.getCurrencyType(), totalCost));
            } catch (FeignException refundError) {
                log.error("CRÍTICO: cobro realizado pero no se pudo entregar NI reembolsar al usuario {}: {}",
                        dto.getUserId(), refundError.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Error grave: se realizó el cobro pero no se pudo entregar ni reembolsar. Contacta a soporte.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo entregar el item al inventario. Se ha reembolsado tu compra.");
        }

        return String.format("Has obtenido %dx %s. %s",
                dto.getQuantity(),
                itemName,
                currencyMessage);
    }

    @Override
    public List<ShopCatalogResponseDto> getCatalog() {
        log.info("getCatalog");
        List<ShopItem> allListings = repository.findAll();

        return allListings.stream().map(listing -> {
            ShopCatalogResponseDto dto = new ShopCatalogResponseDto();
            dto.setShopItemId(listing.getId());
            dto.setItemId(listing.getItemId());
            dto.setPrice(listing.getPrice());
            dto.setCurrencyType(listing.getCurrencyType().name());
            return dto;
        }).toList();
    }
}

package cl.duoc.ms_inventory.service.impl;

import cl.duoc.ms_inventory.client.ItemFeignClient;
import cl.duoc.ms_inventory.client.UserFeignClient;
import cl.duoc.ms_inventory.dto.*;
import cl.duoc.ms_inventory.model.Inventory;
import cl.duoc.ms_inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import cl.duoc.ms_inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;
    private final ItemFeignClient itemClient;
    private final UserFeignClient userClient;

    @Override
    public List<InventoryResponseDto> getInventoryByUserId(Long userId) {
        log.debug("getInventoryByUserId()");
        UserDto user;
        try {
            user = userClient.getUserById(userId);
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + userId);
        }

        List<Inventory> rawInventory = repository.findByUserId(userId);

        List<InventoryResponseDto> responseList = new ArrayList<>();

        for (Inventory entity : rawInventory) {
            InventoryResponseDto dto = new InventoryResponseDto();

            dto.setId(entity.getId());
            dto.setUserId(entity.getUserId());
            dto.setItemId(entity.getItemId());
            dto.setQuantity(entity.getQuantity());
            dto.setUsername(user.getUsername());

            try {
                ItemDto item = itemClient.getItemById(entity.getItemId());
                dto.setItemName(item.getName());
                dto.setItemType(item.getItemType());
            } catch (feign.FeignException e) {
                dto.setItemName("Item Desconocido");
                dto.setItemType("UNKNOWN");
            }

            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public InventoryResponseDto addItem(InventoryRequestDto dto) {
        log.debug("addItem()");

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
        }

        UserDto user;
        ItemDto verifiedItem;
        try {
            user = userClient.getUserById(dto.getUserId());
            verifiedItem = itemClient.getItemById(dto.getItemId());
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario o Item no encontrado.");
        }

        String realItemType = verifiedItem.getItemType();
        Inventory existingItem = repository.findByUserIdAndItemId(dto.getUserId(), dto.getItemId()).orElse(null);

        if ("COSMETIC".equals(realItemType)) {
            if (dto.getQuantity() > 1) {
                throw new RuntimeException("Solo se puede tener 1 de este item");
            }
            if (existingItem != null) {
                throw new RuntimeException("Ya posees este cosmético");
            }
        }
        else if ("CONSUMABLE".equals(realItemType)) {
            int currentAmount = repository.countByUserIdAndItemType(dto.getUserId(), realItemType);
            if (currentAmount + dto.getQuantity() > 20) {
                throw new RuntimeException("Inventario de consumibles lleno.");
            }
        }
        else {
            List<String> gearTypes = List.of("WEAPON", "ARMOR");

            if (gearTypes.contains(realItemType)) {
                int currentGear = repository.countByUserIdAndItemTypeIn(dto.getUserId(), gearTypes);
                if (currentGear + dto.getQuantity() > 500) {
                    throw new RuntimeException("Inventario de equipamiento lleno.");
                }
            }
        }

        Inventory savedItem;
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            savedItem = repository.save(existingItem);
        } else {
            Inventory newItem = new Inventory();
            newItem.setUserId(dto.getUserId());
            newItem.setItemId(dto.getItemId());
            newItem.setItemType(realItemType);
            newItem.setQuantity(dto.getQuantity());
            savedItem = repository.save(newItem);
        }
    
        InventoryResponseDto response = new InventoryResponseDto();
        response.setId(savedItem.getId());
        response.setUserId(savedItem.getUserId());
        response.setItemId(savedItem.getItemId());
        response.setQuantity(savedItem.getQuantity());
        response.setUsername(user.getUsername());
        response.setItemName(verifiedItem.getName());
        response.setItemType(realItemType);

        return response;
    }


    @Override
    public String consumeItem(ConsumeRequestDto dto) {
        log.debug("consumeItem()");

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Debe ser mayor a 0");
        }

        ItemDto verifiedItem;
        try {
            verifiedItem = itemClient.getItemById(dto.getItemId());
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en la base de datos: " + dto.getItemId());
        }
        String itemName = verifiedItem.getName();

        Inventory existingItem = repository.findByUserIdAndItemId(dto.getUserId(), dto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en el inventario"));

        if (existingItem.getQuantity() < dto.getQuantity()) {
            throw new RuntimeException(
                    "Items insuficientes! Total: " + existingItem.getQuantity() +
                            " intento usar/eliminar: " + dto.getQuantity()
            );
        }

        int remainingAmount = existingItem.getQuantity() - dto.getQuantity();

        if (remainingAmount == 0) {
            repository.delete(existingItem);
        } else {
            existingItem.setQuantity(remainingAmount);
            repository.save(existingItem);
        }

        return "- " + dto.getQuantity() + " " + itemName;
    }

    @Override
    public SimpleInventoryResponseDto getSpecificItem(Long userId, Long itemId) {
        log.debug("getSpecificItem()");

        if (userId == null) {
            throw new RuntimeException("Usuario no encontrado");
        } else if (itemId == null) {
            throw new RuntimeException("Item ID es requerido");
        }

        Inventory existingItem = repository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en el inventario"));

        ItemDto item;
        try {
            item = itemClient.getItemById(itemId);
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en la base de datos de items");
        }

        SimpleInventoryResponseDto response = new SimpleInventoryResponseDto();

        response.setId(existingItem.getId());
        response.setUserId(existingItem.getUserId());
        response.setItemId(existingItem.getItemId());
        response.setQuantity(existingItem.getQuantity());

        response.setItemType(item.getItemType());

        return response;
    }

    @Override
    public boolean checkHasItem(Long userId, Long itemId) {
        log.debug("checkHasItem()");
        return repository.findByUserIdAndItemId(userId, itemId).isPresent();
    }

}
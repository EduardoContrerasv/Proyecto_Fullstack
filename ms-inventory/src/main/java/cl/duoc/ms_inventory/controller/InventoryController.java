package cl.duoc.ms_inventory.controller;

import cl.duoc.ms_inventory.dto.ConsumeRequestDto;
import cl.duoc.ms_inventory.dto.InventoryRequestDto;
import cl.duoc.ms_inventory.dto.InventoryResponseDto;
import cl.duoc.ms_inventory.dto.SimpleInventoryResponseDto;
import cl.duoc.ms_inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping("/add")
    public ResponseEntity<InventoryResponseDto> addItem(@Valid @RequestBody InventoryRequestDto dto) {
        return ResponseEntity.ok(service.addItem(dto));
    }

    @PostMapping("/consume")
    public ResponseEntity<String> consumeItem(@Valid @RequestBody ConsumeRequestDto dto) {
        return ResponseEntity.ok(service.consumeItem(dto));
    }

    @GetMapping("/getUserId/{userId}")
    public ResponseEntity<List<InventoryResponseDto>> getInventoryByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getInventoryByUserId(userId));
    }

    @GetMapping("/user/{userId}/item/{itemId}")
    public ResponseEntity<SimpleInventoryResponseDto> getSpecificItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(service.getSpecificItem(userId, itemId));
    }

    @GetMapping("/user/{userId}/item/{itemId}/check")
    public ResponseEntity<Boolean> checkHasItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {

        return ResponseEntity.ok(service.checkHasItem(userId, itemId));
    }

}

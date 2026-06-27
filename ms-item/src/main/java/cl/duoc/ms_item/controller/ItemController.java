package cl.duoc.ms_item.controller;

import cl.duoc.ms_item.dto.ItemRequestDto;
import cl.duoc.ms_item.dto.ItemResponseDto;
import cl.duoc.ms_item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/item")
public class ItemController {
    @Autowired

    private final ItemService service;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/getItemId/{id}")
    public ResponseEntity<ItemResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/rarity/{rarity}")
    public ResponseEntity<List<ItemResponseDto>> findByRarity(@PathVariable String rarity){
        return ResponseEntity.ok(service.findByRarity(rarity));
    }

    @GetMapping("/type/{itemType}")
    public ResponseEntity<List<ItemResponseDto>> findByType(@PathVariable String itemType){
        return ResponseEntity.ok(service.findByType(itemType));
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(service.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

}

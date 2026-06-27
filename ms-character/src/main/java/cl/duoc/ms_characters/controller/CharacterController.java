package cl.duoc.ms_characters.controller;

import cl.duoc.ms_characters.dto.*;
import cl.duoc.ms_characters.service.CharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/character")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService service;

    @PostMapping("/admin/create")
    public ResponseEntity<String> createBaseCharacter(@Valid @RequestBody BaseCharacterRequestDto dto) {
        return ResponseEntity.ok(service.createBaseCharacter(dto));
    }

    @PostMapping("/unlock")
    public ResponseEntity<String> unlockCharacterForUser(@Valid @RequestBody UnlockCharacterDto dto) {
        return ResponseEntity.ok(service.unlockCharacterForUser(dto));
    }

    @GetMapping("/roster/{userId}")
    public ResponseEntity<List<UserRosterResponseDto>> getUserRoster(@PathVariable long userId) {
        return ResponseEntity.ok(service.getUserRoster(userId));
    }

    @PostMapping("/equip")
    public ResponseEntity<String> equipItem(@Valid @RequestBody EquipItemDto dto) {
        return ResponseEntity.ok(service.equipItem(dto));
    }
    @GetMapping
    public ResponseEntity<List<AdminRosterResponseDto>>  getAdminRoster() {
        return ResponseEntity.ok(service.getAllCharacters());
    }

}
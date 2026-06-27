package cl.duoc.ms_combat.controller;

import cl.duoc.ms_combat.dto.CombatRequestDto;
import cl.duoc.ms_combat.model.Combat;
import cl.duoc.ms_combat.service.CombatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/combats")
@RequiredArgsConstructor
public class CombatController {

    private final CombatService combatService;

    @PostMapping("/setup")
    public ResponseEntity<Combat> setupCombat(@RequestBody CombatRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(combatService.createScenario(
                        dto.getEnemy(),
                        dto.getMaxParticipants(),
                        dto.getExperienceAwarded(),
                        dto.getCoinsAwarded(),
                        dto.getCurrencyType()
                ));
    }

    @PostMapping("/{id}/assign-team/{userId}")
    public ResponseEntity<Combat> assignTeam(@PathVariable Long id, @PathVariable Long userId, @RequestBody List<String> characterNames) {
        return ResponseEntity.ok(combatService.assignTeam(id, userId, characterNames));
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Combat> playCombat(@PathVariable Long id) {
        return ResponseEntity.ok(combatService.playCombat(id));
    }

    @GetMapping
    public ResponseEntity<List<Combat>> getAll() {
        return ResponseEntity.ok(combatService.findAll());
    }
}

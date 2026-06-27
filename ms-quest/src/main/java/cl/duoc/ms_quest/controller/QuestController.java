package cl.duoc.ms_quest.controller;

import cl.duoc.ms_quest.dto.QuestRequestDto;
import cl.duoc.ms_quest.dto.QuestResponseDto;
import cl.duoc.ms_quest.service.QuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quest")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService service;

    @PostMapping
    public ResponseEntity<QuestResponseDto> create(@Valid @RequestBody QuestRequestDto dto) {
        return ResponseEntity.ok(service.createQuest(dto));
    }

    @GetMapping
    public ResponseEntity<List<QuestResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAllQuests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuestById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteQuest(id));
    }

    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<String> assignQuestToUser(@PathVariable("id") Long questId,
                                                    @PathVariable("userId") Long userId) {
        service.assignQuestToUser(questId, userId);
        return ResponseEntity.ok("Misión asignada correctamente");
    }

    @PutMapping("/user/{userId}/quest/{questId}/progress")
    public ResponseEntity<String> updateProgress(@PathVariable Long userId,
                                                 @PathVariable Long questId,
                                                 @RequestParam(defaultValue = "1") int delta) {
        service.trackProgress(userId, questId, delta);
        return ResponseEntity.ok("Progreso actualizado");
    }
}

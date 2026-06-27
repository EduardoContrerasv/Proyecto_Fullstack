package cl.duoc.ms_quest.service;

import cl.duoc.ms_quest.dto.QuestRequestDto;
import cl.duoc.ms_quest.dto.QuestResponseDto;
import java.util.List;

public interface QuestService {
    QuestResponseDto createQuest(QuestRequestDto dto);
    List<QuestResponseDto> getAllQuests();
    QuestResponseDto getQuestById(Long id);
    String deleteQuest(Long id);
    void assignQuestToUser(Long questId, Long userId);
    void trackProgress(Long userId, Long questId, int progressDelta);
}
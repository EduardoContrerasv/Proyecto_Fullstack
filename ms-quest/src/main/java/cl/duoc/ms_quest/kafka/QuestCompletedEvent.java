package cl.duoc.ms_quest.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestCompletedEvent {
    private Long questId;
    private Long userId;
    private String questTitle;
    private int goldReward;
    private int expReward;
}

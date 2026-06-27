package cl.duoc.ms_quest.dto;


import cl.duoc.ms_quest.model.QuestType;
import lombok.Data;

@Data
public class QuestResponseDto {
    private Long id;
    private String title;
    private String description;
    private QuestType questType;
    private Integer objective;
    private Integer expReward;
    private Integer coinReward;
    private String status;
}
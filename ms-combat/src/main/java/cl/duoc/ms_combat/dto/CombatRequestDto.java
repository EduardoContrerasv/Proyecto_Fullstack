package cl.duoc.ms_combat.dto;

import lombok.Data;


@Data
public class CombatRequestDto {
    private String enemy;
    private Integer maxParticipants;
    private Integer experienceAwarded;
    private Integer coinsAwarded;
    private String currencyType;
}
package cl.duoc.ms_combat.kafka;

import cl.duoc.ms_combat.enums.CombatResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombatResultEvent {
    private Long combatId;
    private Long userId;
    private String enemy;
    private CombatResult result;
    private Integer experienceGained;
    private Integer coinsGained;
    private String currencyType;
}

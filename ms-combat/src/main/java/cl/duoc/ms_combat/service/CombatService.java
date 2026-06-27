package cl.duoc.ms_combat.service;

import cl.duoc.ms_combat.dto.CombatRequestDto;
import cl.duoc.ms_combat.model.Combat;
import java.util.List;

public interface CombatService {
    Combat createScenario(String enemy, Integer capacity, Integer exp, Integer coins, String currencyType);
    Combat assignTeam(Long combatId, Long userId, List<String> characterNames);
    Combat playCombat(Long combatId);
    List<Combat> findAll();
}
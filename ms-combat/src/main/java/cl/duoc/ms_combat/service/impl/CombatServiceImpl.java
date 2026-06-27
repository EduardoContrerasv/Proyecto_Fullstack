package cl.duoc.ms_combat.service.impl;

import cl.duoc.ms_combat.client.CharacterFeignClient;
import cl.duoc.ms_combat.client.CurrencyFeignClient;
import cl.duoc.ms_combat.dto.CharacterDto;
import cl.duoc.ms_combat.dto.CurrencyDto;
import cl.duoc.ms_combat.enums.CombatResult;
import cl.duoc.ms_combat.kafka.CombatKafkaProducer;
import cl.duoc.ms_combat.kafka.CombatResultEvent;
import cl.duoc.ms_combat.model.Combat;
import cl.duoc.ms_combat.repository.CombatRepository;
import cl.duoc.ms_combat.service.CombatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombatServiceImpl implements CombatService {

    private final CombatRepository combatRepository;
    private final CharacterFeignClient characterFeignClient;
    private final CurrencyFeignClient currencyFeignClient;
    private final CombatKafkaProducer kafkaProducer;

    @Override
    public Combat createScenario(String enemy, Integer capacity, Integer exp, Integer coins, String currencyType) {
        Combat combat = new Combat();
        combat.setEnemy(enemy);
        combat.setMaxParticipants(capacity);
        combat.setBaseExperience(exp != null ? exp : 0);
        combat.setBaseCoins(coins != null ? coins : 0);
        combat.setCurrencyType((currencyType == null || currencyType.isBlank()) ? "GOLD" : currencyType);
        combat.setCombatDate(LocalDateTime.now());
        return combatRepository.save(combat);
    }

    @Override
    public Combat assignTeam(Long combatId, Long userId, List<String> characterNames) {
        Combat combat = combatRepository.findById(combatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combate no encontrado con id " + combatId));

        List<CharacterDto> allUserCharacters = characterFeignClient.getCharactersByUserId(userId);

        List<String> names = new ArrayList<>();
        for (String requestedName : characterNames) {
            CharacterDto found = allUserCharacters.stream()
                    .filter(c -> c.getCharacterName() != null
                            && c.getCharacterName().equalsIgnoreCase(requestedName))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Personaje " + requestedName + " no encontrado o no esta desbloqueado"));
            names.add(found.getCharacterName());
        }

        combat.setUserId(userId);
        combat.setCharacterNames(names);
        combat.setCharacterIds(new ArrayList<>());
        return combatRepository.save(combat);
    }

    @Override
    public Combat playCombat(Long id) {
        Combat combat = combatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combate no encontrado con id " + id));

        boolean win = new Random().nextBoolean();

        if (win) {
            combat.setResult(CombatResult.VICTORY);
            combat.setExperienceGained(combat.getBaseExperience());
            combat.setCoinsGained(combat.getBaseCoins());
        } else {
            combat.setResult(CombatResult.DEFEAT);
            combat.setExperienceGained((int) (combat.getBaseExperience() * 0.1));
            combat.setCoinsGained((int) (combat.getBaseCoins() * 0.1));
        }

        Combat saved = combatRepository.save(combat);
        grantReward(saved);
        publishCombatEvent(saved);
        return saved;
    }

    @Override
    public List<Combat> findAll() {
        return combatRepository.findAll();
    }

    private void publishCombatEvent(Combat combat) {
        try {
            CombatResultEvent event = new CombatResultEvent(
                    combat.getId(),
                    combat.getUserId(),
                    combat.getEnemy(),
                    combat.getResult(),
                    combat.getExperienceGained(),
                    combat.getCoinsGained(),
                    combat.getCurrencyType()
            );
            kafkaProducer.publishCombatResult(event);
        } catch (Exception e) {
            log.error("Error al publicar evento Kafka del combate {}: {}", combat.getId(), e.getMessage());
        }
    }

    private void grantReward(Combat combat) {
        Integer coins = combat.getCoinsGained();
        if (combat.getUserId() == null || coins == null || coins <= 0) {
            return;
        }
        String currency = (combat.getCurrencyType() == null || combat.getCurrencyType().isBlank())
                ? "GOLD" : combat.getCurrencyType();
        try {
            currencyFeignClient.addCurrency(combat.getUserId(), new CurrencyDto(currency, coins));
            log.info("Recompensa de {} {} acreditada al usuario {}", coins, currency, combat.getUserId());
        } catch (Exception e) {
            log.error("No se pudo acreditar la recompensa de combate al usuario {}: {}",
                    combat.getUserId(), e.getMessage());
        }
    }
}

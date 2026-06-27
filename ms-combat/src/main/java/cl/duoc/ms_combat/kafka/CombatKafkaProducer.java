package cl.duoc.ms_combat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombatKafkaProducer {

    private static final String TOPIC = "combat-results";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCombatResult(CombatResultEvent event) {
        log.info("Publicando evento de combate en topic '{}': combatId={}, userId={}, resultado={}",
                TOPIC, event.getCombatId(), event.getUserId(), event.getResult());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getCombatId()), event);
    }
}

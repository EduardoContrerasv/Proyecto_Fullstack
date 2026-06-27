package cl.duoc.ms_quest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestKafkaProducer {

    private static final String TOPIC = "quest-completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishQuestCompleted(QuestCompletedEvent event) {
        log.info("Publicando evento de quest completada en topic '{}': questId={}, userId={}",
                TOPIC, event.getQuestId(), event.getUserId());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getUserId()), event);
    }
}

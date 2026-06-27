package cl.duoc.ms_quest.service.impl;

import cl.duoc.ms_quest.client.CurrencyFeignClient;
import cl.duoc.ms_quest.client.UserFeignClient;
import cl.duoc.ms_quest.dto.CurrencyRequestDto;
import cl.duoc.ms_quest.dto.QuestRequestDto;
import cl.duoc.ms_quest.dto.QuestResponseDto;
import cl.duoc.ms_quest.kafka.QuestCompletedEvent;
import cl.duoc.ms_quest.kafka.QuestKafkaProducer;
import cl.duoc.ms_quest.model.Quest;
import cl.duoc.ms_quest.model.UserQuest;
import cl.duoc.ms_quest.repository.QuestRepository;
import cl.duoc.ms_quest.repository.UserQuestRepository;
import cl.duoc.ms_quest.service.QuestService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestRepository repository;
    private final UserFeignClient userFeignClient;
    private final UserQuestRepository userQuestRepository;
    private final CurrencyFeignClient currencyFeignClient;
    private final QuestKafkaProducer kafkaProducer;

    @Override
    public QuestResponseDto createQuest(QuestRequestDto dto) {
        log.info("createQuest dto {}", dto);
        Quest quest = toEntity(dto);
        quest = repository.save(quest);
        return toDto(quest);
    }

    @Override
    public List<QuestResponseDto> getAllQuests() {
        log.info("getAllQuests");
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuestResponseDto getQuestById(Long id) {
        log.info("getQuestById {}", id);
        Quest quest = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La quest con el id " + id + " no existe"));
        return toDto(quest);
    }

    @Override
    public String deleteQuest(Long id) {
        log.info("deleteQuest {}", id);
        Quest quest = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede borrar: La quest con el id " + id + " no existe"));
        String questTitle = quest.getTitle();
        repository.deleteById(id);
        return "Quest '" + questTitle + "' borrada correctamente";
    }

    @Override
    public void assignQuestToUser(Long questId, Long userId) {
        log.info("assignQuestToUser {}", questId);
        try {
            userFeignClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID " + userId);
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-user: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo validar el usuario con ms-user.");
        }

        Quest quest = repository.findById(questId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Misión no encontrada"));

        UserQuest userQuest = new UserQuest();
        userQuest.setUserId(userId);
        userQuest.setQuest(quest);
        userQuest.setObjectivesRemaining(quest.getObjective());
        userQuest.setCompleted(false);

        userQuestRepository.save(userQuest);
        log.info("La misión {} ha sido asignada al usuario {}", quest.getTitle(), userId);
    }

    @Override
    @Transactional
    public void trackProgress(Long userId, Long questId, int progressDelta) {
        log.info("trackProgress {}", progressDelta);
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new EntityNotFoundException("No existe seguimiento de esta quest para el usuario"));

        if (userQuest.isCompleted()) {
            return;
        }

        int remaining = userQuest.getObjectivesRemaining() - progressDelta;
        if (remaining < 0) {
            remaining = 0;
        }
        userQuest.setObjectivesRemaining(remaining);

        if (remaining == 0) {
            userQuest.setCompleted(true);
            Quest quest = userQuest.getQuest();
            int goldReward = quest.getGoldReward();
            if (goldReward > 0) {
                try {
                    currencyFeignClient.addCurrency(userId, new CurrencyRequestDto("GOLD", goldReward));
                    log.info("Recompensa de {} GOLD acreditada al usuario {}", goldReward, userId);
                } catch (Exception e) {
                    log.error("No se pudo acreditar la recompensa de la quest al usuario {}: {}",
                            userId, e.getMessage());
                }
            }
            try {
                kafkaProducer.publishQuestCompleted(new QuestCompletedEvent(
                        quest.getId(), userId, quest.getTitle(), goldReward, quest.getExpReward()));
            } catch (Exception e) {
                log.error("Error al publicar evento Kafka de quest completada para usuario {}: {}", userId, e.getMessage());
            }
        }
        userQuestRepository.save(userQuest);
    }

    private Quest toEntity(QuestRequestDto dto) {
        Quest quest = new Quest();
        quest.setTitle(dto.getTitle());
        quest.setDescription(dto.getDescription());
        quest.setQuestType(dto.getQuestType());
        quest.setObjective(dto.getObjective());
        quest.setExpReward(dto.getExpReward());
        quest.setGoldReward(dto.getCoinReward());
        quest.setStatus("ACTIVE");
        return quest;
    }

    private QuestResponseDto toDto(Quest entity) {
        QuestResponseDto dto = new QuestResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setQuestType(entity.getQuestType());
        dto.setObjective(entity.getObjective());
        dto.setExpReward(entity.getExpReward());
        dto.setCoinReward(entity.getGoldReward());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}

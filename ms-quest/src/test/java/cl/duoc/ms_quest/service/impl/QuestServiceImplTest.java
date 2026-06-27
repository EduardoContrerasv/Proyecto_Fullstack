package cl.duoc.ms_quest.service.impl;

import cl.duoc.ms_quest.client.CurrencyFeignClient;
import cl.duoc.ms_quest.client.UserFeignClient;
import cl.duoc.ms_quest.dto.QuestRequestDto;
import cl.duoc.ms_quest.dto.QuestResponseDto;
import cl.duoc.ms_quest.kafka.QuestCompletedEvent;
import cl.duoc.ms_quest.kafka.QuestKafkaProducer;
import cl.duoc.ms_quest.model.Quest;
import cl.duoc.ms_quest.model.QuestType;
import cl.duoc.ms_quest.model.UserQuest;
import cl.duoc.ms_quest.repository.QuestRepository;
import cl.duoc.ms_quest.repository.UserQuestRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestServiceImplTest {

    @Mock private QuestRepository repository;
    @Mock private UserFeignClient userFeignClient;
    @Mock private UserQuestRepository userQuestRepository;
    @Mock private CurrencyFeignClient currencyFeignClient;
    @Mock private QuestKafkaProducer kafkaProducer;
    @InjectMocks private QuestServiceImpl service;

    private Quest questConRecompensa(int gold, int exp) {
        Quest q = new Quest();
        q.setId(10L);
        q.setTitle("Mata Goblins");
        q.setDescription("Derrota 3 goblins");
        q.setQuestType(QuestType.SIDEQUEST);
        q.setObjective(3);
        q.setGoldReward(gold);
        q.setExpReward(exp);
        q.setStatus("ACTIVE");
        return q;
    }

    private UserQuest userQuestActiva(Quest quest, int remaining) {
        UserQuest uq = new UserQuest();
        uq.setUserId(1L);
        uq.setQuest(quest);
        uq.setObjectivesRemaining(remaining);
        uq.setCompleted(false);
        return uq;
    }

    // ─── createQuest ──────────────────────────────────────────────────

    @Test
    void createQuest_guardaYRetornaDto() {
        QuestRequestDto dto = new QuestRequestDto();
        dto.setTitle("Matar Dragón");
        dto.setDescription("Derrota al dragón");
        dto.setQuestType(QuestType.MAINSCENARIOQUEST);
        dto.setObjective(1);
        dto.setExpReward(500);
        dto.setCoinReward(200);

        Quest saved = new Quest();
        saved.setId(1L);
        saved.setTitle("Matar Dragón");
        saved.setDescription("Derrota al dragón");
        saved.setQuestType(QuestType.MAINSCENARIOQUEST);
        saved.setObjective(1);
        saved.setExpReward(500);
        saved.setGoldReward(200);
        saved.setStatus("ACTIVE");
        when(repository.save(any(Quest.class))).thenReturn(saved);

        QuestResponseDto result = service.createQuest(dto);

        assertThat(result.getTitle()).isEqualTo("Matar Dragón");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getCoinReward()).isEqualTo(200);
        verify(repository).save(any(Quest.class));
    }

    @Test
    void createQuest_estadoInicialEsActive() {
        QuestRequestDto dto = new QuestRequestDto();
        dto.setTitle("Test");
        dto.setDescription("Desc");
        dto.setQuestType(QuestType.SIDEQUEST);
        dto.setObjective(1);
        when(repository.save(any(Quest.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createQuest(dto);

        ArgumentCaptor<Quest> captor = ArgumentCaptor.forClass(Quest.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
    }

    // ─── getAllQuests ──────────────────────────────────────────────────

    @Test
    void getAllQuests_retornaListaMapeada() {
        Quest q1 = questConRecompensa(100, 200);
        Quest q2 = questConRecompensa(50, 100);
        when(repository.findAll()).thenReturn(List.of(q1, q2));

        List<QuestResponseDto> result = service.getAllQuests();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllQuests_listaVacia_retornaListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAllQuests()).isEmpty();
    }

    // ─── getQuestById ─────────────────────────────────────────────────

    @Test
    void getQuestById_existente_retornaDto() {
        Quest q = questConRecompensa(100, 200);
        when(repository.findById(10L)).thenReturn(Optional.of(q));

        QuestResponseDto result = service.getQuestById(10L);

        assertThat(result.getTitle()).isEqualTo("Mata Goblins");
    }

    @Test
    void getQuestById_inexistente_lanza404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getQuestById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    // ─── deleteQuest ──────────────────────────────────────────────────

    @Test
    void deleteQuest_existente_eliminaYRetornaMensaje() {
        Quest q = questConRecompensa(0, 0);
        q.setTitle("Quest a borrar");
        when(repository.findById(10L)).thenReturn(Optional.of(q));

        String result = service.deleteQuest(10L);

        verify(repository).deleteById(10L);
        assertThat(result).contains("Quest a borrar");
    }

    @Test
    void deleteQuest_inexistente_lanza404() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteQuest(9L))
                .isInstanceOf(ResponseStatusException.class);
        verify(repository, never()).deleteById(any());
    }

    // ─── assignQuestToUser ────────────────────────────────────────────

    @Test
    void assignQuestToUser_usuarioInexistente_lanza404() {
        doThrow(mock(FeignException.NotFound.class)).when(userFeignClient).getUserById(5L);

        assertThatThrownBy(() -> service.assignQuestToUser(1L, 5L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("5");
        verifyNoInteractions(userQuestRepository);
    }

    @Test
    void assignQuestToUser_questInexistente_lanza404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignQuestToUser(99L, 1L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void assignQuestToUser_exitoso_creaUserQuestConObjetivo() {
        Quest q = questConRecompensa(100, 200);
        when(repository.findById(10L)).thenReturn(Optional.of(q));

        service.assignQuestToUser(10L, 1L);

        ArgumentCaptor<UserQuest> captor = ArgumentCaptor.forClass(UserQuest.class);
        verify(userQuestRepository).save(captor.capture());
        UserQuest uq = captor.getValue();
        assertThat(uq.getUserId()).isEqualTo(1L);
        assertThat(uq.getObjectivesRemaining()).isEqualTo(3);
        assertThat(uq.isCompleted()).isFalse();
    }

    // ─── trackProgress ────────────────────────────────────────────────

    @Test
    void trackProgress_completaLaQuest_acreditaRecompensaYPublicaKafka() {
        Quest quest = questConRecompensa(50, 100);
        UserQuest uq = userQuestActiva(quest, 3);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 10L)).thenReturn(Optional.of(uq));

        service.trackProgress(1L, 10L, 3);

        assertThat(uq.isCompleted()).isTrue();
        assertThat(uq.getObjectivesRemaining()).isZero();
        verify(currencyFeignClient).addCurrency(eq(1L), any());
        verify(userQuestRepository).save(uq);

        ArgumentCaptor<QuestCompletedEvent> eventCaptor = ArgumentCaptor.forClass(QuestCompletedEvent.class);
        verify(kafkaProducer).publishQuestCompleted(eventCaptor.capture());
        QuestCompletedEvent event = eventCaptor.getValue();
        assertThat(event.getUserId()).isEqualTo(1L);
        assertThat(event.getQuestId()).isEqualTo(10L);
        assertThat(event.getGoldReward()).isEqualTo(50);
        assertThat(event.getExpReward()).isEqualTo(100);
    }

    @Test
    void trackProgress_progresoParcial_noCompletaNiAcreditaNiPublicaKafka() {
        Quest quest = questConRecompensa(50, 100);
        UserQuest uq = userQuestActiva(quest, 3);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 10L)).thenReturn(Optional.of(uq));

        service.trackProgress(1L, 10L, 1);

        assertThat(uq.isCompleted()).isFalse();
        assertThat(uq.getObjectivesRemaining()).isEqualTo(2);
        verifyNoInteractions(currencyFeignClient);
        verifyNoInteractions(kafkaProducer);
        verify(userQuestRepository).save(uq);
    }

    @Test
    void trackProgress_deltaExcedente_quedaEnCero() {
        Quest quest = questConRecompensa(50, 0);
        UserQuest uq = userQuestActiva(quest, 2);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 10L)).thenReturn(Optional.of(uq));

        service.trackProgress(1L, 10L, 100);

        assertThat(uq.getObjectivesRemaining()).isZero();
        assertThat(uq.isCompleted()).isTrue();
    }

    @Test
    void trackProgress_yaCompletada_esIdempotente() {
        UserQuest uq = new UserQuest();
        uq.setCompleted(true);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 2L)).thenReturn(Optional.of(uq));

        service.trackProgress(1L, 2L, 5);

        verify(userQuestRepository, never()).save(any());
        verifyNoInteractions(currencyFeignClient);
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void trackProgress_sinSeguimiento_lanzaEntityNotFound() {
        when(userQuestRepository.findByUserIdAndQuestId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.trackProgress(1L, 2L, 1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void trackProgress_recompensaCero_noLlamaCurrency() {
        Quest quest = questConRecompensa(0, 50);
        UserQuest uq = userQuestActiva(quest, 1);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 10L)).thenReturn(Optional.of(uq));

        service.trackProgress(1L, 10L, 1);

        assertThat(uq.isCompleted()).isTrue();
        verifyNoInteractions(currencyFeignClient);
        verify(kafkaProducer).publishQuestCompleted(any());
    }

    @Test
    void trackProgress_kafkaFalla_noLanzaExcepcion() {
        Quest quest = questConRecompensa(10, 50);
        UserQuest uq = userQuestActiva(quest, 1);
        when(userQuestRepository.findByUserIdAndQuestId(1L, 10L)).thenReturn(Optional.of(uq));
        doThrow(new RuntimeException("Kafka caído")).when(kafkaProducer).publishQuestCompleted(any());

        assertThatCode(() -> service.trackProgress(1L, 10L, 1)).doesNotThrowAnyException();
    }
}

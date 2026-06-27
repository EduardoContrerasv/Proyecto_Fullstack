package cl.duoc.ms_combat.service.impl;

import cl.duoc.ms_combat.client.CharacterFeignClient;
import cl.duoc.ms_combat.client.CurrencyFeignClient;
import cl.duoc.ms_combat.dto.CharacterDto;
import cl.duoc.ms_combat.enums.CombatResult;
import cl.duoc.ms_combat.kafka.CombatKafkaProducer;
import cl.duoc.ms_combat.kafka.CombatResultEvent;
import cl.duoc.ms_combat.model.Combat;
import cl.duoc.ms_combat.repository.CombatRepository;
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
class CombatServiceImplTest {

    @Mock private CombatRepository combatRepository;
    @Mock private CharacterFeignClient characterFeignClient;
    @Mock private CurrencyFeignClient currencyFeignClient;
    @Mock private CombatKafkaProducer kafkaProducer;
    @InjectMocks private CombatServiceImpl service;

    // ─── createScenario ───────────────────────────────────────────────

    @Test
    void createScenario_valoresNulos_usaValoresPorDefecto() {
        when(combatRepository.save(any(Combat.class))).thenAnswer(inv -> inv.getArgument(0));

        Combat c = service.createScenario("Dragon", 4, null, null, null);

        assertThat(c.getBaseExperience()).isZero();
        assertThat(c.getBaseCoins()).isZero();
        assertThat(c.getCurrencyType()).isEqualTo("GOLD");
    }

    @Test
    void createScenario_valoresExplicitos_losAsignaCorrectamente() {
        when(combatRepository.save(any(Combat.class))).thenAnswer(inv -> inv.getArgument(0));

        Combat c = service.createScenario("Goblin", 2, 500, 250, "GEMS");

        assertThat(c.getEnemy()).isEqualTo("Goblin");
        assertThat(c.getMaxParticipants()).isEqualTo(2);
        assertThat(c.getBaseExperience()).isEqualTo(500);
        assertThat(c.getBaseCoins()).isEqualTo(250);
        assertThat(c.getCurrencyType()).isEqualTo("GEMS");
        assertThat(c.getCombatDate()).isNotNull();
    }

    @Test
    void createScenario_currencyTypeBlanco_usaGoldPorDefecto() {
        when(combatRepository.save(any(Combat.class))).thenAnswer(inv -> inv.getArgument(0));

        Combat c = service.createScenario("Slime", 1, 10, 5, "   ");

        assertThat(c.getCurrencyType()).isEqualTo("GOLD");
    }

    @Test
    void createScenario_guardaEnRepositorio() {
        when(combatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createScenario("Boss", 5, 100, 50, "GOLD");

        verify(combatRepository).save(any(Combat.class));
    }

    // ─── assignTeam ───────────────────────────────────────────────────

    @Test
    void assignTeam_combateInexistente_lanza404() {
        when(combatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignTeam(99L, 1L, List.of("Héroe")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("99");
    }

    @Test
    void assignTeam_personajeNoDesbloqueado_lanza404() {
        Combat combat = new Combat();
        when(combatRepository.findById(1L)).thenReturn(Optional.of(combat));

        CharacterDto other = new CharacterDto();
        other.setCharacterName("OtroHeroe");
        when(characterFeignClient.getCharactersByUserId(1L)).thenReturn(List.of(other));

        assertThatThrownBy(() -> service.assignTeam(1L, 1L, List.of("Aragorn")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Aragorn");
    }

    @Test
    void assignTeam_personajeValido_asignaEquipo() {
        Combat combat = new Combat();
        when(combatRepository.findById(1L)).thenReturn(Optional.of(combat));
        when(combatRepository.save(combat)).thenReturn(combat);

        CharacterDto hero = new CharacterDto();
        hero.setCharacterName("Aragorn");
        when(characterFeignClient.getCharactersByUserId(2L)).thenReturn(List.of(hero));

        Combat result = service.assignTeam(1L, 2L, List.of("aragorn"));

        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getCharacterNames()).contains("Aragorn");
        verify(combatRepository).save(combat);
    }

    // ─── playCombat ───────────────────────────────────────────────────

    @Test
    void playCombat_recompensaConsistenteConElResultado() {
        Combat c = new Combat();
        c.setUserId(1L);
        c.setBaseExperience(200);
        c.setBaseCoins(100);
        c.setCurrencyType("GOLD");
        when(combatRepository.findById(5L)).thenReturn(Optional.of(c));
        when(combatRepository.save(c)).thenReturn(c);

        Combat res = service.playCombat(5L);

        assertThat(res.getResult()).isIn(CombatResult.VICTORY, CombatResult.DEFEAT);
        if (res.getResult() == CombatResult.VICTORY) {
            assertThat(res.getCoinsGained()).isEqualTo(100);
            assertThat(res.getExperienceGained()).isEqualTo(200);
        } else {
            assertThat(res.getCoinsGained()).isEqualTo(10);
            assertThat(res.getExperienceGained()).isEqualTo(20);
        }
    }

    @Test
    void playCombat_conUsuario_acreditaRecompensaYPublicaKafka() {
        Combat c = new Combat();
        c.setUserId(1L);
        c.setBaseExperience(200);
        c.setBaseCoins(100);
        c.setCurrencyType("GOLD");
        when(combatRepository.findById(5L)).thenReturn(Optional.of(c));
        when(combatRepository.save(c)).thenReturn(c);

        service.playCombat(5L);

        verify(currencyFeignClient).addCurrency(eq(1L), any());

        ArgumentCaptor<CombatResultEvent> eventCaptor = ArgumentCaptor.forClass(CombatResultEvent.class);
        verify(kafkaProducer).publishCombatResult(eventCaptor.capture());
        CombatResultEvent event = eventCaptor.getValue();
        assertThat(event.getUserId()).isEqualTo(1L);
        assertThat(event.getResult()).isIn(CombatResult.VICTORY, CombatResult.DEFEAT);
    }

    @Test
    void playCombat_sinUsuarioAsignado_noAcreditaRecompensaYPublicaKafka() {
        Combat c = new Combat();
        c.setBaseCoins(100);
        c.setBaseExperience(200);
        when(combatRepository.findById(5L)).thenReturn(Optional.of(c));
        when(combatRepository.save(c)).thenReturn(c);

        service.playCombat(5L);

        verifyNoInteractions(currencyFeignClient);
        verify(kafkaProducer).publishCombatResult(any());
    }

    @Test
    void playCombat_combateInexistente_lanza404() {
        when(combatRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.playCombat(9L))
                .isInstanceOf(ResponseStatusException.class);

        verifyNoInteractions(kafkaProducer, currencyFeignClient);
    }

    @Test
    void playCombat_recompensaCero_noAcreditaCurrency() {
        Combat c = new Combat();
        c.setUserId(3L);
        c.setBaseCoins(0);
        c.setBaseExperience(100);
        c.setCurrencyType("GOLD");
        when(combatRepository.findById(7L)).thenReturn(Optional.of(c));
        when(combatRepository.save(c)).thenReturn(c);

        service.playCombat(7L);

        verifyNoInteractions(currencyFeignClient);
        verify(kafkaProducer).publishCombatResult(any());
    }

    @Test
    void playCombat_kafkaFalla_noLanzaExcepcion() {
        Combat c = new Combat();
        c.setUserId(1L);
        c.setBaseExperience(100);
        c.setBaseCoins(50);
        c.setCurrencyType("GOLD");
        when(combatRepository.findById(1L)).thenReturn(Optional.of(c));
        when(combatRepository.save(c)).thenReturn(c);
        doThrow(new RuntimeException("Kafka caído")).when(kafkaProducer).publishCombatResult(any());

        assertThatCode(() -> service.playCombat(1L)).doesNotThrowAnyException();
    }

    // ─── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaDelRepositorio() {
        Combat c1 = new Combat();
        Combat c2 = new Combat();
        when(combatRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Combat> result = service.findAll();

        assertThat(result).hasSize(2);
    }
}

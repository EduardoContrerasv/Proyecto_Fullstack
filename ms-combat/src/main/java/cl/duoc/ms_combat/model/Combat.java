package cl.duoc.ms_combat.model;

import cl.duoc.ms_combat.enums.CombatResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "combats")
@Data
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String enemy;
    private Integer maxParticipants;

    @ElementCollection
    @CollectionTable(name = "combat_participants", joinColumns = @JoinColumn(name = "combat_id"))
    @Column(name = "character_id")
    @JsonIgnore
    private List<Long> characterIds;

    @ElementCollection
    @CollectionTable(name = "combat_character_names", joinColumns = @JoinColumn(name = "combat_id"))
    @Column(name = "character_name")
    private List<String> characterNames;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private CombatResult result;

    private Integer experienceGained;
    private Integer coinsGained;
    private LocalDateTime combatDate;

    @JsonIgnore
    private Integer baseExperience;

    @JsonIgnore
    private Integer baseCoins;

    private String currencyType;

}

package cl.duoc.ms_quest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quests")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", nullable = false)
    private QuestType questType;

    @Column(nullable = false)
    private int objective;

    @Column(name = "exp_reward", nullable = false)
    private int expReward;

    @Column(name = "coin_reward", nullable = false)
    private int goldReward;

    @Column(nullable = false)
    private String status = "ACTIVE";
}
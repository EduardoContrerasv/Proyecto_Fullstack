package cl.duoc.ms_quest.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_quest")
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Column(name = "objectives_remaining")
    private int objectivesRemaining;

    @Column(name = "is_completed")
    private boolean completed =false;
}
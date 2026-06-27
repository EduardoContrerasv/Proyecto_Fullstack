package cl.duoc.ms_characters.model;

import cl.duoc.ms_characters.enums.CharacterArchetype;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "base_characters")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BaseCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharacterArchetype characterArchetype;
    @Column(name = "default_cosmetic_item_id", nullable = false)
    private Long defaultCosmeticItemId;

    private int baseHealth;
    private int baseAttack;
    private int baseDefense;
}
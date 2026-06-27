package cl.duoc.ms_characters.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_characters")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "base_character_id", nullable = false)
    private BaseCharacter baseCharacter;

    @Column(name = "equipped_weapon_id")
    private Long equippedWeaponId;

    @Column(name = "equipped_armor_id")
    private Long equippedArmorId;

    @Column(name = "equipped_cosmetic_id", nullable = false)
    private Long equippedCosmeticId;

    private int currentLevel = 1;
    private int currentExperience = 0;

}
package cl.duoc.ms_combat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDto {

    private Long userId;
    private String characterName;
    private int level;
    private String status;
}
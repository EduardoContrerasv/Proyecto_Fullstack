package cl.duoc.ms_characters.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "characterName", "characterArchetype", "health", "attack", "defense" })
public class UserRosterResponseDto {
    private String characterName;
    private String characterClass;
    private int level;
    private int health;
    private int attack;
    private int defense;
}

package cl.duoc.ms_characters.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "characterName", "characterArchetype", "health", "attack", "defense" })
public class AdminRosterResponseDto {
    private Long id;
    private String characterName;
    private String characterArchetype;
    private int health;
    private int attack;
    private int defense;
}

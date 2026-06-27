package cl.duoc.ms_characters.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFeignDto {
    private Long id;
    private String username;
    private LocalDateTime registerDate;
    private int accountLevel;
}
package cl.duoc.ms_combat.client;

import cl.duoc.ms_combat.dto.CharacterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-character", url = "http://ms-character:8091/api/v1/character")
public interface CharacterFeignClient {

    @GetMapping("/roster/{userId}")
    List<CharacterDto> getCharactersByUserId(@PathVariable("userId") Long userId);

}

package cl.duoc.ms_characters.service;


import cl.duoc.ms_characters.dto.*;

import java.util.List;


public interface CharacterService {
    List<UserRosterResponseDto> getUserRoster(long userId);
    String createBaseCharacter(BaseCharacterRequestDto dto);
    String unlockCharacterForUser(UnlockCharacterDto dto);
    String equipItem(EquipItemDto dto);
    List<AdminRosterResponseDto> getAllCharacters();
}

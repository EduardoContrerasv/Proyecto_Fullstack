package cl.duoc.ms_characters.service.impl;

import cl.duoc.ms_characters.client.InventoryClient;
import cl.duoc.ms_characters.client.UserFeignClient;
import cl.duoc.ms_characters.dto.*;
import cl.duoc.ms_characters.model.BaseCharacter;
import cl.duoc.ms_characters.model.UserCharacter;
import cl.duoc.ms_characters.repository.BaseCharacterRepository;
import cl.duoc.ms_characters.repository.UserCharacterRepository;
import cl.duoc.ms_characters.service.CharacterService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import cl.duoc.ms_characters.client.ItemClient;
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements CharacterService {

    private final BaseCharacterRepository baseCharacterRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final UserFeignClient userFeignClient;
    private final InventoryClient inventoryClient;
    private final ItemClient itemClient;


    @Override
    public List<UserRosterResponseDto> getUserRoster(long userId) {
        log.info("getUserRoster");
        List<UserCharacter> roster = userCharacterRepository.findByUserId(userId);

        return roster.stream().map(uc -> {
            UserRosterResponseDto dto = new UserRosterResponseDto();
            dto.setCharacterName(uc.getBaseCharacter().getName());
            dto.setCharacterClass(uc.getBaseCharacter().getCharacterArchetype().name());
            dto.setLevel(uc.getCurrentLevel());
            dto.setHealth(uc.getBaseCharacter().getBaseHealth());
            dto.setAttack(uc.getBaseCharacter().getBaseAttack());
            dto.setDefense(uc.getBaseCharacter().getBaseDefense());
            return dto;
        }).toList();
    }

    @Override
    public String createBaseCharacter(BaseCharacterRequestDto dto) {
        log.info("createBaseCharacter");
        if (baseCharacterRepository.findByName(dto.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El héroe " + dto.getName() + " ya existe.");
        }

        BaseCharacter hero = new BaseCharacter();
        hero.setName(dto.getName());
        hero.setCharacterArchetype(dto.getCharacterArchetype());
        hero.setDefaultCosmeticItemId(dto.getDefaultCosmeticItemId());

        int rawHealth = dto.getBaseHealth();
        int rawAttack = dto.getBaseAttack();
        int rawDefense = dto.getBaseDefense();

        switch (dto.getCharacterArchetype()) {
            case ATTACK:
                hero.setBaseAttack((int) (rawAttack * 1.2));
                hero.setBaseHealth((int) (rawHealth * 0.9));
                hero.setBaseDefense((int) (rawDefense * 0.7));
                break;

            case VANGUARD:
                hero.setBaseAttack((int) (rawAttack * 0.6));
                hero.setBaseHealth((int) (rawHealth * 1.5));
                hero.setBaseDefense((int) (rawDefense * 1.3));
                break;

            case STRATEGIST:
                hero.setBaseAttack((int) (rawAttack * 1.05));
                hero.setBaseHealth((int) (rawHealth * 1.05));
                hero.setBaseDefense((int) (rawDefense * 0.9));
                break;

            case SUPPORT:
                hero.setBaseAttack((int) (rawAttack * 0.7));
                hero.setBaseHealth((int) (rawHealth * 1.1));
                hero.setBaseDefense((int) (rawDefense * 1.1));
                break;
        }

        baseCharacterRepository.save(hero);
        return "Héroe " + hero.getName() + " creado";
    }

    @Override
    public String unlockCharacterForUser(UnlockCharacterDto dto) {
        log.info("unlockCharacterForUser");
        try {
            userFeignClient.getUserById(dto.getUserId());
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID " + dto.getUserId());
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-user: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo validar el usuario con ms-user.");
        }
        BaseCharacter blueprint = baseCharacterRepository.findById(dto.getBaseCharacterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ese héroe no existe en el juego."));

        boolean alreadyOwns = userCharacterRepository.existsByUserIdAndBaseCharacterId(dto.getUserId(), dto.getBaseCharacterId());
        if (alreadyOwns) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya posee a este héroe.");
        }

        UserCharacter playerCopy = new UserCharacter();
        playerCopy.setUserId(dto.getUserId());
        playerCopy.setBaseCharacter(blueprint);
        playerCopy.setEquippedCosmeticId(blueprint.getDefaultCosmeticItemId());

        userCharacterRepository.save(playerCopy);
        return "El jugador ha desbloqueado a " + blueprint.getName() + "!";
    }

    @Override
    public String equipItem(EquipItemDto dto) {
        log.info("equipItem");

        UserCharacter playerHero = userCharacterRepository
                .findByUserIdAndBaseCharacterId(dto.getUserId(), dto.getUserCharacterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No posees a este héroe."));

        boolean ownsItem;
        try {
            ownsItem = inventoryClient.checkHasItem(dto.getUserId(), dto.getItemId());
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-inventory: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error consultando el inventario.");
        }
        if (!ownsItem) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tienes este item en tu inventario.");

        ItemFeignDto itemData;
        try {
            itemData = itemClient.getItemById(dto.getItemId());
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El item no existe en la base de datos.");
        } catch (FeignException e) {
            log.error("Error de comunicacion con ms-item: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo verificar el item con ms-item.");
        }

        switch (dto.getSlot()) {
            case WEAPON:
                if (!"WEAPON".equals(itemData.getItemType())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esto no es un arma.");
                playerHero.setEquippedWeaponId(dto.getItemId());
                break;
            case ARMOR:
                if (!"ARMOR".equals(itemData.getItemType())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esto no es una armadura.");
                playerHero.setEquippedArmorId(dto.getItemId());
                break;
            case COSMETIC:
                if (!"COSMETIC".equals(itemData.getItemType())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esto no es un cosmético.");
                playerHero.setEquippedCosmeticId(dto.getItemId());
                break;
        }

        userCharacterRepository.save(playerHero);
        return "Item equipado: " + dto.getSlot();
    }

    @Override
    public List<AdminRosterResponseDto> getAllCharacters() {
        log.info("getAllCharacters");

        List<BaseCharacter> blueprints = baseCharacterRepository.findAll();

        return blueprints.stream().map(hero -> {
            AdminRosterResponseDto dto = new AdminRosterResponseDto();
            dto.setId(hero.getId());
            dto.setCharacterName(hero.getName());
            dto.setCharacterArchetype(hero.getCharacterArchetype().name());

            dto.setHealth(hero.getBaseHealth());
            dto.setAttack(hero.getBaseAttack());
            dto.setDefense(hero.getBaseDefense());

            return dto;
        }).toList();
    }
}
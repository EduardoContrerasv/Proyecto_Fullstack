package cl.duoc.ms_characters.repository;

import cl.duoc.ms_characters.model.UserCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {
    boolean existsByUserIdAndBaseCharacterId(Long userId, Long baseCharacterId);
    List<UserCharacter> findByUserId(Long userId);
    java.util.Optional<UserCharacter> findByUserIdAndBaseCharacterId(Long userId, Long baseCharacterId);
}

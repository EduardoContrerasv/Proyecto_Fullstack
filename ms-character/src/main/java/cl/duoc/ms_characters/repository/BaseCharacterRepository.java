package cl.duoc.ms_characters.repository;

import cl.duoc.ms_characters.model.BaseCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseCharacterRepository extends JpaRepository<BaseCharacter, Long> {
    Optional<BaseCharacter> findByName(String name);
}

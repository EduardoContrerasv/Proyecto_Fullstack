package cl.duoc.ms_combat.repository;

import cl.duoc.ms_combat.model.Combat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CombatRepository extends JpaRepository<Combat, Long> {
}
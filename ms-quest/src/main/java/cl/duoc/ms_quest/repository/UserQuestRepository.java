package cl.duoc.ms_quest.repository;

import cl.duoc.ms_quest.model.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    Optional<UserQuest> findByUserIdAndQuestId(Long userId, Long questId);
}


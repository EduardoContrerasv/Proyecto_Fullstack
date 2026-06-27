package cl.duoc.ms_quest.repository;

import cl.duoc.ms_quest.model.Quest;
import cl.duoc.ms_quest.model.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {
}

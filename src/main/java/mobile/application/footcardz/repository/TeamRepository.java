package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Integer> {
}
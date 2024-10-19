package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Integer> {
}

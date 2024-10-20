package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LeagueRepository extends JpaRepository<League, Integer> {
    boolean existsByName(String name);
    @Query("SELECT CASE WHEN COUNT(T) > 0 THEN true ELSE false END " +
        "FROM Team T WHERE T.league.id = :id")
    boolean existsTeamByLeagueId(Integer id);
}

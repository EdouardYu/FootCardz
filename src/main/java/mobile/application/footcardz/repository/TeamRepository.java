package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface TeamRepository extends JpaRepository<Team, Integer> {
    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(P) > 0 THEN true ELSE false END " +
        "FROM Player P WHERE P.team.id = :id")
    boolean existsPlayerByTeamId(Integer id);
}
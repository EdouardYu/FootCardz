package mobile.application.footcardz.repository;

import jakarta.annotation.Nonnull;
import mobile.application.footcardz.entity.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    @Query(value = "SELECT P FROM Player P WHERE P.id NOT IN (SELECT UPC.player.id FROM UserPlayerCollection UPC WHERE UPC.user.id = :userId) ORDER BY RANDOM() LIMIT 1")
    Optional<Player> findRandomPlayerNotOwnedByUser(Integer userId);

    @Nonnull
    Page<Player> findAll(@Nonnull Pageable pageable);

    @Query("SELECT P FROM Player P " +
        "WHERE (LOWER(P.name) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(P.team.name) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(P.team.league.name) LIKE LOWER(CONCAT(:term, '%')) " +
        "OR LOWER(P.nationality.name) LIKE LOWER(CONCAT(:term, '%')))")
    Page<Player> searchPlayers(String term, Pageable pageable);
}

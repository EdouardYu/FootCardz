package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.user.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface JwtRepository extends JpaRepository<Jwt, Integer> {
    Optional<Jwt> findByValueAndEnabled(String value, Boolean enabled);

    @Query("SELECT J FROM Jwt J WHERE J.enabled = :enabled AND J.user.username = :username")
    Optional<Jwt> findUserTokenByValidity(String username, Boolean enabled);

    @Modifying
    @Query("UPDATE Jwt J SET J.enabled = false WHERE J.user.username = :username")
    void disableTokensByUser(String username);

    @Query("FROM Jwt j WHERE j.enabled = :enabled AND j.refreshToken.value = :value AND j.refreshToken.expiredAt > :instant")
    Optional<Jwt> findByEnabledRefreshToken(Boolean enabled, Instant instant, String value);

    void deleteAllByEnabledOrRefreshTokenExpiredAtBefore(Boolean enabled, Instant instant);
}

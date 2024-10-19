package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.user.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Integer> {
    @Query("SELECT V FROM Validation V WHERE V.code = :code AND V.user.email = :email")
    Optional<Validation> findUserValidationCode(String email, String code);

    @Modifying
    @Query("UPDATE Validation V SET V.enabled = false WHERE V.user.email = :username")
    void disableValidationCodesByUser(String username);

    void deleteAllByEnabledOrExpiredAtBefore(Boolean enabled, Instant instant);
}

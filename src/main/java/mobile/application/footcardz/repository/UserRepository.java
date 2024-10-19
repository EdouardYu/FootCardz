package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsernameAndEnabled(String username, Boolean enabled);

    Optional<User> findByEmail(String email);

    @Query("SELECT U FROM User U WHERE U.username = :username OR U.email = :username")
    Optional<User> findByUsernameOrEmail(String username);
}


package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.user.UserPlayerCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

public interface UserPlayerCollectionRepository extends JpaRepository<UserPlayerCollection, Integer> {
    boolean existsByUserIdAndAcquiredAtBetween(Integer userId, Instant startOfInterval, Instant endOfInterval);

    @Query(value = "SELECT UPC FROM UserPlayerCollection UPC WHERE UPC.user.id = :userId")
    Page<UserPlayerCollection> findAllByUser(Integer userId, Pageable pageable);

}

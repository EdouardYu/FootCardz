package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(P) > 0 THEN true ELSE false END " +
        "FROM Player P WHERE P.nationality.id = :id")
    boolean existsPlayerByNationalityId(Integer id);
}

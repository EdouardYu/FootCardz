package mobile.application.footcardz.repository;

import mobile.application.footcardz.entity.player.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
}

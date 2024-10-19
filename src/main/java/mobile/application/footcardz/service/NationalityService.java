package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import mobile.application.footcardz.entity.player.Nationality;
import mobile.application.footcardz.repository.NationalityRepository;
import mobile.application.footcardz.service.exception.NationalityException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
@Service
public class NationalityService {
    private final NationalityRepository nationalityRepository;

    public Nationality findById(Integer id) {
        return this.nationalityRepository.findById(id)
            .orElseThrow(() -> new NationalityException("Nationality not found"));
    }
}

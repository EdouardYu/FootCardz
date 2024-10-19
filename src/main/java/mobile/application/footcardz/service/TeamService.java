package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import mobile.application.footcardz.entity.player.Team;
import mobile.application.footcardz.repository.TeamRepository;
import mobile.application.footcardz.service.exception.TeamException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public Team findById(Integer id) {
        return this.teamRepository.findById(id)
            .orElseThrow(() -> new TeamException("Team not found"));
    }
}

package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.mapper.TeamMapper;
import mobile.application.footcardz.dto.team.TeamDTO;
import mobile.application.footcardz.dto.team.TeamRequestDTO;
import mobile.application.footcardz.entity.player.League;
import mobile.application.footcardz.entity.player.Team;
import mobile.application.footcardz.repository.TeamRepository;
import mobile.application.footcardz.service.exception.TeamException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@AllArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueService leagueService;
    private final StorageService storageService;
    private final String FOLDER = "teams";
    private final String FILE_FORMAT = ".png";
    private final int EXPECTED_WIDTH = 256;
    private final int EXPECTED_HEIGHT = 256;

    public TeamDTO addTeam(TeamRequestDTO teamDTO, MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");

        League league = this.leagueService.findById(teamDTO.getLeagueId());

        Team team = Team.builder()
            .name(teamDTO.getName())
            .league(league)
            .build();

        team = this.teamRepository.save(team);

        String fileName = team.getId() + this.FILE_FORMAT;
        this.storageService.storeImageWithSizeCheck(file,
            this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);

        return TeamMapper.teamDTO(team);
    }

    public TeamDTO getTeam(Integer id) {
        Team team = this.findById(id);
        return TeamMapper.teamDTO(team);
    }

    public Page<TeamDTO> getAllTeams(Pageable pageable) {
        return this.teamRepository.findAll(pageable)
            .map(TeamMapper::teamDTO);
    }

    public TeamDTO modifyTeam(Integer id, TeamRequestDTO teamDTO, MultipartFile file) {
        Team team = this.findById(id);

        League league = this.leagueService.findById(teamDTO.getLeagueId());

        team.setName(teamDTO.getName());
        team.setLeague(league);

        team = this.teamRepository.save(team);

        if (file != null && !file.isEmpty()) {
            String fileName = team.getId() + this.FILE_FORMAT;
            this.storageService.storeImageWithSizeCheck(file,
                this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);
        }

        return TeamMapper.teamDTO(team);
    }

    public void deleteTeam(Integer id) {
        Team team = this.findById(id);

        String fileName = team.getId() + this.FILE_FORMAT;
        this.storageService.deleteImage(FOLDER, fileName);

        this.teamRepository.delete(team);
    }

    public Team findById(Integer id) {
        return this.teamRepository.findById(id)
            .orElseThrow(() -> new TeamException("Team not found"));
    }
}

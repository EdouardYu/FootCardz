package mobile.application.footcardz.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.league.LeagueDTO;
import mobile.application.footcardz.dto.league.LeagueRequestDTO;
import mobile.application.footcardz.dto.mapper.LeagueMapper;
import mobile.application.footcardz.entity.player.League;
import mobile.application.footcardz.repository.LeagueRepository;
import mobile.application.footcardz.service.exception.AlreadyUsedException;
import mobile.application.footcardz.service.exception.LeagueException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Transactional
@AllArgsConstructor
@Service
public class LeagueService {
    private final LeagueRepository leagueRepository;
    private final StorageService storageService;
    private final String FOLDER = "leagues";
    private final String FILE_FORMAT = ".png";
    private final int EXPECTED_WIDTH = 170;
    private final int EXPECTED_HEIGHT = 170;
    public LeagueDTO addLeague(@Valid LeagueRequestDTO leagueDTO, MultipartFile file) {
        if(this.leagueRepository.existsByName(leagueDTO.getName()))
            throw new AlreadyUsedException("Name already used");

        if (file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");

        League league = League.builder()
            .name(leagueDTO.getName())
            .build();

        league = this.leagueRepository.save(league);

        String fileName = league.getId() + this.FILE_FORMAT;
        this.storageService.storeImageWithSizeCheck(file,
            this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);

        return LeagueMapper.toLeagueDTO(league);
    }

    public LeagueDTO getLeague(Integer id) {
        League league = this.findById(id);
        return LeagueMapper.toLeagueDTO(league);
    }

    public Page<LeagueDTO> getAllLeagues(Pageable pageable) {
        return this.leagueRepository.findAll(pageable)
            .map(LeagueMapper::toLeagueDTO);
    }

    public LeagueDTO modifyLeague(Integer id, @Valid LeagueRequestDTO leagueDTO, MultipartFile file) {
        League league = this.findById(id);
        String newName = leagueDTO.getName();

        if(!league.getName().equals(newName) && this.leagueRepository.existsByName(newName))
            throw new AlreadyUsedException("Name already used");

        league.setName(newName);
        league = this.leagueRepository.save(league);

        if (file != null && !file.isEmpty()) {
            String fileName = league.getId() + this.FILE_FORMAT;
            this.storageService.storeImageWithSizeCheck(file,
                this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);
        }

        return LeagueMapper.toLeagueDTO(league);
    }

    public void deleteLeague(Integer id) {
        if(this.leagueRepository.existsTeamByLeagueId(id))
            throw new LeagueException("Cannot delete league. Some teams are associated with this league");

        League league = this.findById(id);

        String fileName = league.getId() + this.FILE_FORMAT;
        this.storageService.deleteImage(FOLDER, fileName);

        this.leagueRepository.delete(league);
    }

    public League findById(Integer id) {
        return this.leagueRepository.findById(id)
            .orElseThrow(() -> new LeagueException("League not found"));
    }
}

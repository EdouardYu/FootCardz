package mobile.application.footcardz.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.mapper.PlayerMapper;
import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.dto.player.PlayerDetailsDTO;
import mobile.application.footcardz.dto.player.PlayerRequestDTO;
import mobile.application.footcardz.entity.player.Nationality;
import mobile.application.footcardz.entity.player.Player;
import mobile.application.footcardz.entity.player.Team;
import mobile.application.footcardz.entity.user.User;
import mobile.application.footcardz.repository.PlayerRepository;
import mobile.application.footcardz.service.exception.DailyPlayerAlreadyAssignedException;
import mobile.application.footcardz.service.exception.PlayerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Transactional
@AllArgsConstructor
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final UserService userService;
    private final UserPlayerCollectionService userPlayerCollectionService;
    private final TeamService teamService;
    private final NationalityService nationalityService;
    private final StorageService storageService;
    private final String FOLDER = "players";
    private final String FILE_FORMAT = ".png";
    private final int EXPECTED_WIDTH = 512;
    private final int EXPECTED_HEIGHT = 512;


    public Player getRandomPlayerNotOwnedByUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userService.findById(user.getId());
        Integer userId = dbUser.getId();

        Instant noon = ZonedDateTime.now(ZoneId.of("UTC"))
            .withHour(12)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .toInstant();

        Instant startOfInterval;
        Instant endOfInterval;
        Instant now = Instant.now();

        if (now.isBefore(noon)) {
            startOfInterval = noon.minus(1, ChronoUnit.DAYS);
            endOfInterval = noon;
        } else {
            startOfInterval = noon;
            endOfInterval = noon.plus(1, ChronoUnit.DAYS);
        }

        if (userPlayerCollectionService.existsByUserAndAcquiredAtBetween(userId, startOfInterval, endOfInterval)) {
            throw new DailyPlayerAlreadyAssignedException("Player already assigned for today");
        }

        Player player =  this.playerRepository.findRandomPlayerNotOwnedByUser(userId)
            .orElseThrow(() -> new PlayerException("No available players to assign"));

        return this.userPlayerCollectionService.save(dbUser, player, Instant.now());
    }

    public Page<PlayerDTO> getAllPlayers(Pageable pageable) {
        return this.playerRepository.findAll(pageable)
            .map(PlayerMapper::toPlayerDTO);
    }

    public Page<PlayerDTO> searchPlayers(String term, Pageable pageable) {
        String trimmedTerm = term.trim();

        if (trimmedTerm.length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }

        return this.playerRepository.searchPlayers(trimmedTerm, pageable)
            .map(PlayerMapper::toPlayerDTO);
    }

    public PlayerDetailsDTO getPlayer(Integer id) {
        Player player = this.findById(id);
        return PlayerMapper.toPlayerDetailsDTO(player);
    }

    public PlayerDetailsDTO modifyPlayer(Integer id, PlayerRequestDTO playerDTO, MultipartFile file) {
        Player player = this.findById(id);

        Team team = this.teamService.findById(playerDTO.getTeamId());
        Nationality nationality = this.nationalityService.findById(playerDTO.getNationalityId());

        player.setName(playerDTO.getName());
        player.setPosition(playerDTO.getPosition());
        player.setTeam(team);
        player.setNationality(nationality);

        player = this.playerRepository.save(player);

        if (file != null && !file.isEmpty()) {
            String fileName = player.getId() + this.FILE_FORMAT;
            this.storageService.storeImageWithSizeCheck(file,
                this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);
        }

        return PlayerMapper.toPlayerDetailsDTO(player);
    }

    public PlayerDetailsDTO addPlayer(@Valid PlayerRequestDTO playerDTO, MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");

        Team team = this.teamService.findById(playerDTO.getTeamId());
        Nationality nationality = this.nationalityService.findById(playerDTO.getNationalityId());

        Player player = Player.builder()
            .name(playerDTO.getName())
            .position(playerDTO.getPosition())
            .team(team)
            .nationality(nationality)
            .build();

        player = this.playerRepository.save(player);

        String fileName = player.getId() + this.FILE_FORMAT;
        this.storageService.storeImageWithSizeCheck(file,
            this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);

        return PlayerMapper.toPlayerDetailsDTO(player);
    }

    public void deletePlayer(Integer id) {
        Player player = this.findById(id);

        String fileName = player.getId() + this.FILE_FORMAT;
        this.storageService.deleteImage(this.FOLDER, fileName);

        this.playerRepository.delete(player);
    }

    private Player findById(Integer id) {
        return this.playerRepository.findById(id)
            .orElseThrow(() -> new PlayerException("Player not found"));
    }
}

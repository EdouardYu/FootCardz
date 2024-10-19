package mobile.application.footcardz.service;

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

    public Player getRandomPlayerNotOwnedByUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = this.userService.loadUserByUsername(user.getUsername());
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
        return playerRepository.findAll(pageable)
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
        return PlayerMapper.toPlayerDetailsDTO(
            playerRepository.findById(id)
                .orElseThrow(() -> new PlayerException("Player not found"))
        );
    }

    public PlayerDetailsDTO modifyPlayer(Integer id, PlayerRequestDTO playerDTO, MultipartFile file) {
        Player player = playerRepository.findById(id)
            .orElseThrow(() -> new PlayerException("Player not found"));

        Team team = this.teamService.findById(playerDTO.getTeamId());
        Nationality nationality = this.nationalityService.findById(playerDTO.getNationalityId());

        player.setName(playerDTO.getName());
        player.setPosition(playerDTO.getPosition());
        player.setTeam(team);
        player.setNationality(nationality);

        if (file != null && !file.isEmpty()) {
            String fileName = player.getId() + ".png";
            this.storageService.storeImageWithSizeCheck(file, "players", fileName, 512, 512);
        }

        player = playerRepository.save(player);

        return PlayerMapper.toPlayerDetailsDTO(player);
    }
}

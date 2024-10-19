package mobile.application.footcardz.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.dto.player.PlayerDetailsDTO;
import mobile.application.footcardz.dto.player.PlayerRequestDTO;
import mobile.application.footcardz.entity.player.Player;
import mobile.application.footcardz.service.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("players")
public class PlayerController {
    private final PlayerService playerService;

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player getRandomPlayerNotOwnedByUser() {
        return this.playerService.getRandomPlayerNotOwnedByUser();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PlayerDTO> getAllPlayers(Pageable pageable) {
        return this.playerService.getAllPlayers(pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PlayerDTO> searchPlayers(String term, Pageable pageable) {
        return this.playerService.searchPlayers(term, pageable);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDetailsDTO getPlayer(@PathVariable Integer id) {
        return this.playerService.getPlayer(id);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDetailsDTO modifyPlayer(@PathVariable Integer id, @Valid @RequestPart("player") PlayerRequestDTO playerDTO,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        return this.playerService.modifyPlayer(id, playerDTO, file);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public PlayerDetailsDTO addPlayer(
        @Valid @RequestPart("player") PlayerRequestDTO playerDTO,
        @RequestPart("file") MultipartFile file) {
       return this.playerService.addPlayer(playerDTO, file);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("{id}")
    public void deletePlayer(@PathVariable Integer id) {
        this.playerService.deletePlayer(id);
    }
}

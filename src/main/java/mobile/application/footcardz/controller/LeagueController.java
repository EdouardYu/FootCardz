package mobile.application.footcardz.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.league.LeagueDTO;
import mobile.application.footcardz.dto.league.LeagueRequestDTO;
import mobile.application.footcardz.service.LeagueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("leagues")
public class LeagueController {
    private final LeagueService leagueService;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LeagueDTO addLeague(@Valid @RequestPart("league") LeagueRequestDTO leagueDTO,
                               @RequestPart(value = "file") MultipartFile file) {
         return this.leagueService.addLeague(leagueDTO, file);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LeagueDTO getLeague(@PathVariable Integer id) {
        return this.leagueService.getLeague(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<LeagueDTO> getAllLeagues(Pageable pageable) {
        return this.leagueService.getAllLeagues(pageable);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LeagueDTO modifyLeague(@PathVariable Integer id, @Valid @RequestPart("league") LeagueRequestDTO leagueDTO,
                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        return this.leagueService.modifyLeague(id, leagueDTO, file);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("{id}")
    public void deleteLeague(@PathVariable Integer id) {
        this.leagueService.deleteLeague(id);
    }
}

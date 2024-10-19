package mobile.application.footcardz.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.team.TeamDTO;
import mobile.application.footcardz.dto.team.TeamRequestDTO;
import mobile.application.footcardz.service.TeamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("teams")
public class TeamController {
    private final TeamService teamService;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TeamDTO addTeam(@Valid @RequestPart("team") TeamRequestDTO teamDTO,
                             @RequestPart(value = "file") MultipartFile file) {
        return this.teamService.addTeam(teamDTO, file);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TeamDTO getTeam(@PathVariable Integer id) {
        return this.teamService.getTeam(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TeamDTO> getAllTeams(Pageable pageable) {
        return this.teamService.getAllTeams(pageable);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TeamDTO modifyTeam(@PathVariable Integer id, @Valid @RequestPart("team") TeamRequestDTO teamDTO,
                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        return this.teamService.modifyTeam(id, teamDTO, file);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("{id}")
    public void deleteLeague(@PathVariable Integer id) {
        this.teamService.deleteTeam(id);
    }
}

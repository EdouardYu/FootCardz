package mobile.application.footcardz.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.nation.NationalityDTO;
import mobile.application.footcardz.dto.nation.NationalityRequestDTO;
import mobile.application.footcardz.service.NationalityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("nationalities")
public class NationalityController {
    private final NationalityService nationalityService;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public NationalityDTO addNationality(@Valid @RequestPart("nationality") NationalityRequestDTO nationalityDTO,
                                         @RequestPart(value = "file") MultipartFile file) {
        return this.nationalityService.addNationality(nationalityDTO, file);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NationalityDTO getNationality(@PathVariable Integer id) {
        return this.nationalityService.getNationality(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<NationalityDTO> getAllNationalities(Pageable pageable) {
        return this.nationalityService.getAllNationalities(pageable);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public NationalityDTO modifyNationality(@PathVariable Integer id, @Valid @RequestPart("nationality") NationalityRequestDTO nationalityDTO,
                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        return this.nationalityService.modifyNationality(id, nationalityDTO, file);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("{id}")
    public void deleteNationality(@PathVariable Integer id) {
        this.nationalityService.deleteNationality(id);
    }
}

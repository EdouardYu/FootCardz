package mobile.application.footcardz.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.mapper.NationalityMapper;
import mobile.application.footcardz.dto.nation.NationalityDTO;
import mobile.application.footcardz.dto.nation.NationalityRequestDTO;
import mobile.application.footcardz.entity.player.Nationality;
import mobile.application.footcardz.repository.NationalityRepository;
import mobile.application.footcardz.service.exception.AlreadyUsedException;
import mobile.application.footcardz.service.exception.NationalityException;
import mobile.application.footcardz.service.exception.TeamException;
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
public class NationalityService {
    private final NationalityRepository nationalityRepository;
    private final StorageService storageService;
    private final String FOLDER = "nations";
    private final String FILE_FORMAT = ".png";
    private final int EXPECTED_WIDTH = 104;
    private final int EXPECTED_HEIGHT = 62;

    public NationalityDTO addNationality(@Valid NationalityRequestDTO nationalityDTO, MultipartFile file) {
        if(this.nationalityRepository.existsByName(nationalityDTO.getName()))
            throw new AlreadyUsedException("Name already used");

        if (file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");

        Nationality nationality = Nationality.builder()
            .name(nationalityDTO.getName())
            .build();

        nationality = this.nationalityRepository.save(nationality);

        String fileName = nationality.getId() + this.FILE_FORMAT;
        this.storageService.storeImageWithSizeCheck(file,
            this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);

        return NationalityMapper.toNationalityDTO(nationality);
    }

    public NationalityDTO getNationality(Integer id) {
        Nationality nationality = this.findById(id);
        return NationalityMapper.toNationalityDTO(nationality);
    }

    public Page<NationalityDTO> getAllNationalities(Pageable pageable) {
        return this.nationalityRepository.findAll(pageable)
            .map(NationalityMapper::toNationalityDTO);
    }

    public NationalityDTO modifyNationality(Integer id, @Valid NationalityRequestDTO nationalityDTO, MultipartFile file) {
        Nationality nationality = this.findById(id);
        String newName = nationalityDTO.getName();

        if(!nationality.getName().equals(newName) && this.nationalityRepository.existsByName(newName))
            throw new AlreadyUsedException("Name already used");

        nationality.setName(newName);
        nationality = this.nationalityRepository.save(nationality);

        if (file != null && !file.isEmpty()) {
            String fileName = nationality.getId() + this.FILE_FORMAT;
            this.storageService.storeImageWithSizeCheck(file,
                this.FOLDER, fileName, this.EXPECTED_WIDTH, this.EXPECTED_HEIGHT);
        }

        return NationalityMapper.toNationalityDTO(nationality);
    }

    public void deleteNationality(Integer id) {
        if(this.nationalityRepository.existsPlayerByNationalityId(id))
            throw new TeamException("Cannot delete nationality. Some players are associated with this nationality");

        Nationality nationality = this.findById(id);

        String fileName = nationality.getId() + this.FILE_FORMAT;
        this.storageService.deleteImage(FOLDER, fileName);

        this.nationalityRepository.delete(nationality);
    }

    public Nationality findById(Integer id) {
        return this.nationalityRepository.findById(id)
            .orElseThrow(() -> new NationalityException("Nationality not found"));
    }
}

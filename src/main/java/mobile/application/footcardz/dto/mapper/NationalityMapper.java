package mobile.application.footcardz.dto.mapper;

import mobile.application.footcardz.dto.nation.NationalityDTO;
import mobile.application.footcardz.entity.player.Nationality;

public class NationalityMapper {
    public static NationalityDTO toNationalityDTO(Nationality nationality) {
        return NationalityDTO.builder()
            .name(nationality.getName())
            .imageUrl("/api/images/nations/" + nationality.getId() + ".png")
            .build();
    }
}

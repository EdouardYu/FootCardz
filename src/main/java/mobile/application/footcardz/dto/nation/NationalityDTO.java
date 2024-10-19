package mobile.application.footcardz.dto.nation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NationalityDTO {
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
}

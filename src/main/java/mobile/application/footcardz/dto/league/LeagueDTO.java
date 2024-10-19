package mobile.application.footcardz.dto.league;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueDTO {
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
}

package mobile.application.footcardz.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDTO {
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("league_image_url")
    private String leagueImageUrl;
}

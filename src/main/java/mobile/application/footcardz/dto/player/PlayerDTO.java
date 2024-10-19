package mobile.application.footcardz.dto.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import mobile.application.footcardz.entity.enumeration.Position;

@Data
@Builder
public class PlayerDTO {
    private String name;
    private Position position;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("league_image_url")
    private String leagueImageUrl;
    @JsonProperty("team_image_url")
    private String teamImageUrl;
    @JsonProperty("nationality_image_url")
    private String nationalityImageUrl;
}

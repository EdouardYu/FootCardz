package mobile.application.footcardz.dto.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import mobile.application.footcardz.entity.enumeration.Position;

@Data
@Builder
public class PlayerDetailsDTO {
    private String name;
    private Position position;
    @JsonProperty("image_url")
    private String imageUrl;
    private String league;
    @JsonProperty("league_image_url")
    private String leagueImageUrl;
    private String team;
    @JsonProperty("team_image_url")
    private String teamImageUrl;
    private String nationality;
    @JsonProperty("nationality_image_url")
    private String nationalityImageUrl;
}

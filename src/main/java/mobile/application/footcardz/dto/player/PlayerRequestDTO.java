package mobile.application.footcardz.dto.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import mobile.application.footcardz.entity.enumeration.Position;

@Data
public class PlayerRequestDTO {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z .-]+$", message = "Name can only contain letters, spaces, periods, and hyphens")
    private String name;
    @NotNull(message = "Position cannot be null")
    private Position position;
    @NotNull(message = "Team ID cannot be null")
    @JsonProperty("team_id")
    private Integer teamId;
    @NotNull(message = "Nationality ID cannot be null")
    @JsonProperty("nationality_id")
    private Integer nationalityId;
}

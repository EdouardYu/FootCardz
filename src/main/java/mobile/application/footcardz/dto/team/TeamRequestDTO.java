package mobile.application.footcardz.dto.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamRequestDTO {
    @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z .-]+$", message = "Name can only contain letters, spaces, periods, and hyphens")
    private String name;
    private Integer leagueId;
}

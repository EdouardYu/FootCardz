package mobile.application.footcardz.dto.nation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NationalityRequestDTO {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z .-]+$", message = "Name can only contain letters, spaces, periods, and hyphens")
    private String name;
}

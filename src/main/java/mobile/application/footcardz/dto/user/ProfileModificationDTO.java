package mobile.application.footcardz.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileModificationDTO {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric with underscores allowed")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @JsonCreator
    public ProfileModificationDTO(
        String username,
        String email
    ) {
        this.username = username;
        this.email = email == null ? null : email.toLowerCase();
    }
}

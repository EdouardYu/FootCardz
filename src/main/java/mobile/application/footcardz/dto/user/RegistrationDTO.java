package mobile.application.footcardz.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegistrationDTO {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric with underscores allowed")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&*+<=>?@^_-]).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, " +
            "one number, and one special character (! # $ % & * + - < = > ? @ ^ _)"
    )
    private String password;

    @JsonCreator
    public RegistrationDTO(String username, String email, String password) {
        this.username = username;
        this.email = email == null ? null : email.toLowerCase();
        this.password = password;
    }
}

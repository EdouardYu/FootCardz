package mobile.application.footcardz.dto.user;

import lombok.Builder;
import lombok.Data;
import mobile.application.footcardz.entity.enumeration.Role;

@Data
@Builder
public class ProfileDTO {
    private String username;
    private String email;
    private Role role;
}

package mobile.application.footcardz.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensDTO {
    private String bearer;
    private String refresh;
}

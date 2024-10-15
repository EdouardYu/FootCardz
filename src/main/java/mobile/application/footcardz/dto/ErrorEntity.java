package mobile.application.footcardz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorEntity {
    private int status;
    private String message;
}


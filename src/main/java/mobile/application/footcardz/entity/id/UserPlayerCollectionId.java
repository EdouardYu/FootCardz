package mobile.application.footcardz.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.application.footcardz.entity.player.Player;
import mobile.application.footcardz.entity.user.User;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPlayerCollectionId implements Serializable {
    private User user;
    private Player player;
}

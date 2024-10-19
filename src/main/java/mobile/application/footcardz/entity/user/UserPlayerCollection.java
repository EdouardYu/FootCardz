package mobile.application.footcardz.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.application.footcardz.entity.id.UserPlayerCollectionId;
import mobile.application.footcardz.entity.player.Player;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_player_collection")
@IdClass(UserPlayerCollectionId.class)
public class UserPlayerCollection {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "acquired_at")
    private Instant acquiredAt;
}

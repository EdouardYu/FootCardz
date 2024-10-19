package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import mobile.application.footcardz.dto.mapper.PlayerMapper;
import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.entity.player.Player;
import mobile.application.footcardz.entity.user.User;
import mobile.application.footcardz.entity.user.UserPlayerCollection;
import mobile.application.footcardz.repository.UserPlayerCollectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional
@AllArgsConstructor
@Service
public class UserPlayerCollectionService {
    private final UserPlayerCollectionRepository userPlayerCollectionRepository;

    public boolean existsByUserAndAcquiredAtBetween(Integer userId, Instant startOfInterval, Instant endOInterval) {
        return this.userPlayerCollectionRepository.existsByUserIdAndAcquiredAtBetween(userId, startOfInterval, endOInterval);
    }

    public Player save(User user, Player player, Instant acquiredAt) {
        UserPlayerCollection userPlayerCollection = UserPlayerCollection.builder()
            .user(user)
            .player(player)
            .acquiredAt(acquiredAt)
            .build();

        userPlayerCollection = this.userPlayerCollectionRepository.save(userPlayerCollection);

        return userPlayerCollection.getPlayer();
    }

    public Page<PlayerDTO> findAllByUser(Integer userId, Pageable pageable) {
        return this.userPlayerCollectionRepository.findAllByUser(userId, pageable)
            .map(userPlayer -> PlayerMapper.toPlayerDTO(userPlayer.getPlayer()));
    }
}

package mobile.application.footcardz.dto.mapper;

import mobile.application.footcardz.dto.league.LeagueDTO;
import mobile.application.footcardz.entity.player.League;

public class LeagueMapper {
    public static LeagueDTO toLeagueDTO(League league) {
        return LeagueDTO.builder()
            .name(league.getName())
            .imageUrl("/api/images/leagues/" + league.getId() + ".png")
            .build();
    }
}

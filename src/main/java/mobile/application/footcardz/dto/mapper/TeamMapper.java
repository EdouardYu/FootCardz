package mobile.application.footcardz.dto.mapper;

import mobile.application.footcardz.dto.team.TeamDTO;
import mobile.application.footcardz.entity.player.Team;

public class TeamMapper {
    public static TeamDTO teamDTO(Team team) {
        return TeamDTO.builder()
            .name(team.getName())
            .imageUrl(getImagesUrl("teams", team.getId()))
            .leagueImageUrl(getImagesUrl("leagues", team.getLeague().getId()))
            .build();
    }

    private static String getImagesUrl(String folder, Integer id) {
        return "/api/images/" + folder + "/" + id + ".png";
    }
}

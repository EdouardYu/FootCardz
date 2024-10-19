package mobile.application.footcardz.dto.mapper;

import mobile.application.footcardz.dto.player.PlayerDTO;
import mobile.application.footcardz.dto.player.PlayerDetailsDTO;
import mobile.application.footcardz.entity.player.Player;
import mobile.application.footcardz.entity.player.Team;

public class PlayerMapper {
    public static PlayerDTO toPlayerDTO(Player player) {
        Team team = player.getTeam();
        return PlayerDTO.builder()
            .name(player.getName())
            .position(player.getPosition())
            .playerImageUrl(getImagesUrl("players", player.getId()))
            .leagueImageUrl(getImagesUrl("leagues", team.getLeague().getId()))
            .teamImageUrl(getImagesUrl("teams", team.getId()))
            .nationalityImageUrl(getImagesUrl("nations", player.getNationality().getId()))
            .build();
    }

    public static PlayerDetailsDTO toPlayerDetailsDTO(Player player) {
        Team team = player.getTeam();
        return PlayerDetailsDTO.builder()
            .name(player.getName())
            .position(player.getPosition())
            .playerImageUrl(getImagesUrl("players", player.getId()))
            .league(team.getLeague().getName())
            .leagueImageUrl(getImagesUrl("leagues", team.getLeague().getId()))
            .team(team.getName())
            .teamImageUrl(getImagesUrl("teams", team.getId()))
            .nationality(player.getNationality().getName())
            .nationalityImageUrl(getImagesUrl("nations", player.getNationality().getId()))
            .build();
    }

    private static String getImagesUrl(String folder, Integer id) {
        return "/api/images/" + folder + "/" + id + ".png";
    }
}

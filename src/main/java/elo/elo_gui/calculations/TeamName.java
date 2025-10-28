package elo.elo_gui.calculations;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TeamName {
    ALPHA(1, "\uD83D\uDFE5"),
    BRAVO(2, "\uD83D\uDFE7"),
    CHARLIE(3, "\uD83D\uDFE8"),
    DELTA(4, "\uD83D\uDFE9"),
    ECHO(5, "\uD83D\uDFE6"),
    FOXTROT(6, "\uD83D\uDFEA"),
    GAMMA(7, "\uD83D\uDFEB"),
    ZULU(8, "⬛"),
    OMEGA(9, "⬜"),
    SIGMA(10, "\uD83D\uDD34"),
    NOVA(11, "\uD83D\uDFE2"),
    TITAN(12, "\uD83D\uDFE1"),
    UNKNOWN(99, "\uD83D\uDD35");

    private final int number;
    private final String icon;

    TeamName(int number, String icon) {
        this.number = number;
        this.icon = icon;
    }

    public static TeamName getByNumber(int num) {
        return Arrays.stream(TeamName.values()).filter(team -> team.number == num).findFirst().orElse(UNKNOWN);
    }
}

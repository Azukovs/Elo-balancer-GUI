package elo.elo_gui.calculations;

import java.util.Arrays;

public enum TeamName {
    ALPHA(1),
    BRAVO(2),
    CHARLIE(3),
    DELTA(4),
    ECHO(5),
    FOXTROT(6),
    GAMMA(7),
    ZULU(8),
    OMEGA(9),
    SIGMA(10),
    NOVA(11),
    TITAN(12),
    UNKNOWN(99);

    private final int number;

    TeamName(int number) {
        this.number = number;
    }

    public static TeamName getByNumber(int num) {
        return Arrays.stream(TeamName.values()).filter(team -> team.number == num).findFirst().orElse(UNKNOWN);
    }
}

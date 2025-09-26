package elo.elo_gui.calculations.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerInputData {
    public static int counterTotal = 0;

    private String discordName;
    private String currentFaceit;
    private final int number;

    public PlayerInputData(String discordName, String currentFaceit) {
        this.currentFaceit = currentFaceit;
        this.discordName = discordName;
        counterTotal++;
        this.number = counterTotal;
    }

    @Override
    public String toString() {
        return discordName + "(" + currentFaceit + ") ";
    }
}

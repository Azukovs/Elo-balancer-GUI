package elo.elo_gui.calculations.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Player implements Comparable<Player> {
    private final String discordName;
    private final String steamName;
    private final int currentPremiere;
    private final int maxPremiere;
    private final int currentFaceit;
    private final int maxFaceit;

    @Override
    public int compareTo(Player secondPlayer) {
        if (this.currentFaceit == secondPlayer.currentFaceit) {
            return Integer.compare(this.currentPremiere, secondPlayer.currentPremiere);
        }
        return Integer.compare(this.currentFaceit, secondPlayer.currentFaceit);
    }

    @Override
    public String toString() {
        return discordName + "(" + currentFaceit + ") ";
    }
}
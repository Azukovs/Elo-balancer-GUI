package elo.elo_gui.calculations.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Player implements Comparable<Player> {
    private final String discordName;
    private final String steamName;
    private final int currentPremiere;
    private final int maxPremiere;
    private final int currentFaceit;
    private final int maxFaceit;
    private List<Player> teammates;
    private List<Player> adversaries;
    private boolean hasTeammates;

    @Override
    public int compareTo(Player secondPlayer) {
        if (this.currentFaceit == secondPlayer.currentFaceit) {
            return Integer.compare(this.currentPremiere, secondPlayer.currentPremiere);
        }
        return Integer.compare(this.currentFaceit, secondPlayer.currentFaceit);
    }

    @Override
    public String toString() {
        return discordName + " (" + currentFaceit + ")";
    }

    public void addTeammate(Player teammate) {
        if (teammates == null) {
            teammates = new ArrayList<>();
        }
        teammates.add(teammate);
        hasTeammates = true;
    }

    public void addAdversary(Player adversary) {
        if (adversaries == null) {
            adversaries = new ArrayList<>();
        }
        adversaries.add(adversary);
    }

    public List<Player> getAdversaries() {
        if (adversaries == null) {
            adversaries = new ArrayList<>();
        }
        return adversaries;
    }

    public List<Player> getTeammates() {
        if (teammates == null) {
            teammates = new ArrayList<>();
        }
        return teammates;
    }
}
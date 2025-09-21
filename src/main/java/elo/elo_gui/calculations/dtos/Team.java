package elo.elo_gui.calculations.dtos;

import java.util.ArrayList;
import java.util.List;

public class Team implements Comparable<Team> {
    public List<Player> players;
    public int faceitEloSum;
    public int premiereEloSum;

    public Team() {
        players = new ArrayList<>();
        faceitEloSum = 0;
        premiereEloSum = 0;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        this.faceitEloSum += player.getCurrentFaceit();
        this.premiereEloSum += player.getCurrentPremiere();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        this.faceitEloSum -= player.getCurrentFaceit();
        this.premiereEloSum -= player.getCurrentPremiere();
    }

    public int totalFaceit() {
        return faceitEloSum;
    }

    @Override
    public int compareTo(Team o) {
        //Compares by max faceit first. If equal = then max premiere.
        if (this.faceitEloSum == o.faceitEloSum) {
            return Integer.compare(this.premiereEloSum, o.premiereEloSum);
        }
        return Integer.compare(this.faceitEloSum, o.faceitEloSum);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(faceitEloSum / 5).append(") ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            stringBuilder.append(p.toString());
            if (i != players.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}

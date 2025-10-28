package elo.elo_gui.calculations.dtos;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static elo.elo_gui.calculations.TeamName.getByNumber;

@Getter
public class Team implements Comparable<Team> {
    public static int teamCounter = 1;
    public String teamName;
    public String teamIcon;
    public List<Player> players;
    public int faceitEloSum;
    public int premiereEloSum;
    public int averageFaceitElo;

    public Player player1;
    public Player player2;
    public Player player3;
    public Player player4;
    public Player player5;

    public Team() {
        players = new ArrayList<>();
        faceitEloSum = 0;
        premiereEloSum = 0;
        averageFaceitElo = 0;
        teamName = getByNumber(teamCounter).name();
        teamIcon = getByNumber(teamCounter).getIcon();
        teamCounter++;
    }

    public void addPlayer(Player player) {
        if (player1 == null) {
            player1 = player;
        } else if (player2 == null) {
            player2 = player;
        } else if (player3 == null) {
            player3 = player;
        } else if (player4 == null) {
            player4 = player;
        } else if (player5 == null) {
            player5 = player;
        } else {
            throw new IllegalStateException("Trying to add 6th player");
        }
        this.players.add(player);
        this.faceitEloSum += player.getCurrentFaceit();
        this.premiereEloSum += player.getCurrentPremiere();
        this.averageFaceitElo = faceitEloSum / players.size();
    }

    public void removePlayer(Player player) {
        if (player.equals(player1)) {
            player1 = null;
        } else if (player.equals(player2)) {
            player2 = null;
        } else if (player.equals(player3)) {
            player3 = null;
        } else if (player.equals(player4)) {
            player4 = null;
        } else if (player.equals(player5)) {
            player5 = null;
        } else {
            throw new IllegalStateException("Trying to remove non-existent player");
        }
        this.players.remove(player);
        this.faceitEloSum -= player.getCurrentFaceit();
        this.premiereEloSum -= player.getCurrentPremiere();
        this.averageFaceitElo = faceitEloSum / players.size();
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
        stringBuilder.append(teamIcon).append("TEAM ").append(teamName).append(" (").append(averageFaceitElo).append(")");
        for (Player player : players) {
            stringBuilder.append("\n@").append(player.toString());
        }
        return stringBuilder.toString();
    }
}

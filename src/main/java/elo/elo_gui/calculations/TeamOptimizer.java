package elo.elo_gui.calculations;

import elo.elo_gui.calculations.dtos.Player;
import elo.elo_gui.calculations.dtos.Team;

import java.util.*;

import static java.util.Comparator.comparingInt;

public class TeamOptimizer {
    static int TEAM_SIZE = 5;

    public static List<Team> calculateSingle(List<Player> players, int iterations) {
        List<Team> initialTeams = greedyInitialize(players, TEAM_SIZE);
        List<Team> optimized = optimize(initialTeams, iterations);
        optimized.sort(comparingInt(Team::totalFaceit));
        return optimized;
    }

    // Greedy initialization: assign highest rated players to lowest scoring team
    static List<Team> greedyInitialize(List<Player> players, int teamSize) {
        int teamNumber = players.size() / teamSize;
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < teamNumber; i++) {
            teams.add(new Team());
        }

        players.sort(comparingInt((Player player) -> -player.getCurrentFaceit())
                .thenComparingInt(player -> -player.getCurrentPremiere()));

        for (Player player : players) {
            Team bestTeam = teams.stream()
                    .filter(team -> team.players.size() < teamSize)
                    .min(comparingInt(Team::totalFaceit))
                    .orElseThrow();
            bestTeam.addPlayer(player);
        }

        return teams;
    }

    // Compute max difference in Faceit elo totals across teams
    public static int scoreTeams(List<Team> teams) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Team t : teams) {
            int score = t.totalFaceit();
            min = Math.min(min, score);
            max = Math.max(max, score);
        }
        return max - min;
    }

    // Local search: random swaps to improve Faceit balance
    static List<Team> optimize(List<Team> initialTeams, int iterations) {
        Random rand = new Random();
        List<Team> best = deepCopyTeams(initialTeams);
        int bestScore = scoreTeams(best);

        for (int i = 0; i < iterations; i++) {
            List<Team> current = deepCopyTeams(best);
            Team team1 = current.get(rand.nextInt(current.size()));
            Team team2 = current.get(rand.nextInt(current.size()));
            while (team1 == team2) {
                team2 = current.get(rand.nextInt(current.size()));
            }

            Player player1 = team1.players.get(rand.nextInt(team1.players.size()));
            Player player2 = team2.players.get(rand.nextInt(team2.players.size()));

            team1.removePlayer(player1);
            team2.removePlayer(player2);
            team1.addPlayer(player2);
            team2.addPlayer(player1);

            int score = scoreTeams(current);
            if (score < bestScore) {
                best = current;
                bestScore = score;
            }
        }

        return best;
    }

    // Deep copy of teams to avoid mutation
    static List<Team> deepCopyTeams(List<Team> original) {
        List<Team> copy = new ArrayList<>();
        for (Team t : original) {
            Team newTeam = new Team();
            newTeam.players = new ArrayList<>(t.players);
            newTeam.faceitEloSum = t.faceitEloSum;
            newTeam.premiereEloSum = t.premiereEloSum;
            copy.add(newTeam);
        }
        return copy;
    }
}
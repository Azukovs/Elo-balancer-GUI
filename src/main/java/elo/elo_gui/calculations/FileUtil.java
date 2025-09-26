package elo.elo_gui.calculations;

import elo.elo_gui.calculations.dtos.Player;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import elo.elo_gui.calculations.dtos.Team;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static elo.elo_gui.calculations.TeamOptimizer.scoreTeams;

public class FileUtil {
    public static void loadPlayersInput(List<PlayerInputData> providedPlayers, List<PlayerInputData> providedReserve, FileInputStream input) {
        int dcNameIndex = 0;
        int currentFaceitEloIndex = 0;

        List<PlayerInputData> playerList = new ArrayList<>();

        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataFormatter formatter = new DataFormatter();

        Sheet sheet = workbook.getSheetAt(0);
        int index = 0;
        for (Cell headerCell : sheet.getRow(0)) {
            switch (headerCell.getStringCellValue()) {
                case "Discord Nickname":
                    dcNameIndex = index;
                    break;
                case "Faceit Elo":
                    currentFaceitEloIndex = index;
                    break;
            }
            index++;
        }

        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex == 0) {    //skipping headers
                rowIndex++;
                continue;
            }
            if (row.getCell(0) == null) {   //skipping metadata rows
                rowIndex++;
                continue;
            }

            String nameInSheet = formatter.formatCellValue(row.getCell(dcNameIndex));
            String eloInSheet = formatter.formatCellValue(row.getCell(currentFaceitEloIndex));

            PlayerInputData inputPlayer = new PlayerInputData(nameInSheet, eloInSheet);

            playerList.add(inputPlayer);
            rowIndex++;
        }

        List<PlayerInputData> reservePlayers = new ArrayList<>();
        for (int i = 0; i <= playerList.size() % 5; i++) {
            PlayerInputData reservePlayer = playerList.getLast();
            reservePlayers.add(reservePlayer);
            playerList.remove(reservePlayer);
        }

        providedPlayers.addAll(playerList);
        providedReserve.addAll(reservePlayers);
    }

    public static String outputTeams(List<List<Team>> potentialTeams, List<Player> reserve, int outputNumber) {
        StringBuilder outputData = new StringBuilder();

        try (FileWriter outputWriter = new FileWriter("teams.txt")) {
            File output = new File("teams.txt");
            for (int i = 0; i < outputNumber; i++) {
                List<Team> option = potentialTeams.get(i);
                int faceitDiff = scoreTeams(option);
                outputWriter.write("\nOptimized Teams (Faceit diff = " + faceitDiff + "):\n");
                outputData.append("\nOptimized Teams (Faceit diff = ").append(faceitDiff).append("):\n");
                int num = 1;
                for (Team team : option) {
                    outputWriter.write("Team " + num + " - " + team.toString() + "\n");
                    outputData.append("Team ").append(num).append(" - ").append(team).append("\n");
                    num++;
                }
            }

            outputWriter.write("\nReserve players:\n");
            outputData.append("\nReserve players:\n");
            for (Player player : reserve) {
                outputWriter.write("* " + player.toString() + "\n");
                outputData.append("* ").append(player).append("\n");
            }
            System.out.println("Backup saved:" + output.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputData.toString();
    }
}
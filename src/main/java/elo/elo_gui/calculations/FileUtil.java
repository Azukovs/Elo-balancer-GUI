package elo.elo_gui.calculations;

import elo.elo_gui.EloApplication;
import elo.elo_gui.calculations.dtos.Player;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import elo.elo_gui.calculations.dtos.Team;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static elo.elo_gui.calculations.TeamOptimizer.scoreTeams;

public class FileUtil {
    public static Map<String, Integer> headerMap = new HashMap<>();
    public static String nameColumnHeader = "Discord Nickname";
    public static String eloColumnHeader = "Faceit Elo  (Pilnus ciparus bez komentāriem un komatiem)";

    public static List<String> getHeaderNames(FileInputStream input) {
        List<String> headers = new ArrayList<>();
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet = workbook.getSheetAt(0);
        int index = 0;
        for (Cell headerCell : sheet.getRow(0)) {
            headers.add(headerCell.getStringCellValue());
            headerMap.put(headerCell.getStringCellValue(), index);
            index++;
        }

        return headers;
    }

    public static int loadPlayersInput(List<PlayerInputData> providedPlayers, FileInputStream input) {
        int dcNameIndex = -1;
        int currentFaceitEloIndex = -1;

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
            String stringCellValue = headerCell.getStringCellValue();
            if (stringCellValue.equals(nameColumnHeader)) {
                dcNameIndex = index;
            } else if (stringCellValue.equals(eloColumnHeader)) {
                currentFaceitEloIndex = index;
            }
            index++;
        }

        if (dcNameIndex < 0 || currentFaceitEloIndex < 0) {
            EloApplication.showError("Wrong columns, select manually");
            return -1;
        }

        int rowIndex = 0;
        int longestName = 0;
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

            if (nameInSheet.length() > longestName) {
                longestName = nameInSheet.length();
            }

            PlayerInputData inputPlayer = new PlayerInputData(nameInSheet, eloInSheet);

            playerList.add(inputPlayer);
            rowIndex++;
        }

        providedPlayers.addAll(playerList);
        return longestName;
    }

    public static String outputTeams(List<List<Team>> potentialTeams, List<Player> reserve) {
        StringBuilder outputDataToApp = new StringBuilder();

        List<Team> option = potentialTeams.get(0);
        int faceitDiff = scoreTeams(option);
        outputDataToApp.append("Optimized Teams (Max diff of average faceit elo = ").append(faceitDiff).append("):\n");
        for (Team team : option) {
            outputDataToApp.append("\n").append(team).append("\n");
        }
        outputDataToApp.append("\n").append("Reserve players:\n");
        for (Player player : reserve) {
            outputDataToApp.append("@").append(player).append("\n");
        }
        return outputDataToApp.toString();
    }
}
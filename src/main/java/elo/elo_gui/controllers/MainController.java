package elo.elo_gui.controllers;

import elo.elo_gui.calculations.TeamOptimizer;
import elo.elo_gui.calculations.dtos.Player;
import elo.elo_gui.calculations.dtos.PlayerConnection;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import elo.elo_gui.calculations.dtos.Team;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static elo.elo_gui.calculations.FileUtil.*;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;

public class MainController {
    public static File inputFile;
    List<Player> players = new ArrayList<>();
    List<Player> reserve = new ArrayList<>();
    public static int longestName;

    @FXML
    private Button sheetBrowserButton;
    @FXML
    private Label loadedFile;
    @FXML
    private Button loadPlayersButton;
    @FXML
    private Button setColumns;
    @FXML
    private Button checkTable;
    @FXML
    private ScrollPane loadedPlayerListPane;
    @FXML
    private TableView<PlayerInputData> playerTable;
    @FXML
    private TableColumn<PlayerInputData, String> discordNameColumn;
    @FXML
    private TableColumn<PlayerInputData, String> eloColumn;
    @FXML
    private TableColumn<PlayerInputData, Integer> numberColumn;
    @FXML
    private TextField iterationCountTextField;
    @FXML
    private TextField optionCountTextField;
    @FXML
    private Button generateButton;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea infoTextArea;
    @FXML
    private TableView<Team> outputTable;
    @FXML
    private TableColumn<Team, String> teamName;
    @FXML
    private TableColumn<Team, String> teamIcon;
    @FXML
    private TableColumn<Team, Integer> averageElo;
    @FXML
    private TableColumn<Team, Player> player1;
    @FXML
    private TableColumn<Team, Player> player2;
    @FXML
    private TableColumn<Team, Player> player3;
    @FXML
    private TableColumn<Team, Player> player4;
    @FXML
    private TableColumn<Team, Player> player5;
    @FXML
    private Button copyToClipboard;
    @FXML
    private TextArea hiddenField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button addTeammatesButton;
    @FXML
    private Button addTeamSeparationButton;
    @FXML
    private TableView<PlayerConnection> teammateTable;
    @FXML
    private TableColumn<PlayerConnection, PlayerInputData> teammate1Column;
    @FXML
    private TableColumn<PlayerConnection, PlayerInputData> teammate2Column;
    @FXML
    private TableView<PlayerConnection> teamSeparationTable;
    @FXML
    private TableColumn<PlayerConnection, PlayerInputData> teamSeparation1Column;
    @FXML
    private TableColumn<PlayerConnection, PlayerInputData> teamSeparation2Column;

    @FXML
    protected void onSelectSourceClick() {
        System.out.println("Clicked select source");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Sheet (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        inputFile = fileChooser.showOpenDialog(sheetBrowserButton.getScene().getWindow());
        if (inputFile != null) {
            loadedFile.setText(inputFile.getName());
            loadPlayersButton.setDisable(false);
            checkTable.setDisable(true);
            setColumns.setDisable(false);
            playerTable.getItems().clear();
            outputTextArea.setText("");
            players.clear();
            reserve.clear();
            teammateTable.getItems().clear();
            teamSeparationTable.getItems().clear();
            progressBar.setProgress(0.0);
            PlayerInputData.counterTotal = 0;
            addTeammatesButton.setDisable(true);
            addTeamSeparationButton.setDisable(true);
            onLoadPlayersClick();
        } else {
            loadPlayersButton.setDisable(true);
            loadedFile.setText("");
        }
    }

    @FXML
    public void onLoadPlayersClick() {
        if (inputFile == null) {
            loadPlayersButton.setDisable(true);
            return;
        }
        PlayerInputData.counterTotal = 0;
        System.out.println("Clicked load players");
        List<PlayerInputData> playersInputData = new ArrayList<>();
        try {
            longestName = loadPlayersInput(playersInputData, new FileInputStream(inputFile));
        } catch (FileNotFoundException e) {
            printError("Failed to load input file");
        }
        if (longestName < 0) {
            return;
        }
        playerTable.getItems().clear();
        checkTable.setDisable(false);
        discordNameColumn.setCellValueFactory(new PropertyValueFactory<>("discordName"));
        eloColumn.setCellValueFactory(new PropertyValueFactory<>("currentFaceit"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        eloColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        eloColumn.setOnEditCommit(event -> {
            PlayerInputData elo = event
                    .getTableView()
                    .getItems()
                    .get(event.getTablePosition().getRow());
            elo.setCurrentFaceit(String.valueOf(event.getNewValue()));
        });

        playerTable.setRowFactory(new Callback<>() {
            @Override
            public TableRow<PlayerInputData> call(TableView<PlayerInputData> tableView) {
                final TableRow<PlayerInputData> row = new TableRow<>() {

                    @Override
                    protected void updateItem(PlayerInputData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            return;
                        }
                        boolean failed = false;
                        Color bad = new Color(1, 0, 0, 0.4);
                        Color good = new Color(0, 1, 0, 0.4);

                        try {
                            parseInt(item.getCurrentFaceit());
                        } catch (NumberFormatException e) {
                            failed = true;
                        }
                        this.setBackground(new Background(new BackgroundFill(failed ? bad : good, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                };
                return row;
            }
        });

        playersInputData.forEach(player -> {
            playerTable.getItems().add(player);
        });
        onCheckTableClick();
    }

    @FXML
    protected void onCheckTableClick() {
        System.out.println("Clicked check table");
        ObservableList<PlayerInputData> items = playerTable.getItems();
        for (PlayerInputData item : items) {
            try {
                parseInt(item.getCurrentFaceit());
            } catch (NumberFormatException e) {
                printError("Please fix elo for " + item);
                return;
            }
        }
        printError("Table valid.");
        generateButton.setDisable(false);
        checkTable.setDisable(true);
        setColumns.setDisable(true);
        loadPlayersButton.setDisable(true);
        addTeammatesButton.setDisable(false);
        addTeamSeparationButton.setDisable(false);
    }

    @FXML
    protected void onGenerateTeamsClick() {
        System.out.println("Clicked generate");
        outputTextArea.setText("");
        players.clear();
        reserve.clear();
        outputTable.setItems(FXCollections.observableList(new ArrayList<>()));
        progressBar.setProgress(0.0);
        List<Player> temporary = new ArrayList<>();
        ObservableList<PlayerInputData> items = playerTable.getItems();
        for (PlayerInputData item : items) {
            Player player = Player.builder()
                    .discordName(item.getDiscordName())
                    .currentFaceit(parseInt(item.getCurrentFaceit()))
                    .build();
            temporary.add(player);
        }

        List<Player> reservePlayers = new ArrayList<>();
        int reserveCount = temporary.size() % 5;
        if (reserveCount != 0) {
            for (int i = 0; i <= temporary.size() % 5; i++) {
                Player reservePlayer = temporary.getLast();
                reservePlayers.add(reservePlayer);
                temporary.remove(reservePlayer);
            }
        }

        players.addAll(temporary);
        reserve.addAll(reservePlayers);

        ObservableList<PlayerConnection> teammateItems = teammateTable.getItems();
        ObservableList<PlayerConnection> teamSeparationItems = teamSeparationTable.getItems();

        Map<String, Player> temporaryMap = new HashMap<>();
        players.forEach(player -> temporaryMap.put(player.getDiscordName(), player));
        reserve.forEach(player -> temporaryMap.put(player.getDiscordName(), player));

        teammateItems.forEach(teamMateConnection -> {
            players.forEach(player -> {
                if (player.getDiscordName().equals(teamMateConnection.getP1().getDiscordName())) {
                    Player player2 = temporaryMap.get(teamMateConnection.getP2().getDiscordName());
                    player.addTeammate(player2);
                }
                if (player.getDiscordName().equals(teamMateConnection.getP2().getDiscordName())) {
                    Player player1 = temporaryMap.get(teamMateConnection.getP1().getDiscordName());
                    player.addTeammate(player1);
                }
            });
            reserve.forEach(player -> {
                if (player.getDiscordName().equals(teamMateConnection.getP1().getDiscordName())) {
                    Player player2 = temporaryMap.get(teamMateConnection.getP2().getDiscordName());
                    player.addTeammate(player2);
                }
                if (player.getDiscordName().equals(teamMateConnection.getP2().getDiscordName())) {
                    Player player1 = temporaryMap.get(teamMateConnection.getP1().getDiscordName());
                    player.addTeammate(player1);
                }
            });
        });

        teamSeparationItems.forEach(teamMateConnection -> {
            players.forEach(player -> {
                if (player.getDiscordName().equals(teamMateConnection.getP1().getDiscordName())) {
                    Player player2 = temporaryMap.get(teamMateConnection.getP2().getDiscordName());
                    player.addAdversary(player2);
                }
                if (player.getDiscordName().equals(teamMateConnection.getP2().getDiscordName())) {
                    Player player1 = temporaryMap.get(teamMateConnection.getP1().getDiscordName());
                    player.addAdversary(player1);
                }
            });
            reserve.forEach(player -> {
                if (player.getDiscordName().equals(teamMateConnection.getP1().getDiscordName())) {
                    Player player2 = temporaryMap.get(teamMateConnection.getP2().getDiscordName());
                    player.addAdversary(player2);
                }
                if (player.getDiscordName().equals(teamMateConnection.getP2().getDiscordName())) {
                    Player player1 = temporaryMap.get(teamMateConnection.getP1().getDiscordName());
                    player.addAdversary(player1);
                }
            });
        });

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                List<List<Team>> potentialTeams = new ArrayList<>();
                double progressValue;
                int iterationCounter = parseInt(iterationCountTextField.getText()) * 100000;
                for (int i = 0; i < 10; i++) {
                    List<Team> temp = TeamOptimizer.calculateSingle(players, iterationCounter);
                    potentialTeams.add(temp);
                    progressValue = (double) (i + 1) / 10;
                    double finalProgressValue = progressValue;
                    Platform.runLater(() -> progressBar.setProgress(finalProgressValue));
                }
                potentialTeams.sort(comparingInt(TeamOptimizer::scoreTeams));
                String sortedTeams = outputTeams(potentialTeams, reserve);

                List<Team> finalTeam = potentialTeams.get(0);
                finalTeam.sort(Comparator.comparing(Team::getTeamName));
                for (Team team : finalTeam) {
                    outputTable.getItems().add(team);
                }

                List<String> outputLineList = new ArrayList<>(sortedTeams.lines().toList());
                StringBuilder outputBuilder = new StringBuilder();
                outputBuilder.append(outputLineList.get(0)).append("\n");
                outputLineList.remove(0);
                boolean reserveFound = false;
                for (String line : outputLineList) {
                    if (line.contains("Reserve players")) {
                        reserveFound = true;
                    }
                    if (reserveFound) {
                        outputBuilder.append(line).append("\n");
                    }
                }

                outputTextArea.setText(outputBuilder.toString());
                hiddenField.setText(sortedTeams);
                return null;
            }
        };

        new Thread(task).start();

        StringProperty style = new SimpleStringProperty();
        style.set("-fx-font-family: Consolas;");
        outputTable.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.styleProperty().bind(style);
            return row;
        });

        teamName.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamIcon.setCellValueFactory(new PropertyValueFactory<>("teamIcon"));
        averageElo.setCellValueFactory(new PropertyValueFactory<>("averageFaceitElo"));
        player1.setCellValueFactory(new PropertyValueFactory<>("player1"));
        player2.setCellValueFactory(new PropertyValueFactory<>("player2"));
        player3.setCellValueFactory(new PropertyValueFactory<>("player3"));
        player4.setCellValueFactory(new PropertyValueFactory<>("player4"));
        player5.setCellValueFactory(new PropertyValueFactory<>("player5"));
    }

    @FXML
    protected void onCopyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(hiddenField.getText());
        clipboard.setContent(content);
    }

    @FXML
    protected void onAddTeammatesClick() throws IOException {
        teammate1Column.setCellValueFactory(new PropertyValueFactory<>("p1"));
        teammate2Column.setCellValueFactory(new PropertyValueFactory<>("p2"));

        populate(teammateTable);
    }

    @FXML
    protected void onAddTeamSeparationClick() throws IOException {
        teamSeparation1Column.setCellValueFactory(new PropertyValueFactory<>("p1"));
        teamSeparation2Column.setCellValueFactory(new PropertyValueFactory<>("p2"));

        populate(teamSeparationTable);
    }

    @FXML
    protected void onSetColumnsClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("column-selector.fxml"));
        Parent mainWindow = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Elo optimizer");
        stage.setScene(new Scene(mainWindow));
        ColumnSelectorController controller = fxmlLoader.getController();
        controller.populateBoxes(FXCollections.observableList(getHeaderNames(new FileInputStream(inputFile))));
        stage.show();
    }

    private void populate(TableView table) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("player-picker.fxml"));
        Parent mainWindow = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Elo optimizer");
        stage.setScene(new Scene(mainWindow));
        PlayerPickerController controller = fxmlLoader.getController();
        stage.show();
        table.setVisible(true);
        controller.populateBoxes(playerTable.getItems(), table);
    }

    protected void addToTable(TableView table, PlayerInputData item, PlayerInputData item2) {
        PlayerConnection connection = PlayerConnection.builder()
                .p1(item)
                .p2(item2)
                .build();
        table.getItems().add(connection);
    }

    public void printError(String errorMessage) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                infoTextArea.setText(errorMessage);
                return null;
            }
        };

        new Thread(task).start();
    }
}
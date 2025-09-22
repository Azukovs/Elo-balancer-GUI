package elo.elo_gui;

import elo.elo_gui.calculations.TeamOptimizer;
import elo.elo_gui.calculations.dtos.Player;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import elo.elo_gui.calculations.dtos.Team;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static elo.elo_gui.calculations.FileUtil.loadPlayersInput;
import static elo.elo_gui.calculations.FileUtil.outputTeams;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;

public class MainController {
    File inputFile;
    List<Player> players = new ArrayList<>();
    List<Player> reserve = new ArrayList<>();

    @FXML
    private Button sheetBrowserButton;
    @FXML
    private Label loadedFile;
    @FXML
    private Button loadPlayersButton;
    @FXML
    private Button checkTable;
    @FXML
    private ScrollPane loadedPlayerListPane;
    @FXML
    private TableView playerTable;
    @FXML
    private TableColumn discordName;
    @FXML
    private TableColumn elo;
    @FXML
    private TextField iterationCount;
    @FXML
    private TextField optionCount;
    @FXML
    private Button generate;
    @FXML
    private TextArea output;
    @FXML
    private ProgressBar progressBar;

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
            playerTable.getItems().clear();
            output.setText("");
            players.clear();
            reserve.clear();
            progressBar.setProgress(0.0);
        } else {
            loadPlayersButton.setDisable(true);
            loadedFile.setText("");
        }
    }

    @FXML
    protected void onLoadPlayersClick() throws FileNotFoundException {
        if (inputFile == null) {
            loadPlayersButton.setDisable(true);
            return;
        }
        System.out.println("Clicked load players");
        List<PlayerInputData> players = new ArrayList<>();
        List<PlayerInputData> reserve = new ArrayList<>();
        loadPlayersInput(players, reserve, new FileInputStream(inputFile));
        playerTable.getItems().clear();
        checkTable.setDisable(false);
        discordName.setCellValueFactory(new PropertyValueFactory<>("discordName"));
        elo.setCellValueFactory(new PropertyValueFactory<>("currentFaceit"));
        elo.setCellFactory(TextFieldTableCell.forTableColumn());

        elo.setOnEditCommit((EventHandler<TableColumn.CellEditEvent>) event -> {
            PlayerInputData elo = (PlayerInputData) event
                    .getTableView()
                    .getItems()
                    .get(event.getTablePosition().getRow());
            elo.setCurrentFaceit(String.valueOf(event.getNewValue()));
        });

        playerTable.setRowFactory(new Callback<TableView<PlayerInputData>, TableRow<PlayerInputData>>() {
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
                System.out.println("Updated row");
                return row;
            }
        });

        players.forEach(player -> playerTable.getItems().add(player));
        reserve.forEach(player -> playerTable.getItems().add(player));
    }

    @FXML
    protected void onCheckTableClick() {
        System.out.println("Clicked check table");
        ObservableList<PlayerInputData> items = playerTable.getItems();
        for (PlayerInputData item : items) {
            try {
                parseInt(item.getCurrentFaceit());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Please fix elo for " + item).showAndWait();
                return;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Table valid.").showAndWait();
        playerTable.setEditable(false);
        generate.setDisable(false);
        checkTable.setDisable(true);
        loadPlayersButton.setDisable(true);
    }

    @FXML
    protected void onGenerateTeamsClick() {
        System.out.println("Clicked generate");
        output.setText("");
        players.clear();
        reserve.clear();
        progressBar.setProgress(0.0);
        List<Player> temporary = new ArrayList<>();
        ObservableList<PlayerInputData> items = playerTable.getItems();
        for (PlayerInputData item : items) {
            temporary.add(Player.builder().discordName(item.getDiscordName()).currentFaceit(parseInt(item.getCurrentFaceit())).build());
        }

        List<Player> reservePlayers = new ArrayList<>();
        for (int i = 0; i <= temporary.size() % 5; i++) {
            Player reservePlayer = temporary.getLast();
            reservePlayers.add(reservePlayer);
            temporary.remove(reservePlayer);
        }

        players.addAll(temporary);
        reserve.addAll(reservePlayers);

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                List<List<Team>> potentialTeams = new ArrayList<>();
                double progressValue;
                int optionCounter = parseInt(optionCount.getText());
                int iterationCounter = parseInt(iterationCount.getText());
                for (int i = 0; i < optionCounter; i++) {
                    List<Team> temp = TeamOptimizer.calculateSingle(players, iterationCounter);
                    potentialTeams.add(temp);
                    progressValue = (double) (i + 1) / optionCounter;
                    double finalProgressValue = progressValue;
                    Platform.runLater(() -> progressBar.setProgress(finalProgressValue));
                }
                potentialTeams.sort(comparingInt(TeamOptimizer::scoreTeams));
                String sortedTeams = outputTeams(potentialTeams, reserve, optionCounter);

                output.setText(sortedTeams);
                return null;
            }
        };

        new Thread(task).start();
    }
}
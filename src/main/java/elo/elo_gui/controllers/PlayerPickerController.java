package elo.elo_gui.controllers;

import elo.elo_gui.EloApplication;
import elo.elo_gui.calculations.dtos.PlayerConnection;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import elo.elo_gui.custom_controls.AutoCompleteComboBoxListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PlayerPickerController {
    @FXML
    private ComboBox<PlayerInputData> player1Box;
    @FXML
    private ComboBox<PlayerInputData> player2Box;
    @FXML
    private Button confirmButton;

    private TableView<PlayerConnection> callerTable;

    protected void populateBoxes(ObservableList<PlayerInputData> players, TableView<PlayerConnection> caller) {
        callerTable = caller;

        StringConverter<PlayerInputData> stringConverter = new StringConverter<>() {
            @Override
            public String toString(PlayerInputData object) {
                if (object != null) {
                    return object.getDiscordName();
                }
                return null;
            }

            @Override
            public PlayerInputData fromString(String string) {
                return players
                        .stream()
                        .filter(playerInputData -> playerInputData.getDiscordName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        };

        player1Box.setItems(players);
        player1Box.setConverter(stringConverter);
        player2Box.setItems(players);
        player2Box.setConverter(stringConverter);
        new AutoCompleteComboBoxListener(player1Box);
        new AutoCompleteComboBoxListener(player2Box);
    }

    @FXML
    protected void confirmSelection() {
        PlayerInputData p1 = player1Box.getSelectionModel().getSelectedItem();
        PlayerInputData p2 = player2Box.getSelectionModel().getSelectedItem();

        MainController controller = EloApplication.loader.getController();
        if (p1.equals(p2)) {
            controller.printError("Must be different players!");
            return;
        } else {
            controller.addToTable(callerTable, p1, p2);
        }

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}

package elo.elo_gui;

import elo.elo_gui.calculations.dtos.PlayerConnection;
import elo.elo_gui.calculations.dtos.PlayerInputData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.control.Alert.AlertType.ERROR;

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
        player1Box.setItems(players);
        player2Box.setItems(players);
    }

    @FXML
    protected void confirmSelection() throws IOException {
        PlayerInputData p1 = player1Box.getSelectionModel().getSelectedItem();
        PlayerInputData p2 = player2Box.getSelectionModel().getSelectedItem();
        if (p1.equals(p2)) {
            new Alert(ERROR, "Must be different players!").showAndWait();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-window.fxml"));
        Parent mainWindow = (Parent) fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.addToTable(callerTable, p1, p2);

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}

package elo.elo_gui;

import elo.elo_gui.calculations.FileUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ColumnSelectorController {
    @FXML
    private ComboBox<String> nameColumnBox;
    @FXML
    private ComboBox<String> eloColumnBox;
    @FXML
    private Button confirmButton;

    @FXML
    protected void confirmSelection() throws IOException {
        String selectedNameColumn = nameColumnBox.getSelectionModel().getSelectedItem();
        String selectedEloColumn = eloColumnBox.getSelectionModel().getSelectedItem();

        FileUtil.nameColumnHeader = selectedNameColumn;
        FileUtil.eloColumnHeader = selectedEloColumn;

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    public void populateBoxes(ObservableList<String> headers) {
        nameColumnBox.setItems(headers);
        eloColumnBox.setItems(headers);
    }
}

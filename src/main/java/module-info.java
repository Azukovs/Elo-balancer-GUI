module elo.elo_gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires lombok;

    opens elo.elo_gui to javafx.fxml;
    opens elo.elo_gui.calculations to javafx.fxml;
    opens elo.elo_gui.calculations.dtos to javafx.base;
    exports elo.elo_gui;
}
package elo.elo_gui;

import elo.elo_gui.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EloApplication extends Application {
    public static FXMLLoader loader;

    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("glass.disableThreadChecks", "true");
        if (loader == null) {
            loader = new FXMLLoader(EloApplication.class.getResource("controllers/main-window.fxml"));
        }
        Scene scene = new Scene(loader.load(), 1700, 960);
        stage.setTitle("Elo optimizer");
        stage.setScene(scene);
        stage.show();
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            EloApplication.showError(exception);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void showError(Throwable e) {
        MainController controller = loader.getController();

        if (e.getCause() == null) {
            controller.printError(e.getMessage());
        }
        if (e.getCause() != null) {
            showError(e.getCause());
        }
    }

    public static void showError(String msg) {
        MainController controller = loader.getController();
        controller.printError(msg);
    }
}
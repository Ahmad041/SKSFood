package org.gui.sksfood;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class MainApp extends Application {

    private static final String LOGIN_FXML = "/org/gui/sksfood/LoginView.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getResource(LOGIN_FXML);
        if (resource == null) {
            throw new IllegalStateException(
                    "FXML tidak ditemukan: " + LOGIN_FXML + "\n" +
                            "Pastikan LoginView.fxml ada di:\n" +
                            "  src/main/resources/org/gui/sksfood/LoginView.fxml"
            );
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent loginRoot = loader.load();

        Scene loginScene = new Scene(loginRoot, 1280, 900);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("SKS-Food – HIMMA Pendidikan");
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
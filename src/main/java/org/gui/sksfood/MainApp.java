package org.gui.sksfood;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static final String LOGIN_FXML = "/org/gui/sksfood/LoginView.fxml";

    // Disimpan sebagai static supaya bisa diakses dari controller manapun
    // lewat MainApp.switchScene(...)
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainApp.primaryStage = primaryStage;

        URL resource = getClass().getResource(LOGIN_FXML);
        if (resource == null) {
            throw new IllegalStateException(
                    "FXML tidak ditemukan: " + LOGIN_FXML + " " +
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

    /**
     * Mengganti scene yang sedang tampil di primaryStage dengan FXML baru.
     *
     * @param fxmlPath path absolut ke file FXML, contoh:
     *                 "/org/gui/sksfood/DashboardView.fxml"
     */
    public static void switchScene(String fxmlPath) {
        try {
            URL resource = MainApp.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException(
                        "FXML tidak ditemukan: " + fxmlPath + "\n" +
                                "Pastikan file berada di folder resources sesuai path tersebut."
                );
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1280, 900);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

        } catch (IOException e) {
            throw new RuntimeException("Gagal memuat scene: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
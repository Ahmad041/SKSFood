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

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("SKS-Food – HIMMA Pendidikan");
        primaryStage.setFullScreenExitHint("");
        primaryStage.setResizable(false);

        switchScene(LOGIN_FXML);

        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * Ganti isi Stage utama ke FXML lain tanpa membuat Stage baru,
     * supaya style (undecorated, fullscreen, ukuran) tetap konsisten
     * di seluruh halaman aplikasi.
     *
     * Contoh pemanggilan dari controller lain setelah login berhasil:
     *   MainApp.switchScene("/org/gui/sksfood/Karyawan.fxml");
     */
    public static void switchScene(String fxmlPath) {
        try {
            URL resource = MainApp.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException(
                        "FXML tidak ditemukan: " + fxmlPath + "\n" +
                                "Pastikan file berada di src/main/resources" + fxmlPath
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
            throw new RuntimeException("Gagal memuat FXML: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package org.gui.sksfood;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;


public class TestPelangganApp extends Application {

    // Sesuaikan path ini dengan lokasi FXML Pelanggan kamu
    private static final String PELANGGAN_FXML = "/org/gui/sksfood/Pelanggan.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource(PELANGGAN_FXML);
        if (resource == null) {
            throw new IllegalStateException(
                    "FXML tidak ditemukan: " + PELANGGAN_FXML + "\n" +
                            "Cek lagi nama file & lokasinya di src/main/resources/..."
            );
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();


        var bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());

        stage.setTitle("TEST - Halaman Pelanggan");
        stage.setScene(scene);


        stage.setMaximized(true);


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
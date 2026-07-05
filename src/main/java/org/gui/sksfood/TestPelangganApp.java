package org.gui.sksfood;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Class khusus untuk TESTING halaman Pelanggan saja,
 * tanpa perlu login dulu lewat MainApp.
 *
 * Cara pakai:
 * - Di IDE: klik kanan file ini -> Run 'TestPelangganApp.main()'
 * - Lewat command line (lihat instruksi di chat)
 *
 * File ini TIDAK dipakai di aplikasi asli, hanya untuk development/testing.
 * Boleh dihapus nanti kalau sudah tidak dibutuhkan.
 */
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

        Scene scene = new Scene(root, 1432, 1024);
        stage.setTitle("TEST - Halaman Pelanggan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
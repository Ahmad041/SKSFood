package org.gui.sksfood.controller;

import SQL.DBConnect;
import org.gui.sksfood.ADT.Karyawan;
import org.gui.sksfood.MainApp;
import org.gui.sksfood.controller.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    // ── fx:id bindings ────────────────────────────────────────────────
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnLogin;
    @FXML private Button        btnTogglePassword;
    @FXML private Button        btnAstraTech;
    @FXML private Hyperlink     lnkLupaPassword;
    @FXML private Hyperlink     lnkRegister;

    private static final String DASHBOARD_FXML = "/org/gui/sksfood/Karyawan.fxml";

    // ── onAction handlers ─────────────────────────────────────────────

    /** Tombol "Log In →" */
    @FXML
    private void onLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanPeringatan("Username dan password wajib diisi.");
            return;
        }

        Karyawan karyawan = cekLogin(username, password);

        if (karyawan == null) {
            tampilkanPeringatan("Username atau password salah.");
            return;
        }

        if (!"Aktif".equalsIgnoreCase(karyawan.getStatusKaryawan())) {
            tampilkanPeringatan("Akun ini tidak aktif. Hubungi admin.");
            return;
        }

        Session.setLoggedInKaryawan(karyawan);
        MainApp.switchScene(DASHBOARD_FXML);
    }

    private Karyawan cekLogin(String username, String password) {
        String sql = "SELECT * FROM karyawan WHERE krw_username = ? AND krw_password = ?";

        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            Karyawan k = null;
            if (rs.next()) {
                k = new Karyawan(
                        rs.getString("krw_id"),
                        rs.getString("krw_nama"),
                        rs.getString("krw_noTlpn"),
                        rs.getString("krw_jabatan"),
                        rs.getString("krw_username"),
                        rs.getString("krw_password"),
                        rs.getString("krw_status")
                );
            }
            rs.close();
            return k;
        } catch (SQLException e) {
            tampilkanError("Gagal terhubung ke database", e);
            return null;
        }
    }

    /** Tombol show/hide password */
    @FXML
    private void onTogglePassword(ActionEvent event) {
        // JavaFX tidak mendukung toggle PasswordField ↔ TextField secara native di FXML.
        // Implementasi: tukar visibility antara PasswordField dan TextField biasa
        // atau gunakan custom skin di sini.
        System.out.println("Toggle password visibility");
    }

    /** Tombol "AstraTech" SSO */
    @FXML
    private void onLoginAstraTech(ActionEvent event) {
        // TODO: buka OAuth flow AstraTech
        System.out.println("Login via AstraTech");
    }

    /** Link "Lupa Password?" */
    @FXML
    private void onLupaPassword(ActionEvent event) {
        // TODO: buka halaman / dialog reset password
        System.out.println("Lupa password clicked");
    }

    /** Link "Register here" */
    @FXML
    private void onRegister(ActionEvent event) {
        // TODO: navigasi ke halaman registrasi
        System.out.println("Register clicked");
    }

    // ── ALERT HELPER ──────────────────────────────────────────────────
    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanError(String pesan, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(pesan);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }


}
package org.gui.sksfood.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    // ── fx:id bindings ────────────────────────────────────────────────
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnLogin;
    @FXML private Button        btnTogglePassword;
    @FXML private Button        btnAstraTech;
    @FXML private Hyperlink     lnkLupaPassword;
    @FXML private Hyperlink     lnkRegister;

    // ── onAction handlers ─────────────────────────────────────────────

    /** Tombol "Log In →" */
    @FXML
    private void onLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // TODO: tampilkan pesan error / shake animation
            return;
        }

        // TODO: panggil service autentikasi
        // AuthService.login(username, password);
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
}
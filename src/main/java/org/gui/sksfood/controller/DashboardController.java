package org.gui.sksfood.controller;

import SQL.DBConnect;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

// =============================================
// 1. Model Class untuk Data Dashboard
// =============================================
class DashboardData {
    private String saldoKas;
    private String pendapatan;
    private String pengeluaran;
    private int jumlahPesanan;
    private String ketersediaanStok;
    private String totalOmset;
    private double persentaseKeuntungan;
    private String totalBulanIni;

    public DashboardData() {}

    public void setSaldoKas(String saldoKas) { this.saldoKas = saldoKas; }
    public void setPendapatan(String pendapatan) { this.pendapatan = pendapatan; }
    public void setPengeluaran(String pengeluaran) { this.pengeluaran = pengeluaran; }
    public void setJumlahPesanan(int jumlah) { this.jumlahPesanan = jumlah; }
    public void setKetersediaanStok(String stok) { this.ketersediaanStok = stok; }
    public void setTotalOmset(String omset) { this.totalOmset = omset; }
    public void setPersentaseKeuntungan(double profit) { this.persentaseKeuntungan = profit; }
    public void setTotalBulanIni(String total) { this.totalBulanIni = total; }

    // Getters...
}

public class DashboardController {

    // =============================================
    // 2. Database Connection
    // =============================================
    private DBConnect connect = new DBConnect();

    // =============================================
    // 3. Session Management
    // =============================================
    private UserSession session = new UserSession();
    private boolean isLoggedIn = false;

    // =============================================
    // 4. FXML References
    // =============================================
    @FXML private Label lblSaldoKas;
    @FXML private Label lblPendapatan;
    @FXML private Label lblPengeluaran;
    @FXML private Label lblJumlahPesanan;
    @FXML private Label lblKetersediaanStok;
    @FXML private Label lblTotalOmset;
    @FXML private Label lblPersentaseKeuntungan;
    @FXML private Label lblTotalBulanIni;
    @FXML private Label lblGrafikJudul;
    @FXML private Label lblStatusUser;

    // PERBAIKAN: Gunakan tipe data generik standar dari LineChart
    @FXML private LineChart<String, Number> chartPenjualan;

    // =============================================
    // 5. Navigation References
    // =============================================
    @FXML private Button btnDashboard;
    @FXML private Button btnKaryawan;
    @FXML private Button btnMenu;
    @FXML private Button btnPelanggan;
    @FXML private Button btnPemesanan;
    @FXML private Button btnTempat;
    @FXML private Button btnUlasanMenu;
    @FXML private Button btnKembali;
    @FXML private Button btnSettings;
    @FXML private Button btnLogout;

    private static class UserSession {
        private String username;
        private String role;
        private LocalDateTime loginTime;

        public void setUsername(String name) { this.username = name; }
        public void setRole(String role) { this.role = role; }
        public void setLoginTime(LocalDateTime time) { this.loginTime = time; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public LocalDateTime getLoginTime() { return loginTime; }
    }

    @FXML
    private void onDashboardClick(ActionEvent actionEvent) {
        loadDashboardData();
        updateMenuStyle("Dashboard");
    }

    @FXML
    private void onKaryawanClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.KaryawanController", "/org/gui/sksfood/Karyawan.fxml");
            updateMenuStyle("Karyawan");
        } catch (Exception e) {
            showError("Gagal membuka halaman Karyawan: " + e.getMessage());
        }
    }

    @FXML
    private void onMenuClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.MenuController", "/org/gui/sksfood/Menu.fxml");
            updateMenuStyle("Menu");
        } catch (Exception e) {
            showError("Gagal membuka halaman Menu: " + e.getMessage());
        }
    }

    @FXML
    private void onPelangganClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.PelangganController", "/org/gui/sksfood/Pelanggan.fxml");
            updateMenuStyle("Pelanggan");
        } catch (Exception e) {
            showError("Gagal membuka halaman Pelanggan: " + e.getMessage());
        }
    }

    @FXML
    private void onPemesananClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.PemesananController", "/org/gui/sksfood/Pemesanan.fxml");
            updateMenuStyle("Pemesanan");
        } catch (Exception e) {
            showError("Gagal membuka halaman Pemesanan: " + e.getMessage());
        }
    }

    @FXML
    private void onTempatClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.TempatController", "/org/gui/sksfood/Tempat.fxml");
            updateMenuStyle("Tempat");
        } catch (Exception e) {
            showError("Gagal membuka halaman Tempat: " + e.getMessage());
        }
    }

    @FXML
    private void onUlasanMenuClick(ActionEvent actionEvent) {
        try {
            navigateTo("org.gui.sksfood.controller.UlasanMenuController", "/org/gui/sksfood/UlasanMenu.fxml");
            updateMenuStyle("UlasanMenu");
        } catch (Exception e) {
            showError("Gagal membuka halaman Ulasan Menu: " + e.getMessage());
        }
    }

    @FXML
    private void onKembaliClick(ActionEvent actionEvent) {
        loadDashboardData();
    }

    @FXML
    private void onSettingsClick(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Fitur Settings akan segera hadir!");
        alert.setContentText("Anda bisa mengaktifkan notifikasi, tema, dan pengaturan lainnya.");
        alert.showAndWait();
    }

    @FXML
    private void onLogoutClick(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Logout");
        alert.setContentText("Apakah Anda yakin ingin keluar dari aplikasi?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                logout();
            }
        });
    }

    // =============================================
    // Navigasi Dinamis
    // =============================================
    private void navigateTo(String controllerName, String fxmlPath) throws IOException {
        showAlert("Loading", "Sedang memuat halaman " + controllerName + "...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage newStage = new Stage();
        Scene newScene = new Scene(root, 1280, 900);
        newStage.setScene(newScene);
        newStage.setTitle(controllerName);

        // PERBAIKAN: Ambil referensi Window dari node yang sudah ada (btnDashboard)
        newStage.initOwner(btnDashboard.getScene().getWindow());
        newStage.setResizable(false);

        try {
            newStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/icons/app.ico")));
        } catch (Exception ignored) {
            // Abaikan jika icon tidak ditemukan agar aplikasi tidak crash
        }

        newStage.show();
    }

    private void loadDashboardData() {
        try {
            updateSummaryCards();
            updateListPesanan();
            showToast("Data Dashboard", "Data berhasil di-refresh!", "info");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal load data: " + e.getMessage());
        }
    }

    private void updateSummaryCards() {
        try {
            String query = "SELECT " +
                    "SUM(men_harga) as totalPendapatan, " +
                    "SUM(men_stok) as totalStok, " +
                    "CASE WHEN SUM(men_status) > 0 THEN 'Tersedia' ELSE 'Habis' END as statusStok " +
                    "FROM dbo.menu " +
                    "WHERE men_status IN ('Aktif', 'Habis')";

            Statement stmt = connect.conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                lblSaldoKas.setText("Rp. 5.000.000");
                lblPendapatan.setText("Rp. " + rs.getString("totalPendapatan"));
                lblJumlahPesanan.setText(String.valueOf(rs.getInt("totalStok")));
                lblKetersediaanStok.setText(rs.getString("statusStok"));
            }

            stmt.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateListPesanan() {
        String query = "SELECT TOP 5 * FROM dbo.pemesanan ORDER BY men_id DESC";
        try {
            Statement stmt = connect.conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            lblStatusUser.setText("Rizki Pilar - KARYAWAN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        session = new UserSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/gui/sksfood/LoginView.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow(); // Tutup window saat ini dan ganti scene
            Scene scene = new Scene(root, 1280, 900);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText(null);
        alert.setContentText("Anda telah keluar dari aplikasi.");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showToast(String title, String message, String type) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        lblSaldoKas.setText("Rp. 5.000.000");
        lblPendapatan.setText("Rp. 0");
        lblPengeluaran.setText("Rp. 1.500.000");
        lblJumlahPesanan.setText("0");
        lblKetersediaanStok.setText("0%");
        lblTotalOmset.setText("Rp. 0");
        lblPersentaseKeuntungan.setText("0%");
        lblTotalBulanIni.setText("Rp. 0");

        setupChart();
        loadDashboardData();
        setupSidebarMenu();
    }

    // =============================================
    // 26. Setup Chart (Grafik)
    // =============================================
    private void setupChart() {
        // PERBAIKAN: Cast Axis dan Gunakan Tipe Data XYChart standar
        chartPenjualan.getData().clear();

        CategoryAxis xAxis = (CategoryAxis) chartPenjualan.getXAxis();
        xAxis.setLabel("Bulan");
        xAxis.setCategories(FXCollections.observableArrayList("Jan", "Feb", "Mar", "Apr", "Mei", "Jun"));

        chartPenjualan.getYAxis().setLabel("Penjualan (Rp)");

        XYChart.Series<String, Number> pendapatan = new XYChart.Series<>();
        pendapatan.setName("Pendapatan");
        pendapatan.getData().add(new XYChart.Data<>("Jan", 5000000));
        pendapatan.getData().add(new XYChart.Data<>("Feb", 7500000));
        pendapatan.getData().add(new XYChart.Data<>("Mar", 6200000));
        pendapatan.getData().add(new XYChart.Data<>("Apr", 8000000));
        pendapatan.getData().add(new XYChart.Data<>("Mei", 9500000));

        XYChart.Series<String, Number> pengeluaran = new XYChart.Series<>();
        pengeluaran.setName("Pengeluaran");
        pengeluaran.getData().add(new XYChart.Data<>("Jan", 1500000));
        pengeluaran.getData().add(new XYChart.Data<>("Feb", 1800000));
        pengeluaran.getData().add(new XYChart.Data<>("Mar", 1600000));
        pengeluaran.getData().add(new XYChart.Data<>("Apr", 2000000));
        pengeluaran.getData().add(new XYChart.Data<>("Mei", 2200000));

        chartPenjualan.getData().addAll(pendapatan, pengeluaran);
    }

    // =============================================
    // 27. Helper: Setup Sidebar Menu
    // =============================================
    private void setupSidebarMenu() {
        // PERBAIKAN: Gunakan .setStyle() alih-alih .getStyle().set()
        btnDashboard.setStyle("-fx-background-color: #1D4ED8; -fx-text-fill: white; -fx-font-weight: bold;");
        btnKaryawan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnPelanggan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnPemesanan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnTempat.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnUlasanMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        btnKembali.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold;");
        btnSettings.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
    }

    // =============================================
    // 28. Helper: Update Menu Style saat navigasi
    // =============================================
    private void updateMenuStyle(String selectedMenu) {
        // PERBAIKAN: Gunakan .setStyle()
        btnDashboard.setStyle("-fx-background-color: transparent; -fx-text-fill: #1E3A8A;");
        btnKaryawan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
        btnMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
        btnPelanggan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
        btnPemesanan.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
        btnTempat.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");
        btnUlasanMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748B;");

        switch (selectedMenu) {
            case "Dashboard":
                btnDashboard.setStyle("-fx-background-color: #1D4ED8; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "Karyawan":
                btnKaryawan.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "Menu":
                btnMenu.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "Pelanggan":
                btnPelanggan.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "Pemesanan":
                btnPemesanan.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "Tempat":
                btnTempat.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case "UlasanMenu":
                btnUlasanMenu.setStyle("-fx-background-color: #1A3A8F; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
        }
    }
}
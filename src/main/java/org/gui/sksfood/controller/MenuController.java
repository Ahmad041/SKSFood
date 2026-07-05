package org.gui.sksfood.controller;

import SQL.DBConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDateTime;


public class MenuController {
    // ... existing code ...

    // ============================================
    // 1. FXML References (Inisialisasi dari FXML)
    // ============================================
    @FXML private TextField txtIdMenu;
    @FXML private TextField txtNamaMenu;
    @FXML private TextField txtDeskripsiMenu;
    @FXML private TextField txtHargaMenu;
    @FXML private TextField txtKategoriMenu;
    @FXML private TextField txtStokMenu;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private Label lblTotalMenu;
    @FXML private Label lblMenuTersedia;
    @FXML private Label lblMenuHabis;
    @FXML private TableView<Menu> tableViewMenu;
    @FXML private TableColumn<Menu, String> colMenu;
    @FXML private TableColumn<Menu, String> colKategori;
    @FXML private TableColumn<Menu, String> colHarga;
    @FXML private TableColumn<Menu, Integer> colStok;
    @FXML private TableColumn<Menu, String> colStatus;
    @FXML private TableColumn<Menu, String> colAksi;

    // ============================================
    // 2. Variable Helper
    // ============================================
    private DBConnect connect = new DBConnect();
    private boolean isEditMode = false;
    private Menu menuBaru = null;

    // ============================================
    // 3. CRUD: CREATE (Insert)
    // ============================================
    @FXML
    private void onSimpan(ActionEvent actionEvent) {
        try {
            // Validasi Form
            if (txtIdMenu.getText().isEmpty() || txtNamaMenu.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Mohon lengkapi semua data menu!");
                alert.showAndWait();
                return;
            }

            // Set Mode Baru jika belum ada
            if (!isEditMode) {
                menuBaru = new Menu(
                        txtIdMenu.getText(),
                        txtNamaMenu.getText(),
                        txtDeskripsiMenu.getText(),
                        Integer.parseInt(txtHargaMenu.getText()),
                        txtKategoriMenu.getText(),
                        Integer.parseInt(txtStokMenu.getText()),
                        cmbStatus.getValue()
                );
            }

            // Simpan ke Database
            saveMenu(menuBaru);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data menu berhasil disimpan!");
            alert.showAndWait();

            // Reset Form
            resetForm();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal menyimpan data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void saveMenu(Menu menu) throws SQLException {
        String insertQuery = "{CALL sp_InsertMenu(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        String updateQuery = "{CALL sp_UpdateMenu(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        CallableStatement csInsert = connect.conn.prepareCall(insertQuery);
        CallableStatement csUpdate = connect.conn.prepareCall(updateQuery);

        if (!isEditMode) {
            // INSERT mode
            csInsert.setString(1, menu.getMenId());
            csInsert.setString(2, menu.getMenNama());
            csInsert.setString(3, menu.getMenDeskripsi());
            csInsert.setInt(4, menu.getMenHarga());
            csInsert.setString(5, menu.getMenKategori());
            csInsert.setInt(6, menu.getMenStok());
            csInsert.setString(7, menu.getMenStatus());
            csInsert.setString(8, "Administrator");
            csInsert.setDate(9, java.sql.Date.valueOf(String.valueOf(LocalDateTime.now())));

            // Untuk update, kita perlu set @men_modifDate dan @men_modifBy
            // Gunakan @men_id dari procedure untuk INSERT
            int row = csInsert.executeUpdate();
            String updatedId = csInsert.getString(1);

            csUpdate.clearParameters();
            csUpdate.setString(1, updatedId);
            csUpdate.setString(2, null);
            csUpdate.setString(3, null);
            csUpdate.setInt(4, 0);
            csUpdate.setString(5, null);
            csUpdate.setInt(6, 0);
            csUpdate.setString(7, null);
            csUpdate.setString(8, "Administrator");

            csUpdate.execute();

        } else {
            // UPDATE mode
            csUpdate.clearParameters();
            csUpdate.setString(1, menu.getMenId());
            csUpdate.setString(2, menu.getMenNama());
            csUpdate.setString(3, menu.getMenDeskripsi());
            csUpdate.setInt(4, menu.getMenHarga());
            csUpdate.setString(5, menu.getMenKategori());
            csUpdate.setInt(6, menu.getMenStok());
            csUpdate.setString(7, menu.getMenStatus());
            csUpdate.setString(8, "Administrator");

            csUpdate.execute();
        }

        // Reload Data
        loadDataMenu();

        // Close Resources
        csInsert.close();
        csUpdate.close();

    }

    // ============================================
    // 4. CRUD: READ (Load Data dari DB)
    // ============================================
    @FXML
    private void loadDataMenu() {
        try {
            // Query untuk mengambil data dari tabel menu
            String query = "SELECT * FROM dbo.menu ORDER BY men_id DESC";

            Statement stmt = connect.conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableViewMenu.getItems().clear();

            while (rs.next()) {
                Menu menu = new Menu(
                        rs.getString("men_id"),
                        rs.getString("men_nama"),
                        rs.getString("men_deskripsi"),
                        rs.getInt("men_harga"),
                        rs.getString("men_kategori"),
                        rs.getInt("men_stok"),
                        rs.getString("men_status")
                );
                tableViewMenu.getItems().add(menu);
            }

            rs.close();
            stmt.close();

            // Update Count
            updateMenuCount();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // 5. CRUD: READ (Update Menu Count di UI)
    // ============================================
    private void updateMenuCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM dbo.menu " +
                    "UNION ALL SELECT COUNT(*) as total WHERE men_status = 'Aktif' " +
                    "UNION ALL SELECT COUNT(*) as total WHERE men_status = 'Habis'";

            // Lebih baik gunakan query terpisah
            String countQuery = "SELECT COUNT(*) as total FROM dbo.menu WHERE men_status = 'Aktif'";
            String habisQuery = "SELECT COUNT(*) as total FROM dbo.menu WHERE men_status = 'Habis'";

            Statement stmt = connect.conn.createStatement();
            ResultSet rsTotal = stmt.executeQuery(countQuery);
            ResultSet rsHabis = stmt.executeQuery(habisQuery);

            rsTotal.next();
            lblTotalMenu.setText(String.valueOf(rsTotal.getInt("total")));

            rsHabis.next();
            lblMenuHabis.setText(String.valueOf(rsHabis.getInt("total")));

            // Untuk menu tersedia, kita hitung total - habis
            rsHabis.close();
            stmt.close();
            rsTotal.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // 6. CRUD: UPDATE (Ubah Data)
    // ============================================
    @FXML
    private void onUbah(ActionEvent actionEvent) {
        if (tableViewMenu.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Pilih data menu yang akan diubah!");
            alert.showAndWait();
            return;
        }

        // Set mode edit
        isEditMode = true;

        // Ambil data menu yang dipilih dari TableView
        Menu selectedMenu = tableViewMenu.getSelectionModel().getSelectedItem();

        // Isi Form dengan data yang dipilih
        txtIdMenu.setText(selectedMenu.getMenId());
        txtNamaMenu.setText(selectedMenu.getMenNama());
        txtDeskripsiMenu.setText(selectedMenu.getMenDeskripsi());
        txtHargaMenu.setText(String.valueOf(selectedMenu.getMenHarga()));
        txtKategoriMenu.setText(selectedMenu.getMenKategori());
        txtStokMenu.setText(String.valueOf(selectedMenu.getMenStok()));
        cmbStatus.setValue(selectedMenu.getMenStatus());

        // Reset Flag
        isEditMode = false;
    }

    // ============================================
    // 7. CRUD: DELETE (Hapus Data)
    // ============================================
    @FXML
    private void onHapus(ActionEvent actionEvent) {
        if (tableViewMenu.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(null);
            alert.setContentText("Pilih data menu yang akan dihapus!");
            alert.showAndWait();
            return;
        }

        Menu selectedMenu = tableViewMenu.getSelectionModel().selectedItemProperty().get();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin menghapus data menu '" + selectedMenu.getMenNama() + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteMenu(selectedMenu.getMenId());
            }
        });
    }

    private void deleteMenu(String menId) {
        try {
            String deleteQuery = "DELETE FROM dbo.menu WHERE men_id = ?";
            PreparedStatement pstmt = connect.conn.prepareStatement(deleteQuery);
            pstmt.setString(1, menId);
            pstmt.executeUpdate();

            // Reload Data
            loadDataMenu();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data menu berhasil dihapus!");
            alert.showAndWait();

            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal menghapus data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ============================================
    // 8. Helper: Bersihkan Form
    // ============================================
    private void resetForm() {
        txtIdMenu.clear();
        txtNamaMenu.clear();
        txtDeskripsiMenu.clear();
        txtHargaMenu.clear();
        txtKategoriMenu.clear();
        txtStokMenu.clear();
        cmbStatus.setValue(null);

        isEditMode = false;
        menuBaru = null;
    }

    // ============================================
    // 9. Helper: Model Class Menu
    // ============================================
    private static class Menu {
        private String menId;
        private String menNama;
        private String menDeskripsi;
        private int menHarga;
        private String menKategori;
        private int menStok;
        private String menStatus;

        public Menu(String id, String nama, String deskripsi, int harga, String kategori, int stok, String status) {
            this.menId = id;
            this.menNama = nama;
            this.menDeskripsi = deskripsi;
            this.menHarga = harga;
            this.menKategori = kategori;
            this.menStok = stok;
            this.menStatus = status;
        }

        public String getMenId() { return menId; }
        public String getMenNama() { return menNama; }
        public String getMenDeskripsi() { return menDeskripsi; }
        public int getMenHarga() { return menHarga; }
        public String getMenKategori() { return menKategori; }
        public int getMenStok() { return menStok; }
        public String getMenStatus() { return menStatus; }
    }

    // ... existing code ...
}
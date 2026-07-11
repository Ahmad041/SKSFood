package org.gui.sksfood.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.gui.sksfood.ADT.Tempat;
import SQL.DBConnect;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TempatController implements Initializable {

    // Form fields
    @FXML private TextField txtIdTempat;
    @FXML private TextField txtNamaTempat;
    @FXML private TextField txtLokasi;
    @FXML private ComboBox<String> cmbJamBuka;
    @FXML private ComboBox<String> cmbJamTutup;

    // Form buttons
    @FXML private Button btnSimpan;
    @FXML private Button btnUbah;
    @FXML private Button btnHapus;
    @FXML private Button btnBatal;

    // Filter fields
    @FXML private TextField txtCari;
    @FXML private ComboBox<String> cmbFilterStatus;
    @FXML private ComboBox<String> cmbFilterGedung;
    @FXML private ComboBox<String> cmbFilterLantai;
    @FXML private ComboBox<String> cmbFilterRuangan;
    @FXML private Button btnTerapkanFilter;
    @FXML private Button btnResetFilter;

    // Table
    @FXML private TableView<Tempat> tabelTempat;
    @FXML private TableColumn<Tempat, String> colId;
    @FXML private TableColumn<Tempat, String> colNama;
    @FXML private TableColumn<Tempat, String> colLokasi;
    @FXML private TableColumn<Tempat, String> colJamBuka;
    @FXML private TableColumn<Tempat, String> colJamTutup;
    @FXML private TableColumn<Tempat, String> colStatus;

    // Pagination
    @FXML private Label lblTotal;
    @FXML private Label lblPage;
    @FXML private Button BtFirstPage;
    @FXML private Button BtPrevPage;
    @FXML private Button BtNextPage;
    @FXML private Button BtLastPage;

    private static final int PAGE_SIZE = 5;
    private int currentPage = 1;
    private int totalPage = 1;

    private final ObservableList<Tempat> masterData = FXCollections.observableArrayList();
    private ObservableList<Tempat> currentFilteredData = FXCollections.observableArrayList();

    // Variabel untuk menyimpan filter terakhir
    private String lastStatus = "Semua";
    private String lastGedung = "Semua";
    private String lastLantai = "Semua";
    private String lastRuangan = "Semua";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFormState(false);
        initJamComboBox();
        initFilterComboBox();
        setupValidationListeners();
        setupSearchListener(); // Tambahkan listener untuk pencarian real-time
        setupTable();
        loadData();
    }

    private void initJamComboBox() {
        ObservableList<String> jamList = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                LocalTime time = LocalTime.of(hour, minute);
                jamList.add(time.format(formatter));
            }
        }

        cmbJamBuka.setItems(jamList);
        cmbJamTutup.setItems(jamList);
        cmbJamBuka.getSelectionModel().select("08:00");
        cmbJamTutup.getSelectionModel().select("16:00");

        String comboStyle = "-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;";
        cmbJamBuka.setStyle(comboStyle);
        cmbJamTutup.setStyle(comboStyle);
    }

    private void initFilterComboBox() {
        // Filter Status
        cmbFilterStatus.setItems(FXCollections.observableArrayList("Semua", "Aktif", "Nonaktif"));
        cmbFilterStatus.getSelectionModel().selectFirst();

        // Filter Gedung
        cmbFilterGedung.setItems(FXCollections.observableArrayList("Semua", "Gedung Astra", "Gedung Dormitory"));
        cmbFilterGedung.getSelectionModel().selectFirst();

        // Filter Lantai - 1 sampai 20
        ObservableList<String> lantaiList = FXCollections.observableArrayList();
        lantaiList.add("Semua");
        for (int i = 1; i <= 20; i++) {
            lantaiList.add("Lantai " + i);
        }
        cmbFilterLantai.setItems(lantaiList);
        cmbFilterLantai.getSelectionModel().selectFirst();

        // Filter Ruangan - 100 sampai 300
        ObservableList<String> ruanganList = FXCollections.observableArrayList();
        ruanganList.add("Semua");
        for (int i = 100; i <= 300; i++) {
            ruanganList.add(String.valueOf(i));
        }
        cmbFilterRuangan.setItems(ruanganList);
        cmbFilterRuangan.getSelectionModel().selectFirst();

        // Tambahkan listener untuk filter (langsung berubah tanpa klik CARI)
        cmbFilterStatus.setOnAction(e -> applyFilters());
        cmbFilterGedung.setOnAction(e -> applyFilters());
        cmbFilterLantai.setOnAction(e -> applyFilters());
        cmbFilterRuangan.setOnAction(e -> applyFilters());
    }

    private void setupSearchListener() {
        // Pencarian real-time - langsung berubah saat mengetik
        txtCari.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void setupValidationListeners() {
        txtNamaTempat.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String filtered = newValue.replaceAll("[^a-zA-Z\\s]", "");
                if (!filtered.equals(newValue)) {
                    txtNamaTempat.setText(filtered);
                    showValidationTooltip(txtNamaTempat, "Nama hanya boleh berisi huruf dan spasi");
                } else {
                    txtNamaTempat.setTooltip(null);
                    txtNamaTempat.setStyle("-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
                }
            }
        });

        txtLokasi.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String filtered = newValue.replaceAll("[^a-zA-Z0-9\\s]", "");
                if (!filtered.equals(newValue)) {
                    txtLokasi.setText(filtered);
                    showValidationTooltip(txtLokasi, "Lokasi hanya boleh berisi huruf, angka, dan spasi");
                } else {
                    txtLokasi.setTooltip(null);
                    txtLokasi.setStyle("-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
                }
            }
        });
    }

    private void showValidationTooltip(TextField textField, String message) {
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #FFE8E8; -fx-text-fill: #C0392B; -fx-font-weight: 600;");
        textField.setTooltip(tooltip);
        textField.setStyle("-fx-background-color: WHITE; -fx-border-color: #C0392B; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    textField.setStyle("-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadData() {
        masterData.clear();
        String sql = "SELECT * FROM tempat ORDER BY tmp_status DESC, tmp_nama ASC";
        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                masterData.add(new Tempat(
                        rs.getString("tmp_id"),
                        rs.getString("tmp_nama"),
                        rs.getString("tmp_lokasi"),
                        rs.getString("tmp_jamBuka"),
                        rs.getString("tmp_jamTutup"),
                        rs.getString("tmp_status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data tempat: " + e.getMessage());
        }
        applyFilters();
        autoGenerateId();
    }

    private void setupTable() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIdTempat()));
        colNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNamaTempat()));
        colLokasi.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLokasi()));
        colJamBuka.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJamBuka()));
        colJamTutup.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJamTutup()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatusTempat()));

        colId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) { setText(null); setStyle(""); return; }
                setText(id);
                setStyle("-fx-text-fill:#1A3A8F;-fx-font-weight:600;");
            }
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setGraphic(null); setText(null); return; }
                Label badge = new Label(val);
                if ("Aktif".equalsIgnoreCase(val)) {
                    badge.setStyle("-fx-background-color:#E0F5E8;-fx-text-fill:#1E8A3C;-fx-font-weight:700;-fx-font-size:11px;-fx-padding:3 10;-fx-background-radius:10;");
                } else {
                    badge.setStyle("-fx-background-color:#FFE8E8;-fx-text-fill:#C0392B;-fx-font-weight:700;-fx-font-size:11px;-fx-padding:3 10;-fx-background-radius:10;");
                }
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void autoGenerateId() {
        String sql = "SELECT dbo.fn_GenerateIdTempat() AS newId";
        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtIdTempat.setText(rs.getString("newId"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal generate ID Tempat: " + e.getMessage());
        }
    }

    private void setFormState(boolean editMode) {
        btnSimpan.setDisable(editMode);
        btnUbah.setDisable(!editMode);
        btnHapus.setDisable(!editMode);
    }

    private boolean validasi() {
        StringBuilder sb = new StringBuilder();
        boolean isValid = true;

        String nama = txtNamaTempat.getText().trim();
        if (nama.isEmpty()) {
            sb.append("• Nama Tempat wajib diisi.\n");
            isValid = false;
        } else if (nama.length() < 3) {
            sb.append("• Nama Tempat minimal 3 karakter.\n");
            isValid = false;
        } else if (!nama.matches("^[a-zA-Z\\s]+$")) {
            sb.append("• Nama Tempat hanya boleh berisi huruf dan spasi.\n");
            isValid = false;
        }

        String lokasi = txtLokasi.getText().trim();
        if (lokasi.isEmpty()) {
            sb.append("• Lokasi wajib diisi.\n");
            isValid = false;
        } else if (!lokasi.matches("^[a-zA-Z0-9\\s]+$")) {
            sb.append("• Lokasi hanya boleh berisi huruf, angka, dan spasi.\n");
            isValid = false;
        }

        String jamBuka = cmbJamBuka.getValue();
        String jamTutup = cmbJamTutup.getValue();

        if (jamBuka == null || jamBuka.isEmpty()) {
            sb.append("• Jam Buka wajib dipilih.\n");
            isValid = false;
        }

        if (jamTutup == null || jamTutup.isEmpty()) {
            sb.append("• Jam Tutup wajib dipilih.\n");
            isValid = false;
        }

        if (jamBuka != null && jamTutup != null && !jamBuka.isEmpty() && !jamTutup.isEmpty()) {
            try {
                LocalTime buka = LocalTime.parse(jamBuka);
                LocalTime tutup = LocalTime.parse(jamTutup);
                if (!buka.isBefore(tutup)) {
                    sb.append("• Jam Buka harus lebih awal dari Jam Tutup.\n");
                    isValid = false;
                }
            } catch (Exception e) {
                sb.append("• Format jam tidak valid.\n");
                isValid = false;
            }
        }

        if (!isValid) {
            showAlert("Validasi Gagal", sb.toString());
        }
        return isValid;
    }

    private String currentUser() {
        return "system";
    }

    @FXML
    void onSimpan(ActionEvent event) {
        if (!validasi()) return;

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_InsertTempat(?,?,?,?,?,?,?)}")) {

            cs.setString(1, txtIdTempat.getText().trim());
            cs.setString(2, txtNamaTempat.getText().trim());
            cs.setString(3, txtLokasi.getText().trim());
            cs.setString(4, cmbJamBuka.getValue());
            cs.setString(5, cmbJamTutup.getValue());
            cs.setString(6, "Aktif");
            cs.setString(7, currentUser());

            cs.execute();
            showAlert("Berhasil", "Data tempat berhasil ditambahkan.");

            loadData();
            onBersih(null);
        } catch (SQLException e) {
            showAlert("Gagal Simpan", "Error: " + e.getMessage());
        }
    }

    @FXML
    void onUbah(ActionEvent event) {
        if (!validasi()) return;

        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tempat SET tmp_nama=?, tmp_lokasi=?, tmp_jamBuka=?, tmp_jamTutup=?, tmp_modifBy=?, tmp_modifDate=GETDATE() WHERE tmp_id=?")) {

            ps.setString(1, txtNamaTempat.getText().trim());
            ps.setString(2, txtLokasi.getText().trim());
            ps.setString(3, cmbJamBuka.getValue());
            ps.setString(4, cmbJamTutup.getValue());
            ps.setString(5, currentUser());
            ps.setString(6, txtIdTempat.getText().trim());

            ps.executeUpdate();
            showAlert("Berhasil", "Data tempat berhasil diubah.");

            loadData();
            onBersih(null);
        } catch (SQLException e) {
            showAlert("Gagal Ubah", "Error: " + e.getMessage());
        }
    }

    @FXML
    void onHapus(ActionEvent event) {
        Tempat dipilih = tabelTempat.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            showAlert("Peringatan", "Silakan pilih data yang akan dihapus.");
            return;
        }

        if ("Nonaktif".equalsIgnoreCase(dipilih.getStatusTempat()) || "Tidak Aktif".equalsIgnoreCase(dipilih.getStatusTempat())) {
            showAlert("Peringatan", "Data tempat ini sudah tidak aktif.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menonaktifkan tempat " + dipilih.getNamaTempat() + "?");
        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tempat SET tmp_status='Nonaktif', tmp_modifBy=?, tmp_modifDate=GETDATE() WHERE tmp_id=?")) {

            ps.setString(1, currentUser());
            ps.setString(2, dipilih.getIdTempat());

            ps.executeUpdate();
            showAlert("Berhasil", "Tempat berhasil dinonaktifkan.");

            loadData();
            onBersih(null);
        } catch (SQLException e) {
            showAlert("Gagal Hapus", "Error: " + e.getMessage());
        }
    }

    @FXML
    void onBersih(ActionEvent event) {
        txtNamaTempat.clear();
        txtLokasi.clear();
        cmbJamBuka.getSelectionModel().select("08:00");
        cmbJamTutup.getSelectionModel().select("16:00");
        setFormState(false);
        tabelTempat.getSelectionModel().clearSelection();
        autoGenerateId();

        txtNamaTempat.setStyle("-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
        txtLokasi.setStyle("-fx-background-color: WHITE; -fx-border-color: #D0D8E8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12;");
        txtNamaTempat.setTooltip(null);
        txtLokasi.setTooltip(null);
    }

    @FXML
    void onTableClick(MouseEvent event) {
        Tempat dipilih = tabelTempat.getSelectionModel().getSelectedItem();
        if (dipilih == null) return;

        txtIdTempat.setText(dipilih.getIdTempat());
        txtNamaTempat.setText(dipilih.getNamaTempat());
        txtLokasi.setText(dipilih.getLokasi());
        cmbJamBuka.getSelectionModel().select(dipilih.getJamBuka());
        cmbJamTutup.getSelectionModel().select(dipilih.getJamTutup());

        setFormState(true);
    }

    // Method untuk menerapkan filter (digunakan oleh real-time search dan tombol CARI)
    private void applyFilters() {
        String keyword = txtCari.getText().trim().toLowerCase();
        String status = cmbFilterStatus.getValue();
        String gedung = cmbFilterGedung.getValue();
        String lantai = cmbFilterLantai.getValue();
        String ruangan = cmbFilterRuangan.getValue();

        // Simpan filter terakhir
        lastStatus = status;
        lastGedung = gedung;
        lastLantai = lantai;
        lastRuangan = ruangan;

        ObservableList<Tempat> filtered = FXCollections.observableArrayList();

        for (Tempat t : masterData) {
            // Pencarian umum di SEMUA field termasuk STATUS
            boolean matchKeyword = keyword.isEmpty() ||
                    t.getIdTempat().toLowerCase().contains(keyword) ||
                    t.getNamaTempat().toLowerCase().contains(keyword) ||
                    t.getLokasi().toLowerCase().contains(keyword) ||
                    t.getJamBuka().toLowerCase().contains(keyword) ||
                    t.getJamTutup().toLowerCase().contains(keyword) ||
                    t.getStatusTempat().toLowerCase().contains(keyword);

            // Filter Status (dari combobox)
            boolean matchStatus = status == null || status.equals("Semua") ||
                    t.getStatusTempat().equalsIgnoreCase(status);

            // Filter Gedung
            boolean matchGedung = true;
            if (gedung != null && !gedung.equals("Semua")) {
                String gedungLower = gedung.toLowerCase();
                if (gedungLower.contains("astra")) {
                    matchGedung = t.getLokasi().toLowerCase().contains("astra") ||
                            t.getNamaTempat().toLowerCase().contains("astra");
                } else if (gedungLower.contains("dormitory")) {
                    matchGedung = t.getLokasi().toLowerCase().contains("dormitory") ||
                            t.getNamaTempat().toLowerCase().contains("dormitory");
                }
            }

            // Filter Lantai
            boolean matchLantai = true;
            if (lantai != null && !lantai.equals("Semua")) {
                String lantaiNumber = lantai.replace("Lantai ", "").trim();
                matchLantai = t.getLokasi().toLowerCase().contains("lantai " + lantaiNumber) ||
                        t.getLokasi().toLowerCase().contains("l " + lantaiNumber) ||
                        t.getLokasi().toLowerCase().contains("lanta " + lantaiNumber);
            }

            // Filter Ruangan
            boolean matchRuangan = true;
            if (ruangan != null && !ruangan.equals("Semua")) {
                matchRuangan = t.getLokasi().toLowerCase().contains("ruangan " + ruangan) ||
                        t.getLokasi().toLowerCase().contains("r " + ruangan) ||
                        t.getLokasi().toLowerCase().contains("r." + ruangan);
            }

            if (matchKeyword && matchStatus && matchGedung && matchLantai && matchRuangan) {
                filtered.add(t);
            }
        }

        // Sorting: Aktif di atas, Nonaktif di bawah
        filtered.sort((t1, t2) -> {
            if (t1.getStatusTempat().equalsIgnoreCase("Aktif") && t2.getStatusTempat().equalsIgnoreCase("Nonaktif")) {
                return -1;
            } else if (t1.getStatusTempat().equalsIgnoreCase("Nonaktif") && t2.getStatusTempat().equalsIgnoreCase("Aktif")) {
                return 1;
            }
            return t1.getNamaTempat().compareTo(t2.getNamaTempat());
        });

        currentFilteredData.setAll(filtered);
        currentPage = 1;
        refreshTable();
    }

    @FXML
    void onTerapkanFilter(ActionEvent event) {
        // Tombol CARI - menerapkan filter dengan nilai saat ini
        applyFilters();
    }

    @FXML
    void onResetFilter(ActionEvent event) {
        txtCari.clear();
        cmbFilterStatus.getSelectionModel().selectFirst();
        cmbFilterGedung.getSelectionModel().selectFirst();
        cmbFilterLantai.getSelectionModel().selectFirst();
        cmbFilterRuangan.getSelectionModel().selectFirst();
        applyFilters();
    }

    private void refreshTable() {
        int total = currentFilteredData.size();
        totalPage = (total == 0) ? 1 : (int) Math.ceil((double) total / PAGE_SIZE);
        if (currentPage > totalPage) currentPage = totalPage;

        int from = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        if (total > 0) {
            tabelTempat.setItems(FXCollections.observableArrayList(currentFilteredData.subList(from, to)));
        } else {
            tabelTempat.setItems(FXCollections.observableArrayList());
        }

        if (lblTotal != null) lblTotal.setText("Total Data : " + total);
        if (lblPage != null) lblPage.setText(currentPage + " / " + totalPage);

        BtFirstPage.setDisable(currentPage <= 1);
        BtPrevPage.setDisable(currentPage <= 1);
        BtNextPage.setDisable(currentPage >= totalPage);
        BtLastPage.setDisable(currentPage >= totalPage);
    }

    @FXML void onFirstPage(ActionEvent event) { currentPage = 1; refreshTable(); }
    @FXML void onPrevPage(ActionEvent event) { if (currentPage > 1) { currentPage--; refreshTable(); } }
    @FXML void onNextPage(ActionEvent event) { if (currentPage < totalPage) { currentPage++; refreshTable(); } }
    @FXML void onLastPage(ActionEvent event) { currentPage = totalPage; refreshTable(); }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
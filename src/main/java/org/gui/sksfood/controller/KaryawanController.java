package org.gui.sksfood.controller;

import org.gui.sksfood.ADT.Karyawan;
import SQL.DBConnect;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class KaryawanController implements Initializable {

    @FXML private HBox menuKaryawan;
    @FXML private HBox menuKembali;
    @FXML private HBox menuSettings;

    @FXML private Label lblTotalKaryawan;
    @FXML private Label lblKaryawanAktif;
    @FXML private Label lblKaryawanNonAktif;

    @FXML private TextField txtIdKaryawan;
    @FXML private TextField txtNamaKaryawan;
    @FXML private TextField txtNoTelp;
    @FXML private ComboBox<String> cmbJabatan;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtStatus;

    @FXML private Button btnSimpan;
    @FXML private Button btnUbah;
    @FXML private Button btnHapus;
    @FXML private Button btnBatal;

    @FXML private TextField txtCari;

    @FXML private TableView<Karyawan> tabelKaryawan;
    @FXML private TableColumn<Karyawan, String> colId;
    @FXML private TableColumn<Karyawan, String> colNama;
    @FXML private TableColumn<Karyawan, String> colNoTelp;
    @FXML private TableColumn<Karyawan, String> colJabatan;
    @FXML private TableColumn<Karyawan, String> colUsername;
    @FXML private TableColumn<Karyawan, String> colStatus;

    @FXML private Label lblTotal;
    @FXML private Label lblPage;

    private final ObservableList<Karyawan> masterData = FXCollections.observableArrayList();

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;
    private int totalPage = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtIdKaryawan.setEditable(false);
        txtStatus.setEditable(false);
        txtStatus.setText("Aktif");

        cmbJabatan.setItems(FXCollections.observableArrayList(
                "Manajer", "Kasir", "Koki", "Pelayan", "Kurir"
        ));

        setupTable();
        setupListeners();
        setFormState(false);

        Platform.runLater(() -> {
            loadData();
            autoGenerateId();
        });
    }

    // ── VALIDASI INPUT REAL-TIME (mengikuti pola KiosController) ─────────
    private void setupListeners() {

        // No. Telepon: hanya angka, maksimal 15 digit
        txtNoTelp.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^0-9]", "");
            if (filtered.length() > 15) filtered = filtered.substring(0, 15);
            if (!filtered.equals(newVal)) txtNoTelp.setText(filtered);
        });

        // Nama Karyawan: hanya huruf, spasi, titik, apostrof, tanda hubung; maksimal 50 karakter
        txtNamaKaryawan.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("[^A-Za-z\\s.'-]", "");
            if (filtered.length() > 50) filtered = filtered.substring(0, 50);
            if (!filtered.equals(newVal)) txtNamaKaryawan.setText(filtered);
        });

        // Username: tidak boleh mengandung spasi, maksimal 20 karakter
        txtUsername.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtered = newVal.replaceAll("\\s", "");
            if (filtered.length() > 20) filtered = filtered.substring(0, 20);
            if (!filtered.equals(newVal)) txtUsername.setText(filtered);
        });

        // Password: maksimal 20 karakter (boleh karakter apa saja)
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 20) txtPassword.setText(newVal.substring(0, 20));
        });
    }

    // ── SETUP TABEL + BADGE WARNA STATUS ─────────────────────────────────
    private void setupTable() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIdKaryawan()));
        colNama.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNamaKaryawan()));
        colNoTelp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNoTlpnKaryawan()));
        colJabatan.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJabatan()));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatusKaryawan()));

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
                badge.setStyle("Aktif".equalsIgnoreCase(val)
                        ? "-fx-background-color:#E0F5E8;-fx-text-fill:#1E8A3C;-fx-font-weight:700;-fx-font-size:11px;-fx-padding:3 10;-fx-background-radius:10;"
                        : "-fx-background-color:#FFE8E8;-fx-text-fill:#C0392B;-fx-font-weight:700;-fx-font-size:11px;-fx-padding:3 10;-fx-background-radius:10;");
                setGraphic(badge);
                setText(null);
            }
        });
    }

    // ── LOAD DATA (sp_GetKaryawan) ────────────────────────────────────────
    private void loadData() {
        try {
            List<Karyawan> list = new ArrayList<>();
            try (Connection conn = new DBConnect().conn;
                 CallableStatement cs = conn.prepareCall("{CALL sp_GetKaryawan}")) {
                ResultSet rs = cs.executeQuery();
                while (rs.next()) list.add(mapRow(rs));
                rs.close();
            }
            masterData.setAll(list);
            currentPage = 1;
            refreshTable();
            refreshStatistik();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Koneksi", "Gagal memuat data.\nDetail: " + e.getMessage());
        }
    }

    private Karyawan mapRow(ResultSet rs) throws SQLException {
        return new Karyawan(
                rs.getString("krw_id"),
                rs.getString("krw_nama"),
                rs.getString("krw_noTlpn"),
                rs.getString("krw_jabatan"),
                rs.getString("krw_username"),
                "",                              // password tidak dikembalikan sp_GetKaryawan
                rs.getString("krw_status")
        );
    }

    // ── STATISTIK (Total / Aktif / Tidak Aktif) ──────────────────────────
    private void refreshStatistik() {
        long aktif = masterData.stream()
                .filter(k -> "Aktif".equalsIgnoreCase(k.getStatusKaryawan()))
                .count();
        long tidakAktif = masterData.size() - aktif;

        lblTotalKaryawan.setText(String.valueOf(masterData.size()));
        lblKaryawanAktif.setText(String.valueOf(aktif));
        lblKaryawanNonAktif.setText(String.valueOf(tidakAktif));
    }

    // ── PAGINATION ─────────────────────────────────────────────────────────
    private void refreshTable() {
        int total = masterData.size();
        totalPage = (total == 0) ? 1 : (int) Math.ceil((double) total / PAGE_SIZE);
        if (currentPage > totalPage) currentPage = totalPage;

        int from = (currentPage - 1) * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, total);

        tabelKaryawan.setItems(FXCollections.observableArrayList(masterData.subList(from, to)));
        lblTotal.setText("Total Data : " + total);
        lblPage.setText(String.valueOf(currentPage));
    }

    @FXML
    public void onFirstPage() { currentPage = 1; refreshTable(); }

    @FXML
    public void onPrevPage() { if (currentPage > 1) { currentPage--; refreshTable(); } }

    @FXML
    public void onNextPage() { if (currentPage < totalPage) { currentPage++; refreshTable(); } }

    @FXML
    public void onLastPage() { currentPage = totalPage; refreshTable(); }

    // ── GENERATE ID OTOMATIS (dihitung di Java dari MAX(krw_id), tidak gantung ke fungsi SQL) ──
    private void autoGenerateId() {
        String prefix = "KR";
        int nextNumber = 1;

        String sql = "SELECT MAX(krw_id) AS maxId FROM karyawan";
        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maxId = rs.getString("maxId");
                if (maxId != null && maxId.length() > prefix.length()) {
                    try {
                        int lastNumber = Integer.parseInt(maxId.substring(prefix.length()));
                        nextNumber = lastNumber + 1;
                    } catch (NumberFormatException ignore) {
                        // format id tidak sesuai pola KRxxx, biarkan mulai dari 1
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Generate ID",
                    "Gagal mengambil ID terakhir dari tabel karyawan.\nDetail: " + e.getMessage());
        }

        txtIdKaryawan.setText(prefix + String.format("%03d", nextNumber));
    }

    // ── AMBIL PASSWORD LAMA (dipakai saat UBAH tanpa ganti password) ────
    private String ambilPasswordLama(String idKaryawan) {
        String sql = "SELECT krw_password FROM karyawan WHERE krw_id = ?";
        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idKaryawan);
            ResultSet rs = ps.executeQuery();
            String password = null;
            if (rs.next()) password = rs.getString("krw_password");
            rs.close();
            return password;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengambil password lama: " + e.getMessage());
            return null;
        }
    }

    // ── FORM STATE (mengikuti pola KiosController) ───────────────────────
    // editMode = true  → sedang mengedit baris terpilih  → SIMPAN mati, UBAH/HAPUS hidup
    // editMode = false → mode input baru                  → SIMPAN hidup, UBAH/HAPUS mati
    private void setFormState(boolean editMode) {
        btnSimpan.setDisable(editMode);
        btnUbah.setDisable(!editMode);
        btnHapus.setDisable(!editMode);
    }

    // ── VALIDASI FORM (kumpulkan semua kesalahan sekaligus) ──────────────
    private boolean validasi(boolean modeTambah) {
        StringBuilder sb = new StringBuilder();

        String nama = txtNamaKaryawan.getText().trim();
        if (nama.isEmpty()) {
            sb.append("• Nama Karyawan wajib diisi.\n");
        } else if (nama.length() < 3) {
            sb.append("• Nama Karyawan minimal 3 karakter.\n");
        } else if (!nama.matches("^[A-Za-z\\s.'-]+$")) {
            sb.append("• Nama Karyawan hanya boleh huruf dan spasi.\n");
        }

        String noTelp = txtNoTelp.getText().trim();
        if (noTelp.isEmpty()) {
            sb.append("• No. Telepon wajib diisi.\n");
        } else if (!noTelp.matches("^0[0-9]{9,13}$")) {
            sb.append("• No. Telepon harus angka, diawali 0, dan panjang 10-14 digit.\n");
        }

        if (cmbJabatan.getValue() == null) {
            sb.append("• Jabatan wajib dipilih.\n");
        }

        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            sb.append("• Username wajib diisi.\n");
        } else if (username.length() < 4) {
            sb.append("• Username minimal 4 karakter.\n");
        } else if (username.contains(" ")) {
            sb.append("• Username tidak boleh mengandung spasi.\n");
        }

        String password = txtPassword.getText();
        if (modeTambah && password.isEmpty()) {
            sb.append("• Password wajib diisi untuk data baru.\n");
        } else if (!password.isEmpty() && password.length() < 3) {
            sb.append("• Password minimal 3 karakter.\n");
        }

        if (sb.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", sb.toString());
            return false;
        }
        return true;
    }

    // ── TAMBAH / SIMPAN (sp_InsertKaryawan) ──────────────────────────────
    @FXML
    public void onSimpan() {
        if (!validasi(true)) return;

        Karyawan k = new Karyawan(
                txtIdKaryawan.getText().trim(),
                txtNamaKaryawan.getText().trim(),
                txtNoTelp.getText().trim(),
                cmbJabatan.getValue(),
                txtUsername.getText().trim(),
                txtPassword.getText(),
                "Aktif"   // status selalu Aktif untuk data baru
        );

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_InsertKaryawan(?,?,?,?,?,?,?,?)}")) {
            cs.setString(1, k.getIdKaryawan());
            cs.setString(2, k.getNamaKaryawan());
            cs.setString(3, k.getNoTlpnKaryawan());
            cs.setString(4, k.getJabatan());
            cs.setString(5, k.getUsername());
            cs.setString(6, k.getPassword());
            cs.setString(7, k.getStatusKaryawan());
            cs.setString(8, currentUser());   // krw_createBy (wajib diisi, tidak boleh null)
            cs.execute();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data karyawan berhasil ditambahkan.");
            loadData();
            onBersih();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Simpan", "Error: " + e.getMessage());
        }
    }

    /** Ambil identitas pengguna untuk kolom krw_createBy / krw_modifBy. */
    private String currentUser() {
        return "system";
        // Kalau nanti sudah ada mekanisme login yang menyimpan siapa yang sedang
        // login (misal lewat class Session), ganti baris di atas jadi:
        //   Karyawan login = org.gui.sksfood.Session.getLoggedInKaryawan();
        //   return (login != null && login.getUsername() != null) ? login.getUsername() : "system";
    }

    // ── UBAH (sp_UpdateKaryawan) ──────────────────────────────────────────
    @FXML
    public void onUbah() {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data karyawan pada tabel terlebih dahulu.");
            return;
        }
        if (!validasi(false)) return;

        String passwordFinal;
        if (txtPassword.getText().isEmpty()) {
            passwordFinal = ambilPasswordLama(txtIdKaryawan.getText().trim());
            if (passwordFinal == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan",
                        "Gagal mempertahankan password lama, silakan isi password baru.");
                return;
            }
        } else {
            passwordFinal = txtPassword.getText();
        }

        Karyawan k = new Karyawan(
                txtIdKaryawan.getText().trim(),
                txtNamaKaryawan.getText().trim(),
                txtNoTelp.getText().trim(),
                cmbJabatan.getValue(),
                txtUsername.getText().trim(),
                passwordFinal,
                txtStatus.getText()   // status tidak diubah lewat form, cuma ikut nilai yang lama
        );

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_UpdateKaryawan(?,?,?,?,?,?,?,?)}")) {
            cs.setString(1, k.getIdKaryawan());
            cs.setString(2, k.getNamaKaryawan());
            cs.setString(3, k.getNoTlpnKaryawan());
            cs.setString(4, k.getJabatan());
            cs.setString(5, k.getUsername());
            cs.setString(6, k.getPassword());
            cs.setString(7, k.getStatusKaryawan());
            cs.setString(8, currentUser());   // krw_modifBy
            cs.execute();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data karyawan berhasil diubah.");
            loadData();
            onBersih();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Ubah", "Error: " + e.getMessage());
        }
    }

    // ── HAPUS (soft-delete: status jadi "Tidak Aktif" via sp_UpdateKaryawan) ─
    @FXML
    public void onHapus() {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data karyawan pada tabel terlebih dahulu.");
            return;
        }

        if ("Tidak Aktif".equalsIgnoreCase(dipilih.getStatusKaryawan())) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Data karyawan ini sudah tidak aktif.");
            return;
        }

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Nonaktifkan");
        konfirmasi.setHeaderText("Nonaktifkan Karyawan");
        konfirmasi.setContentText("Data \"" + dipilih.getNamaKaryawan() + "\" akan diubah statusnya menjadi Tidak Aktif.\nLanjutkan?");
        if (txtIdKaryawan.getScene() != null) {
            konfirmasi.initOwner(txtIdKaryawan.getScene().getWindow());
        }

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isEmpty() || hasil.get() != ButtonType.OK) return;

        String passwordLama = ambilPasswordLama(dipilih.getIdKaryawan());
        if (passwordLama == null) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengambil data karyawan untuk dinonaktifkan.");
            return;
        }

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_UpdateKaryawan(?,?,?,?,?,?,?,?)}")) {
            cs.setString(1, dipilih.getIdKaryawan());
            cs.setString(2, dipilih.getNamaKaryawan());
            cs.setString(3, dipilih.getNoTlpnKaryawan());
            cs.setString(4, dipilih.getJabatan());
            cs.setString(5, dipilih.getUsername());
            cs.setString(6, passwordLama);
            cs.setString(7, "Tidak Aktif");
            cs.setString(8, currentUser());   // krw_modifBy
            cs.execute();

            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Karyawan berhasil dinonaktifkan.");
            loadData();
            onBersih();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Nonaktifkan", "Error: " + e.getMessage());
        }
    }

    // ── CARI (fn_CariKaryawanByNama) ─────────────────────────────────────
    @FXML
    public void onCari() {
        String keyword = txtCari.getText() == null ? "" : txtCari.getText().trim();

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            List<Karyawan> hasil = new ArrayList<>();
            try (Connection conn = new DBConnect().conn;
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM fn_CariKaryawanByNama(?)")) {
                ps.setString(1, keyword);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) hasil.add(mapRow(rs));
                rs.close();
            }
            masterData.setAll(hasil);
            currentPage = 1;
            refreshTable();
            refreshStatistik();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Cari", "Error: " + e.getMessage());
        }
    }

    // ── BATAL / RESET FORM ────────────────────────────────────────────────
    @FXML
    public void onBersih() {
        txtNamaKaryawan.clear();
        txtNoTelp.clear();
        cmbJabatan.getSelectionModel().clearSelection();
        txtUsername.clear();
        txtPassword.clear();
        txtStatus.setText("Aktif");
        txtCari.clear();
        tabelKaryawan.getSelectionModel().clearSelection();

        setFormState(false);
        autoGenerateId();
    }

    // ── KLIK BARIS TABEL → ISI FORM (mode UBAH) ──────────────────────────
    @FXML
    public void onTableClick(MouseEvent event) {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) return;

        txtIdKaryawan.setText(dipilih.getIdKaryawan());
        txtNamaKaryawan.setText(dipilih.getNamaKaryawan());
        txtNoTelp.setText(dipilih.getNoTlpnKaryawan());
        cmbJabatan.setValue(dipilih.getJabatan());
        txtUsername.setText(dipilih.getUsername());
        txtPassword.clear(); // kosong = "tidak diubah", password lama dipertahankan otomatis
        txtStatus.setText(dipilih.getStatusKaryawan());

        setFormState(true);
    }

    // ── ALERT HELPER (tidak menutup halaman yang sedang dibuka) ──────────
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Runnable show = () -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            if (txtIdKaryawan != null && txtIdKaryawan.getScene() != null) {
                alert.initOwner(txtIdKaryawan.getScene().getWindow());
            }
            alert.showAndWait();
        };
        if (Platform.isFxApplicationThread()) show.run();
        else Platform.runLater(show);
    }
}

package org.gui.sksfood.controller;

import org.gui.sksfood.ADT.Karyawan;
import SQL.DBConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    @FXML private ComboBox<String> cmbStatus;

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
    private final ObservableList<Karyawan> tampilData  = FXCollections.observableArrayList();

    private static final int ROWS_PER_PAGE = 10;
    private int halamanSekarang = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbJabatan.setItems(FXCollections.observableArrayList(
                "Manajer", "Kasir", "Koki", "Pelayan", "Kurir"
        ));
        cmbStatus.setItems(FXCollections.observableArrayList(
                "Aktif", "Tidak Aktif"
        ));

        colId.setCellValueFactory(new PropertyValueFactory<>("idKaryawan"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaKaryawan"));
        colNoTelp.setCellValueFactory(new PropertyValueFactory<>("noTlpnKaryawan"));
        colJabatan.setCellValueFactory(new PropertyValueFactory<>("jabatan"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusKaryawan"));

        tabelKaryawan.setItems(tampilData);

        loadData();
    }

    // ── LOAD DATA (sp_GetKaryawan) ───────────────────────────────────────
    private void loadData() {
        masterData.clear();
        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_GetKaryawan}")) {
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                masterData.add(mapRow(rs));
            }
            rs.close();
        } catch (SQLException e) {
            tampilkanError("Gagal mengambil data karyawan (cek nama kolom di sp_GetKaryawan): " + e.getMessage(), e);
            return; // hentikan supaya tidak lanjut ke refresh dengan data kosong/setengah
        }
        halamanSekarang = 1;
        refreshTampilan();
        refreshStatistik();
    }

    private Karyawan mapRow(ResultSet rs) throws SQLException {
        return new Karyawan(
                rs.getString("krw_id"),
                rs.getString("krw_nama"),
                rs.getString("krw_noTlpn"),
                rs.getString("krw_jabatan"),
                rs.getString("krw_username"),
                "",                          // password sengaja tidak diambil, SP tidak mengembalikannya
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

    // ── PAGINATION ────────────────────────────────────────────────────────
    private void refreshTampilan() {
        tampilData.clear();

        int totalData = masterData.size();
        int totalHalaman = Math.max(1, (int) Math.ceil(totalData / (double) ROWS_PER_PAGE));
        if (halamanSekarang > totalHalaman) halamanSekarang = totalHalaman;
        if (halamanSekarang < 1) halamanSekarang = 1;

        int mulai = (halamanSekarang - 1) * ROWS_PER_PAGE;
        int akhir  = Math.min(mulai + ROWS_PER_PAGE, totalData);

        for (int i = mulai; i < akhir; i++) {
            tampilData.add(masterData.get(i));
        }

        lblTotal.setText("Total Data : " + totalData);
        lblPage.setText(String.valueOf(halamanSekarang));
    }

    @FXML
    public void onFirstPage() {
        halamanSekarang = 1;
        refreshTampilan();
    }

    @FXML
    public void onPrevPage() {
        if (halamanSekarang > 1) halamanSekarang--;
        refreshTampilan();
    }

    @FXML
    public void onNextPage() {
        int totalHalaman = Math.max(1, (int) Math.ceil(masterData.size() / (double) ROWS_PER_PAGE));
        if (halamanSekarang < totalHalaman) halamanSekarang++;
        refreshTampilan();
    }

    @FXML
    public void onLastPage() {
        halamanSekarang = Math.max(1, (int) Math.ceil(masterData.size() / (double) ROWS_PER_PAGE));
        refreshTampilan();
    }

    // ── TAMBAH / SIMPAN (sp_InsertKaryawan + fn_GeneratedIdKaryawan) ─────
    @FXML
    public void onSimpan() {
        if (!validasiForm(true)) return;

        String idBaru = generateId();
        if (idBaru == null) return;

        Karyawan k = new Karyawan(
                idBaru,
                txtNamaKaryawan.getText().trim(),
                txtNoTelp.getText().trim(),
                cmbJabatan.getValue(),
                txtUsername.getText().trim(),
                txtPassword.getText(),
                cmbStatus.getValue()
        );

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_InsertKaryawan(?,?,?,?,?,?,?)}")) {
            cs.setString(1, k.getIdKaryawan());
            cs.setString(2, k.getNamaKaryawan());
            cs.setString(3, k.getNoTlpnKaryawan());
            cs.setString(4, k.getJabatan());
            cs.setString(5, k.getUsername());
            cs.setString(6, k.getPassword());
            cs.setString(7, k.getStatusKaryawan());
            cs.executeUpdate();

            tampilkanInfo("Data karyawan berhasil ditambahkan.");
            onBersih();
            loadData();
        } catch (SQLException e) {
            tampilkanError("Gagal menyimpan data karyawan", e);
        }
    }

    private String generateId() {
        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{? = CALL fn_GeneratedIdKaryawan()}")) {
            // Jika fn_GeneratedIdKaryawan dipanggil sebagai scalar function biasa,
            // gunakan query berikut sebagai alternatif:
            // "SELECT dbo.fn_GeneratedIdKaryawan() AS Id_Karyawan"
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT dbo.fn_GeneratedIdKaryawan() AS Id_Karyawan")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String id = rs.getString("Id_Karyawan");
                    rs.close();
                    return id;
                }
                rs.close();
            }
        } catch (SQLException e) {
            tampilkanError("Gagal generate ID karyawan", e);
        }
        return null;
    }

    // ── UBAH (sp_UpdateKaryawan) ──────────────────────────────────────────
    @FXML
    public void onUbah() {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            tampilkanPeringatan("Pilih data karyawan pada tabel terlebih dahulu.");
            return;
        }
        if (!validasiForm(false)) return;

        Karyawan k = new Karyawan(
                txtIdKaryawan.getText().trim(),
                txtNamaKaryawan.getText().trim(),
                txtNoTelp.getText().trim(),
                cmbJabatan.getValue(),
                txtUsername.getText().trim(),
                txtPassword.getText(),
                cmbStatus.getValue()
        );

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_UpdateKaryawan(?,?,?,?,?,?,?)}")) {
            cs.setString(1, k.getIdKaryawan());
            cs.setString(2, k.getNamaKaryawan());
            cs.setString(3, k.getNoTlpnKaryawan());
            cs.setString(4, k.getJabatan());
            cs.setString(5, k.getUsername());
            cs.setString(6, k.getPassword());
            cs.setString(7, k.getStatusKaryawan());
            cs.executeUpdate();

            tampilkanInfo("Data karyawan berhasil diubah.");
            onBersih();
            loadData();
        } catch (SQLException e) {
            tampilkanError("Gagal mengubah data karyawan", e);
        }
    }

    // ── HAPUS (sp_DeleteKaryawan_ById) ───────────────────────────────────
    @FXML
    public void onHapus() {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            tampilkanPeringatan("Pilih data karyawan pada tabel terlebih dahulu.");
            return;
        }

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText(null);
        konfirmasi.setContentText("Yakin ingin menghapus data \"" + dipilih.getNamaKaryawan() + "\"?");
        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isEmpty() || hasil.get() != ButtonType.OK) return;

        try (Connection conn = new DBConnect().conn;
             CallableStatement cs = conn.prepareCall("{CALL sp_DeleteKaryawan_ById(?)}")) {
            cs.setString(1, dipilih.getIdKaryawan());
            cs.executeUpdate();

            tampilkanInfo("Data karyawan berhasil dihapus.");
            onBersih();
            loadData();
        } catch (SQLException e) {
            tampilkanError("Gagal menghapus data karyawan", e);
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

        List<Karyawan> hasil = new ArrayList<>();
        try (Connection conn = new DBConnect().conn;
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM fn_CariKaryawanByNama(?)")) {
            ps.setString(1, keyword);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hasil.add(mapRow(rs));
            }
            rs.close();
        } catch (SQLException e) {
            tampilkanError("Gagal mencari data karyawan", e);
            return;
        }

        masterData.setAll(hasil);
        halamanSekarang = 1;
        refreshTampilan();
        refreshStatistik();
    }

    // ── BATAL / RESET FORM ────────────────────────────────────────────────
    @FXML
    public void onBersih() {
        txtIdKaryawan.clear();
        txtNamaKaryawan.clear();
        txtNoTelp.clear();
        cmbJabatan.getSelectionModel().clearSelection();
        txtUsername.clear();
        txtPassword.clear();
        cmbStatus.getSelectionModel().clearSelection();
        txtCari.clear();
        tabelKaryawan.getSelectionModel().clearSelection();
    }

    // ── KLIK BARIS TABEL → ISI FORM ───────────────────────────────────────
    @FXML
    public void onTableClick(MouseEvent event) {
        Karyawan dipilih = tabelKaryawan.getSelectionModel().getSelectedItem();
        if (dipilih == null) return;

        txtIdKaryawan.setText(dipilih.getIdKaryawan());
        txtNamaKaryawan.setText(dipilih.getNamaKaryawan());
        txtNoTelp.setText(dipilih.getNoTlpnKaryawan());
        cmbJabatan.setValue(dipilih.getJabatan());
        txtUsername.setText(dipilih.getUsername());
        txtPassword.setText(dipilih.getPassword());
        cmbStatus.setValue(dipilih.getStatusKaryawan());
    }

    // ── VALIDASI FORM ─────────────────────────────────────────────────────
    private boolean validasiForm(boolean modeTambah) {
        if (txtNamaKaryawan.getText().trim().isEmpty()
                || txtNoTelp.getText().trim().isEmpty()
                || cmbJabatan.getValue() == null
                || txtUsername.getText().trim().isEmpty()
                || cmbStatus.getValue() == null) {
            tampilkanPeringatan("Semua field wajib diisi.");
            return false;
        }
        if (modeTambah && txtPassword.getText().isEmpty()) {
            tampilkanPeringatan("Password wajib diisi untuk data baru.");
            return false;
        }
        return true;
    }

    // ── ALERT HELPER ──────────────────────────────────────────────────────
    private void tampilkanInfo(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

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
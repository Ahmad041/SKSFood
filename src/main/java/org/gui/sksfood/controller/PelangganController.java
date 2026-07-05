package org.gui.sksfood.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gui.sksfood.ADT.Pelanggan;
import SQL.DBConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PelangganController implements Initializable {

    // ================= Menu (sidebar) =================
    @FXML
    private javafx.scene.layout.HBox menuKaryawan;
    @FXML
    private javafx.scene.layout.HBox menuKembali;
    @FXML
    private javafx.scene.layout.HBox menuSettings;

    // ================= Statistik =================
    @FXML
    private Label lblTotalPelanggan;
    @FXML
    private Label lblPelangganAktif;
    @FXML
    private Label lblPelangganNonAktif;

    // ================= Form Input =================
    @FXML
    private TextField txtIdPelanggan;
    @FXML
    private TextField txtNamaPelanggan;
    @FXML
    private TextField txtNoTelp;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbDepartemen;
    @FXML
    private ComboBox<String> cmbStatusAkun;

    // ================= Tombol Aksi =================
    @FXML
    private Button btnSimpan;
    @FXML
    private Button btnUbah;
    @FXML
    private Button btnHapus;
    @FXML
    private Button btnBatal;
    @FXML
    private Button btnCari;

    // ================= Pencarian =================
    @FXML
    private TextField txtCari;

    // ================= Tabel =================
    @FXML
    private TableView<Pelanggan> tabelPelanggan;
    @FXML
    private TableColumn<Pelanggan, String> colId;
    @FXML
    private TableColumn<Pelanggan, String> colNama;
    @FXML
    private TableColumn<Pelanggan, String> colNoTelp;
    @FXML
    private TableColumn<Pelanggan, String> colEmail;
    @FXML
    private TableColumn<Pelanggan, String> colPassword;
    @FXML
    private TableColumn<Pelanggan, String> colDepartemen;
    @FXML
    private TableColumn<Pelanggan, String> colStatusAkun;

    // ================= Pagination =================
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPage;

    // ================= Konstanta & State =================
    private static final String PREFIX_ID = "PLG";
    private static final int PANJANG_ID = 4; // PLG0001 -> 4 digit angka
    private static final int MAX_DIGIT_TELP = 13;
    private static final int PANJANG_PASSWORD = 8;
    private static final int ROW_PER_PAGE = 10;

    private final ObservableList<Pelanggan> masterData = FXCollections.observableArrayList();
    private ObservableList<Pelanggan> filteredData = FXCollections.observableArrayList();

    private int currentPage = 1;
    private int totalPage = 1;

    private final List<String> daftarDepartemen = List.of(
            "Badan Kepengurusan Harian",
            "Departemen Acara",
            "Departemen Big Circle",
            "Departemen Minat Bakat",
            "Departemen Pendidikan",
            "Departemen PHD",
            "Departemen PSDM-O"
    );

    private final List<String> daftarStatusAkun = List.of(
            "Aktif",
            "Non Aktif"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBox();
        setupNoTelpFormatter();
        setupTabel();
        muatDataDariDatabase();
        tampilkanHalaman(1);
        perbaruiStatistik();
        generateIdBaru();
    }

    // =====================================================================
    // SETUP AWAL
    // =====================================================================

    private void setupComboBox() {
        cmbDepartemen.setItems(FXCollections.observableArrayList(daftarDepartemen));
        cmbStatusAkun.setItems(FXCollections.observableArrayList(daftarStatusAkun));
        cmbStatusAkun.getSelectionModel().select("Aktif");
    }

    /**
     * No. Telepon: hanya angka, maksimal 13 digit.
     */
    private void setupNoTelpFormatter() {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            if (!newText.matches("\\d{0," + MAX_DIGIT_TELP + "}")) {
                return null; // tolak perubahan (huruf/simbol atau lebih dari 13 digit)
            }
            return change;
        });
        txtNoTelp.setTextFormatter(formatter);
    }

    private void setupTabel() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNoTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colDepartemen.setCellValueFactory(new PropertyValueFactory<>("departemen"));
        colStatusAkun.setCellValueFactory(new PropertyValueFactory<>("statusAkun"));
    }

    // =====================================================================
    // AMBIL DATA DARI DATABASE
    // =====================================================================

    private void muatDataDariDatabase() {
        masterData.clear();
        String sql = "SELECT plg_id, plg_nama, plg_noTlpn, plg_email, plg_password, "
                + "plg_departemen, plg_statusAkun FROM pelanggan ORDER BY plg_id ASC";

        try (Connection conn = bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pelanggan p = new Pelanggan(
                        rs.getString("plg_id"),
                        rs.getString("plg_nama"),
                        rs.getString("plg_noTlpn"),
                        rs.getString("plg_email"),
                        rs.getString("plg_password"),
                        rs.getString("plg_departemen"),
                        rs.getString("plg_statusAkun")
                );
                masterData.add(p);
            }
        } catch (SQLException e) {
            tampilkanPeringatan("Gagal memuat data pelanggan: " + e.getMessage());
        }

        filteredData = FXCollections.observableArrayList(masterData);
    }

    // =====================================================================
    // GENERATE ID OTOMATIS (PLG0001, PLG0002, ...)
    // =====================================================================

    private void generateIdBaru() {
        // Memakai function SQL Server fn_GenerateIdPelanggan() yang sudah dibuat di database,
        // supaya logic generate ID hanya ada di satu tempat (di DB), bukan diduplikasi di Java.
        String sql = "SELECT dbo.fn_GenerateIdPelanggan() AS IdBaru";
        String idBaru = PREFIX_ID + String.format("%0" + PANJANG_ID + "d", 1);

        try (Connection conn = bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                idBaru = rs.getString("IdBaru");
            }
        } catch (SQLException e) {
            tampilkanPeringatan("Gagal membuat ID pelanggan otomatis: " + e.getMessage());
        }

        txtIdPelanggan.setText(idBaru);
    }

    // =====================================================================
    // VALIDASI FORM
    // =====================================================================

    private boolean validasiForm() {
        StringBuilder pesan = new StringBuilder();

        if (txtNamaPelanggan.getText() == null || txtNamaPelanggan.getText().trim().isEmpty()) {
            pesan.append("- Nama pelanggan wajib diisi.\n");
        }

        String noTelp = txtNoTelp.getText();
        if (noTelp == null || noTelp.trim().isEmpty()) {
            pesan.append("- No. Telepon wajib diisi.\n");
        } else if (!noTelp.matches("\\d{1," + MAX_DIGIT_TELP + "}")) {
            pesan.append("- No. Telepon hanya boleh berisi angka, maksimal ")
                    .append(MAX_DIGIT_TELP).append(" digit.\n");
        }

        String email = txtEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            pesan.append("- Email wajib diisi.\n");
        } else if (!isEmailGmailValid(email)) {
            pesan.append("- Email harus menggunakan format ...@gmail.com\n");
        }

        String password = txtPassword.getText();
        if (password == null || password.isEmpty()) {
            pesan.append("- Password wajib diisi.\n");
        } else if (password.length() != PANJANG_PASSWORD) {
            pesan.append("- Password harus berisi tepat ").append(PANJANG_PASSWORD).append(" karakter.\n");
        }

        if (cmbDepartemen.getValue() == null) {
            pesan.append("- Departemen wajib dipilih.\n");
        }

        if (cmbStatusAkun.getValue() == null) {
            pesan.append("- Status akun wajib dipilih.\n");
        }

        if (pesan.length() > 0) {
            tampilkanPeringatan(pesan.toString());
            return false;
        }
        return true;
    }

    private boolean isEmailGmailValid(String email) {
        // Format umum email + wajib diakhiri @gmail.com
        String regex = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        return Pattern.matches(regex, email.trim());
    }

    // =====================================================================
    // CRUD - SIMPAN
    // =====================================================================

    @FXML
    private void onSimpan(ActionEvent event) {
        if (!validasiForm()) {
            return;
        }

        // plg_createBy & plg_createDate wajib diisi (NOT NULL di tabel).
        String sql = "INSERT INTO pelanggan "
                + "(plg_id, plg_nama, plg_noTlpn, plg_email, plg_password, plg_departemen, plg_statusAkun, "
                + "plg_createBy, plg_createDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtIdPelanggan.getText());
            ps.setString(2, txtNamaPelanggan.getText().trim());
            ps.setString(3, txtNoTelp.getText().trim());
            ps.setString(4, txtEmail.getText().trim());
            ps.setString(5, txtPassword.getText());
            ps.setString(6, cmbDepartemen.getValue());
            ps.setString(7, cmbStatusAkun.getValue());
            ps.setString(8, getUserLoginSaatIni());

            ps.executeUpdate();

            tampilkanInfo("Data pelanggan berhasil disimpan.");
            muatUlangSemua();
            bersihkanForm();

        } catch (SQLException e) {
            tampilkanPeringatan("Gagal menyimpan data pelanggan: " + e.getMessage());
        }
    }

    // =====================================================================
    // CRUD - UBAH
    // =====================================================================

    @FXML
    private void onUbah(ActionEvent event) {
        if (txtIdPelanggan.getText() == null || txtIdPelanggan.getText().isEmpty()) {
            tampilkanPeringatan("Pilih data pelanggan yang ingin diubah terlebih dahulu.");
            return;
        }

        if (!validasiForm()) {
            return;
        }

        // plg_modifBy & plg_modifDate ikut diupdate setiap kali data diubah.
        String sql = "UPDATE pelanggan SET plg_nama = ?, plg_noTlpn = ?, plg_email = ?, "
                + "plg_password = ?, plg_departemen = ?, plg_statusAkun = ?, "
                + "plg_modifBy = ?, plg_modifDate = GETDATE() WHERE plg_id = ?";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNamaPelanggan.getText().trim());
            ps.setString(2, txtNoTelp.getText().trim());
            ps.setString(3, txtEmail.getText().trim());
            ps.setString(4, txtPassword.getText());
            ps.setString(5, cmbDepartemen.getValue());
            ps.setString(6, cmbStatusAkun.getValue());
            ps.setString(7, getUserLoginSaatIni());
            ps.setString(8, txtIdPelanggan.getText());

            int baris = ps.executeUpdate();

            if (baris > 0) {
                tampilkanInfo("Data pelanggan berhasil diubah.");
                muatUlangSemua();
                bersihkanForm();
            } else {
                tampilkanPeringatan("Data pelanggan tidak ditemukan.");
            }

        } catch (SQLException e) {
            tampilkanPeringatan("Gagal mengubah data pelanggan: " + e.getMessage());
        }
    }

    // =====================================================================
    // CRUD - HAPUS
    // =====================================================================

    @FXML
    private void onHapus(ActionEvent event) {
        if (txtIdPelanggan.getText() == null || txtIdPelanggan.getText().isEmpty()) {
            tampilkanPeringatan("Pilih data pelanggan yang ingin dihapus terlebih dahulu.");
            return;
        }

        Alert konfirmasi = new Alert(AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText(null);
        konfirmasi.setContentText("Apakah Anda yakin ingin menghapus data pelanggan "
                + txtIdPelanggan.getText() + "?");

        konfirmasi.showAndWait().ifPresent(respon -> {
            if (respon.getButtonData().isDefaultButton()) {
                hapusDariDatabase(txtIdPelanggan.getText());
            }
        });
    }

    private void hapusDariDatabase(String id) {
        String sql = "DELETE FROM pelanggan WHERE plg_id = ?";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            int baris = ps.executeUpdate();

            if (baris > 0) {
                tampilkanInfo("Data pelanggan berhasil dihapus.");
                muatUlangSemua();
                bersihkanForm();
            } else {
                tampilkanPeringatan("Data pelanggan tidak ditemukan.");
            }

        } catch (SQLException e) {
            tampilkanPeringatan("Gagal menghapus data pelanggan: " + e.getMessage());
        }
    }

    // =====================================================================
    // BATAL / BERSIHKAN FORM
    // =====================================================================

    @FXML
    private void onBersih(ActionEvent event) {
        bersihkanForm();
    }

    private void bersihkanForm() {
        txtNamaPelanggan.clear();
        txtNoTelp.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbDepartemen.getSelectionModel().clearSelection();
        cmbStatusAkun.getSelectionModel().select("Aktif");
        tabelPelanggan.getSelectionModel().clearSelection();
        generateIdBaru();
    }

    // =====================================================================
    // PENCARIAN
    // =====================================================================

    @FXML
    private void onCari(ActionEvent event) {
        String kataKunci = txtCari.getText() == null ? "" : txtCari.getText().trim().toLowerCase();

        if (kataKunci.isEmpty()) {
            filteredData = FXCollections.observableArrayList(masterData);
        } else {
            List<Pelanggan> hasil = new ArrayList<>();
            for (Pelanggan p : masterData) {
                if (p.getId().toLowerCase().contains(kataKunci)
                        || p.getNama().toLowerCase().contains(kataKunci)
                        || (p.getEmail() != null && p.getEmail().toLowerCase().contains(kataKunci))) {
                    hasil.add(p);
                }
            }
            filteredData = FXCollections.observableArrayList(hasil);
        }

        tampilkanHalaman(1);
    }

    // =====================================================================
    // KLIK BARIS TABEL -> ISI FORM
    // =====================================================================

    @FXML
    private void onTableClick(MouseEvent event) {
        Pelanggan dipilih = tabelPelanggan.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            return;
        }

        txtIdPelanggan.setText(dipilih.getId());
        txtNamaPelanggan.setText(dipilih.getNama());
        txtNoTelp.setText(dipilih.getNoTelp());
        txtEmail.setText(dipilih.getEmail());
        txtPassword.setText(dipilih.getPassword());
        cmbDepartemen.setValue(dipilih.getDepartemen());
        cmbStatusAkun.setValue(dipilih.getStatusAkun());
    }

    // =====================================================================
    // PAGINATION
    // =====================================================================

    @FXML
    private void onFirstPage(ActionEvent event) {
        tampilkanHalaman(1);
    }

    @FXML
    private void onPrevPage(ActionEvent event) {
        if (currentPage > 1) {
            tampilkanHalaman(currentPage - 1);
        }
    }

    @FXML
    private void onNextPage(ActionEvent event) {
        if (currentPage < totalPage) {
            tampilkanHalaman(currentPage + 1);
        }
    }

    @FXML
    private void onLastPage(ActionEvent event) {
        tampilkanHalaman(totalPage);
    }

    private void tampilkanHalaman(int halaman) {
        int totalData = filteredData.size();
        totalPage = Math.max(1, (int) Math.ceil((double) totalData / ROW_PER_PAGE));

        if (halaman < 1) {
            halaman = 1;
        }
        if (halaman > totalPage) {
            halaman = totalPage;
        }
        currentPage = halaman;

        int start = (currentPage - 1) * ROW_PER_PAGE;
        int end = Math.min(start + ROW_PER_PAGE, totalData);

        ObservableList<Pelanggan> halamanData = FXCollections.observableArrayList();
        if (start < totalData) {
            halamanData.addAll(filteredData.subList(start, end));
        }

        tabelPelanggan.setItems(halamanData);
        lblPage.setText(String.valueOf(currentPage));
        lblTotal.setText("Total Data : " + totalData);
    }

    // =====================================================================
    // STATISTIK
    // =====================================================================

    private void perbaruiStatistik() {
        int total = masterData.size();
        long aktif = masterData.stream()
                .filter(p -> "Aktif".equalsIgnoreCase(p.getStatusAkun()))
                .count();
        long nonAktif = total - aktif;

        lblTotalPelanggan.setText(String.valueOf(total));
        lblPelangganAktif.setText(String.valueOf(aktif));
        lblPelangganNonAktif.setText(String.valueOf(nonAktif));
    }

    // =====================================================================
    // UTIL
    // =====================================================================

    /**
     * Membuka koneksi database melalui SQL.DBConnect.
     * DBConnect menangkap Exception secara internal (tidak melempar),
     * sehingga di sini kita cek null dan lempar SQLException agar
     * blok try-catch di setiap method CRUD tetap berfungsi normal.
     */
    private Connection bukaKoneksi() throws SQLException {
        DBConnect db = new DBConnect();
        if (db.conn == null) {
            throw new SQLException("Koneksi database tidak berhasil dibuka. "
                    + "Periksa kembali konfigurasi server/kredensial pada DBConnect.");
        }
        return db.conn;
    }

    /**
     * Mengambil ID/username karyawan yang sedang login, untuk diisi ke
     * kolom plg_createBy / plg_modifBy.
     *
     * Project ini belum punya mekanisme session/login-tracking, jadi untuk
     * sementara nilainya di-hardcode. Kalau nanti kamu bikin sistem login
     * karyawan (misal simpan ID karyawan di variabel static setelah login
     * berhasil), tinggal ganti isi method ini untuk ambil dari situ.
     */
    private String getUserLoginSaatIni() {
        return "ADMIN";
    }

    private void muatUlangSemua() {
        muatDataDariDatabase();
        tampilkanHalaman(currentPage);
        perbaruiStatistik();
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanInfo(String pesan) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
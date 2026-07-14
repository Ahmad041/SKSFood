package org.gui.sksfood.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
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
    private ComboBox<String> cmbDepartemen;
    @FXML
    private ComboBox<String> cmbStatusAkun;
    @FXML
    private Label lblStatusAkun; // dipakai untuk sembunyikan label + combo bersamaan

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

    // ================= Pencarian, Sorting, Filter =================
    @FXML
    private TextField txtCari;
    @FXML
    private ComboBox<String> cmbUrutkan;
    @FXML
    private ComboBox<String> cmbFilterStatus;
    @FXML
    private ComboBox<String> cmbFilterDepartemen;

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
    private static final int ROW_PER_PAGE = 10;

    private static final String SORT_TERBARU = "Terbaru (Default)";
    private static final String SORT_ID_DESC = "ID Descending";
    private static final String FILTER_SEMUA = "Semua";
    private static final String STATUS_NON_AKTIF = "Non Aktif";
    private static final String STATUS_AKTIF = "Aktif";

    private final ObservableList<Pelanggan> masterData = FXCollections.observableArrayList();
    private ObservableList<Pelanggan> filteredData = FXCollections.observableArrayList();

    private int currentPage = 1;
    private int totalPage = 1;

    private final List<String> daftarDepartemen = List.of(
            "PSI", "DKA", "DA3", "UPT", "UPTIF"
    );

    private final List<String> daftarStatusAkun = List.of(
            STATUS_AKTIF,
            STATUS_NON_AKTIF
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBox();
        setupNoTelpFormatter();
        setupTabel();
        setupValidasiTombolSimpan(); // (#baru) simpan hanya aktif kalau form sudah terisi
        muatDataDariDatabase();
        sembunyikanStatusAkun(); // (#3) form "tambah baru" -> status disembunyikan
        perbaruiTampilan();
        perbaruiStatistik();
        generateIdBaru();
        perbaruiTombolSimpan();
    }

    // =====================================================================
    // SETUP AWAL
    // =====================================================================

    private void setupComboBox() {
        cmbDepartemen.setItems(FXCollections.observableArrayList(daftarDepartemen));
        cmbStatusAkun.setItems(FXCollections.observableArrayList(daftarStatusAkun));
        cmbStatusAkun.getSelectionModel().select(STATUS_AKTIF);
        // (#baru) status akun tidak lagi bisa diubah lewat form, combo hanya
        // dipakai untuk MENAMPILKAN status saat mode edit -> selalu non-interaktif.
        cmbStatusAkun.setDisable(true);
        cmbStatusAkun.setMouseTransparent(true);
        cmbStatusAkun.setFocusTraversable(false);

        // (#5/#6) opsi sorting
        cmbUrutkan.setItems(FXCollections.observableArrayList(SORT_TERBARU, SORT_ID_DESC));
        cmbUrutkan.getSelectionModel().select(SORT_TERBARU);
        cmbUrutkan.valueProperty().addListener((obs, lama, baru) -> perbaruiTampilan());

        // (#7) filter status & departemen
        List<String> opsiFilterStatus = new ArrayList<>();
        opsiFilterStatus.add(FILTER_SEMUA);
        opsiFilterStatus.addAll(daftarStatusAkun);
        cmbFilterStatus.setItems(FXCollections.observableArrayList(opsiFilterStatus));
        cmbFilterStatus.getSelectionModel().select(FILTER_SEMUA);
        cmbFilterStatus.valueProperty().addListener((obs, lama, baru) -> perbaruiTampilan());

        List<String> opsiFilterDept = new ArrayList<>();
        opsiFilterDept.add(FILTER_SEMUA);
        opsiFilterDept.addAll(daftarDepartemen);
        cmbFilterDepartemen.setItems(FXCollections.observableArrayList(opsiFilterDept));
        cmbFilterDepartemen.getSelectionModel().select(FILTER_SEMUA);
        cmbFilterDepartemen.valueProperty().addListener((obs, lama, baru) -> perbaruiTampilan());
    }

    /**
     * No. Telepon: hanya angka, maksimal 13 digit. (#2)
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
        colDepartemen.setCellValueFactory(new PropertyValueFactory<>("departemen"));
        colStatusAkun.setCellValueFactory(new PropertyValueFactory<>("statusAkun"));
    }

    /**
     * (#baru) Tombol SIMPAN hanya bisa ditekan kalau semua field wajib
     * (nama, no telp, email, departemen) sudah terisi. Dipasang sebagai
     * listener supaya statusnya selalu update live saat user mengetik.
     */
    private void setupValidasiTombolSimpan() {
        txtNamaPelanggan.textProperty().addListener((obs, lama, baru) -> perbaruiTombolSimpan());
        txtNoTelp.textProperty().addListener((obs, lama, baru) -> perbaruiTombolSimpan());
        txtEmail.textProperty().addListener((obs, lama, baru) -> perbaruiTombolSimpan());
        cmbDepartemen.valueProperty().addListener((obs, lama, baru) -> perbaruiTombolSimpan());
    }

    private void perbaruiTombolSimpan() {
        boolean namaTerisi = txtNamaPelanggan.getText() != null && !txtNamaPelanggan.getText().trim().isEmpty();
        boolean noTelpTerisi = txtNoTelp.getText() != null && !txtNoTelp.getText().trim().isEmpty();
        boolean emailTerisi = txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty();
        boolean deptTerisi = cmbDepartemen.getValue() != null;

        boolean semuaTerisi = namaTerisi && noTelpTerisi && emailTerisi && deptTerisi;
        btnSimpan.setDisable(!semuaTerisi);
    }

    // =====================================================================
    // AMBIL DATA DARI DATABASE
    // =====================================================================

    private void muatDataDariDatabase() {
        masterData.clear();
        String sql = "SELECT plg_id, plg_nama, plg_noTlpn, plg_email, "
                + "plg_departemen, plg_statusAkun, plg_createDate FROM pelanggan ORDER BY plg_id ASC";

        try (Connection conn = bukaKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("plg_createDate");
                Pelanggan p = new Pelanggan(
                        rs.getString("plg_id"),
                        rs.getString("plg_nama"),
                        rs.getString("plg_noTlpn"),
                        rs.getString("plg_email"),
                        rs.getString("plg_departemen"),
                        rs.getString("plg_statusAkun"),
                        ts == null ? null : ts.toLocalDateTime()
                );
                masterData.add(p);
            }
        } catch (SQLException e) {
            tampilkanPeringatan("Gagal memuat data pelanggan: " + e.getMessage());
        }
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

        String nama = txtNamaPelanggan.getText();
        if (nama == null || nama.trim().isEmpty()) {
            pesan.append("- Nama pelanggan wajib diisi.\n");
        } else if (isNamaSudahDipakai(nama.trim(), txtIdPelanggan.getText())) {
            // (#1) validasi nama/username tidak boleh sama
            pesan.append("- Nama pelanggan sudah digunakan, silakan gunakan nama lain.\n");
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

        if (cmbDepartemen.getValue() == null) {
            pesan.append("- Departemen wajib dipilih.\n");
        }

        // (#baru) status akun tidak lagi diinput lewat form (read-only / otomatis),
        // jadi tidak perlu divalidasi di sini.

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

    /**
     * (#1) Cek apakah nama pelanggan sudah dipakai oleh data lain (selain id yang
     * sedang diedit). Dicek langsung ke database supaya konsisten walau ada
     * banyak instance aplikasi yang jalan bersamaan.
     */
    private boolean isNamaSudahDipakai(String nama, String idSaatIni) {
        String sql = "SELECT COUNT(*) AS jumlah FROM pelanggan WHERE LOWER(plg_nama) = LOWER(?) AND plg_id <> ?";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, idSaatIni == null ? "" : idSaatIni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah") > 0;
                }
            }
        } catch (SQLException e) {
            tampilkanPeringatan("Gagal memeriksa nama pelanggan: " + e.getMessage());
        }
        return false;
    }

    // =====================================================================
    // CRUD - SIMPAN
    // =====================================================================

    @FXML
    private void onSimpan(ActionEvent event) {
        // (#3) mode tambah baru -> status akun tidak ditampilkan, default "Aktif"
        if (!cmbStatusAkun.isVisible()) {
            cmbStatusAkun.setValue(STATUS_AKTIF);
        }

        if (!validasiForm()) {
            return;
        }

        // (#baru) password sudah dihapus total dari form & tabel, plg_password
        // tidak lagi diisi di sini. Pastikan kolom plg_password sudah dihapus
        // (atau dibuat nullable/ada default) di sisi database.
        String sql = "INSERT INTO pelanggan "
                + "(plg_id, plg_nama, plg_noTlpn, plg_email, plg_departemen, plg_statusAkun, "
                + "plg_createBy, plg_createDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtIdPelanggan.getText());
            ps.setString(2, txtNamaPelanggan.getText().trim());
            ps.setString(3, txtNoTelp.getText().trim());
            ps.setString(4, txtEmail.getText().trim());
            ps.setString(5, cmbDepartemen.getValue());
            ps.setString(6, cmbStatusAkun.getValue());
            ps.setString(7, getUserLoginSaatIni());

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

        // (#baru) status akun TIDAK diubah dari sini karena sudah read-only di form;
        // hanya nama, no telp, email, departemen yang bisa diubah lewat UBAH.
        String sql = "UPDATE pelanggan SET plg_nama = ?, plg_noTlpn = ?, plg_email = ?, "
                + "plg_departemen = ?, "
                + "plg_modifBy = ?, plg_modifDate = GETDATE() WHERE plg_id = ?";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtNamaPelanggan.getText().trim());
            ps.setString(2, txtNoTelp.getText().trim());
            ps.setString(3, txtEmail.getText().trim());
            ps.setString(4, cmbDepartemen.getValue());
            ps.setString(5, getUserLoginSaatIni());
            ps.setString(6, txtIdPelanggan.getText());

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
    // CRUD - HAPUS (SOFT DELETE)
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
        konfirmasi.setContentText("Apakah Anda yakin ingin menghapus (menonaktifkan) data pelanggan "
                + txtIdPelanggan.getText() + "?");

        konfirmasi.showAndWait().ifPresent(respon -> {
            if (respon.getButtonData().isDefaultButton()) {
                hapusDariDatabase(txtIdPelanggan.getText());
            }
        });
    }

    /**
     * (#baru) HAPUS tidak lagi menghapus baris dari tabel (hard delete),
     * melainkan hanya mengubah plg_statusAkun menjadi "Non Aktif" (soft delete).
     * Data tetap ada di database untuk keperluan histori/audit, dan otomatis
     * akan ditampilkan paling bawah pada daftar (lihat #8 di perbaruiTampilan()).
     */
    private void hapusDariDatabase(String id) {
        String sql = "UPDATE pelanggan SET plg_statusAkun = ?, plg_modifBy = ?, plg_modifDate = GETDATE() "
                + "WHERE plg_id = ?";

        try (Connection conn = bukaKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, STATUS_NON_AKTIF);
            ps.setString(2, getUserLoginSaatIni());
            ps.setString(3, id);

            int baris = ps.executeUpdate();

            if (baris > 0) {
                tampilkanInfo("Data pelanggan berhasil dihapus (dinonaktifkan).");
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
        cmbDepartemen.getSelectionModel().clearSelection();
        cmbStatusAkun.getSelectionModel().select(STATUS_AKTIF);
        sembunyikanStatusAkun(); // (#3) kembali ke mode tambah baru -> status disembunyikan
        tabelPelanggan.getSelectionModel().clearSelection();
        generateIdBaru();
        perbaruiTombolSimpan();
    }

    // =====================================================================
    // (#3) TAMPIL/SEMBUNYI STATUS AKUN
    // =====================================================================

    private void sembunyikanStatusAkun() {
        cmbStatusAkun.setVisible(false);
        cmbStatusAkun.setManaged(false);
        if (lblStatusAkun != null) {
            lblStatusAkun.setVisible(false);
            lblStatusAkun.setManaged(false);
        }
    }

    private void tampilkanStatusAkunUntukEdit() {
        cmbStatusAkun.setVisible(true);
        cmbStatusAkun.setManaged(true);
        // (#baru) status akun hanya ditampilkan sebagai info, tidak bisa diedit
        // lewat form ini lagi -> tetap disabled/non-interaktif.
        cmbStatusAkun.setDisable(true);
        cmbStatusAkun.setMouseTransparent(true);
        if (lblStatusAkun != null) {
            lblStatusAkun.setVisible(true);
            lblStatusAkun.setManaged(true);
        }
    }

    // =====================================================================
    // PENCARIAN (#4, #9)
    // =====================================================================

    @FXML
    private void onCari(ActionEvent event) {
        perbaruiTampilan();
    }

    // =====================================================================
    // (#5, #6, #7, #8) SORTING + FILTER + PENCARIAN DIGABUNG DI SATU PIPELINE
    // =====================================================================

    private void perbaruiTampilan() {
        String kataKunci = txtCari.getText() == null ? "" : txtCari.getText().trim().toLowerCase();
        String filterStatus = cmbFilterStatus.getValue();
        String filterDept = cmbFilterDepartemen.getValue();
        String urutan = cmbUrutkan.getValue();

        List<Pelanggan> hasil = new ArrayList<>();
        for (Pelanggan p : masterData) {
            // (#4, #9) cari berdasarkan ID, Nama, atau No. Telepon saja.
            // Pencarian nama pakai "contains" (substring) terhadap nama lengkap,
            // sehingga otomatis bisa menemukan lewat nama depan, tengah, ATAU
            // belakang -- misalnya "budi santoso wijaya" tetap ketemu walau
            // yang diketik "santoso" (nama tengah) atau "wijaya" (nama belakang).
            boolean cocokCari = kataKunci.isEmpty()
                    || mengandung(p.getId(), kataKunci)
                    || cocokNama(p.getNama(), kataKunci)
                    || mengandung(p.getNoTelp(), kataKunci);

            // (#7) filter status & departemen
            boolean cocokStatus = filterStatus == null || FILTER_SEMUA.equals(filterStatus)
                    || filterStatus.equalsIgnoreCase(p.getStatusAkun());
            boolean cocokDept = filterDept == null || FILTER_SEMUA.equals(filterDept)
                    || filterDept.equalsIgnoreCase(p.getDepartemen());

            if (cocokCari && cocokStatus && cocokDept) {
                hasil.add(p);
            }
        }

        // (#6) sorting: terbaru (createDate desc) atau ID descending
        Comparator<Pelanggan> comparator;
        if (SORT_ID_DESC.equals(urutan)) {
            comparator = Comparator.comparing(Pelanggan::getId, Comparator.nullsLast(Comparator.reverseOrder()));
        } else {
            comparator = Comparator.comparing(
                    Pelanggan::getCreateDate,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
        }
        hasil.sort(comparator);

        // (#8) data dengan status "Non Aktif" (termasuk hasil soft delete) selalu
        // ditaruh paling bawah, tanpa merusak urutan sorting di masing-masing kelompok.
        List<Pelanggan> aktif = new ArrayList<>();
        List<Pelanggan> nonAktif = new ArrayList<>();
        for (Pelanggan p : hasil) {
            if (STATUS_NON_AKTIF.equalsIgnoreCase(p.getStatusAkun())) {
                nonAktif.add(p);
            } else {
                aktif.add(p);
            }
        }
        List<Pelanggan> gabungan = new ArrayList<>(aktif);
        gabungan.addAll(nonAktif);

        filteredData = FXCollections.observableArrayList(gabungan);
        tampilkanHalaman(1);
    }

    private boolean mengandung(String sumber, String kataKunci) {
        return sumber != null && sumber.toLowerCase().contains(kataKunci);
    }

    /**
     * (#baru) Pencarian nama: dicocokkan terhadap nama lengkap sebagai substring,
     * DAN terhadap tiap kata (token) di nama tersebut secara terpisah. Dengan
     * begitu nama depan, tengah, maupun belakang semuanya bisa ditemukan,
     * termasuk pencarian sebagian kata di tengah nama.
     */
    private boolean cocokNama(String namaLengkap, String kataKunci) {
        if (namaLengkap == null) {
            return false;
        }
        String namaLower = namaLengkap.toLowerCase();
        if (namaLower.contains(kataKunci)) {
            return true;
        }
        for (String bagian : namaLower.split("\\s+")) {
            if (bagian.contains(kataKunci)) {
                return true;
            }
        }
        return false;
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
        cmbDepartemen.setValue(dipilih.getDepartemen());

        // (#3) mode edit -> status akun ditampilkan (read-only, lihat #baru)
        tampilkanStatusAkunUntukEdit();
        cmbStatusAkun.setValue(dipilih.getStatusAkun());

        perbaruiTombolSimpan();
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
                .filter(p -> STATUS_AKTIF.equalsIgnoreCase(p.getStatusAkun()))
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
        perbaruiTampilan();
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
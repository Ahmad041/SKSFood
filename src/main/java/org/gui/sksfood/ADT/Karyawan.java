package org.gui.sksfood.ADT;

public class Karyawan {

    private String idKaryawan;
    private String namaKaryawan;
    private String noTlpnKaryawan;
    private String jabatan;
    private String username;
    private String password;
    private String statusKaryawan;

    // ── Constructor kosong ────────────────────────────────────────────
    public Karyawan() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Karyawan(String idKaryawan, String namaKaryawan,
                    String noTlpnKaryawan, String jabatan,
                    String username, String password,
                    String statusKaryawan) {
        this.idKaryawan     = idKaryawan;
        this.namaKaryawan   = namaKaryawan;
        this.noTlpnKaryawan = noTlpnKaryawan;
        this.jabatan        = jabatan;
        this.username       = username;
        this.password       = password;
        this.statusKaryawan = statusKaryawan;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getIdKaryawan()     { return idKaryawan; }
    public String getNamaKaryawan()   { return namaKaryawan; }
    public String getNoTlpnKaryawan() { return noTlpnKaryawan; }
    public String getJabatan()        { return jabatan; }
    public String getUsername()       { return username; }
    public String getPassword()       { return password; }
    public String getStatusKaryawan() { return statusKaryawan; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdKaryawan(String idKaryawan)         { this.idKaryawan     = idKaryawan; }
    public void setNamaKaryawan(String namaKaryawan)     { this.namaKaryawan   = namaKaryawan; }
    public void setNoTlpnKaryawan(String noTlpnKaryawan) { this.noTlpnKaryawan = noTlpnKaryawan; }
    public void setJabatan(String jabatan)               { this.jabatan        = jabatan; }
    public void setUsername(String username)             { this.username       = username; }
    public void setPassword(String password)             { this.password       = password; }
    public void setStatusKaryawan(String statusKaryawan) { this.statusKaryawan = statusKaryawan; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Karyawan{" +
                "id="        + idKaryawan     +
                ", nama="    + namaKaryawan   +
                ", jabatan=" + jabatan        +
                ", status="  + statusKaryawan +
                "}";
    }
}
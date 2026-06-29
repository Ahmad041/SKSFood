package org.gui.sksfood.ADT;

public class Pelanggan {

    private String idPelanggan;
    private String namaPelanggan;
    private String noTlpnPelanggan;
    private String email;
    private String password;
    private String departemen;
    private String statusAkunPelanggan;

    // ── Constructor kosong ────────────────────────────────────────────
    public Pelanggan() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Pelanggan(String idPelanggan, String namaPelanggan,
                     String noTlpnPelanggan, String email,
                     String password, String departemen,
                     String statusAkunPelanggan) {
        this.idPelanggan          = idPelanggan;
        this.namaPelanggan        = namaPelanggan;
        this.noTlpnPelanggan      = noTlpnPelanggan;
        this.email                = email;
        this.password             = password;
        this.departemen           = departemen;
        this.statusAkunPelanggan  = statusAkunPelanggan;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getIdPelanggan()         { return idPelanggan; }
    public String getNamaPelanggan()       { return namaPelanggan; }
    public String getNoTlpnPelanggan()     { return noTlpnPelanggan; }
    public String getEmail()               { return email; }
    public String getPassword()            { return password; }
    public String getDepartemen()          { return departemen; }
    public String getStatusAkunPelanggan() { return statusAkunPelanggan; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdPelanggan(String idPelanggan)                       { this.idPelanggan         = idPelanggan; }
    public void setNamaPelanggan(String namaPelanggan)                   { this.namaPelanggan       = namaPelanggan; }
    public void setNoTlpnPelanggan(String noTlpnPelanggan)               { this.noTlpnPelanggan     = noTlpnPelanggan; }
    public void setEmail(String email)                                   { this.email               = email; }
    public void setPassword(String password)                             { this.password            = password; }
    public void setDepartemen(String departemen)                         { this.departemen          = departemen; }
    public void setStatusAkunPelanggan(String statusAkunPelanggan)       { this.statusAkunPelanggan = statusAkunPelanggan; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Pelanggan{" +
                "id="       + idPelanggan         +
                ", nama="   + namaPelanggan        +
                ", email="  + email               +
                ", status=" + statusAkunPelanggan  +
                "}";
    }
}
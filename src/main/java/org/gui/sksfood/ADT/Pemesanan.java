package org.gui.sksfood.ADT;

public class Pemesanan {

    private String idPemesanan;
    private String idPelanggan;
    private String idKaryawan;
    private String idTempat;
    private String jenisPesanan;
    private int    totalHarga;
    private String metodePembayaran;
    private String statusPembayaran;
    private String statusPemesanan;
    private String catatan;

    // ── Constructor kosong ────────────────────────────────────────────
    public Pemesanan() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Pemesanan(String idPemesanan, String idPelanggan,
                     String idKaryawan, String idTempat,
                     String jenisPesanan, int totalHarga,
                     String metodePembayaran, String statusPembayaran,
                     String statusPemesanan, String catatan) {
        this.idPemesanan      = idPemesanan;
        this.idPelanggan      = idPelanggan;
        this.idKaryawan       = idKaryawan;
        this.idTempat         = idTempat;
        this.jenisPesanan     = jenisPesanan;
        this.totalHarga       = totalHarga;
        this.metodePembayaran = metodePembayaran;
        this.statusPembayaran = statusPembayaran;
        this.statusPemesanan  = statusPemesanan;
        this.catatan          = catatan;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getIdPemesanan()      { return idPemesanan; }
    public String getIdPelanggan()      { return idPelanggan; }
    public String getIdKaryawan()       { return idKaryawan; }
    public String getIdTempat()         { return idTempat; }
    public String getJenisPesanan()     { return jenisPesanan; }
    public int    getTotalHarga()       { return totalHarga; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public String getStatusPembayaran() { return statusPembayaran; }
    public String getStatusPemesanan()  { return statusPemesanan; }
    public String getCatatan()          { return catatan; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdPemesanan(String idPemesanan)           { this.idPemesanan      = idPemesanan; }
    public void setIdPelanggan(String idPelanggan)           { this.idPelanggan      = idPelanggan; }
    public void setIdKaryawan(String idKaryawan)             { this.idKaryawan       = idKaryawan; }
    public void setIdTempat(String idTempat)                 { this.idTempat         = idTempat; }
    public void setJenisPesanan(String jenisPesanan)         { this.jenisPesanan     = jenisPesanan; }
    public void setTotalHarga(int totalHarga)                { this.totalHarga       = totalHarga; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public void setStatusPembayaran(String statusPembayaran) { this.statusPembayaran = statusPembayaran; }
    public void setStatusPemesanan(String statusPemesanan)   { this.statusPemesanan  = statusPemesanan; }
    public void setCatatan(String catatan)                   { this.catatan          = catatan; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Pemesanan{" +
                "id="              + idPemesanan      +
                ", idPelanggan="   + idPelanggan      +
                ", idKaryawan="    + idKaryawan       +
                ", idTempat="      + idTempat         +
                ", jenis="         + jenisPesanan     +
                ", total="         + totalHarga       +
                ", stsPembayaran=" + statusPembayaran +
                ", stsPemesanan="  + statusPemesanan  +
                "}";
    }
}
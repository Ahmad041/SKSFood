package org.gui.sksfood.ADT;

public class DetailPemesanan {

    private String detailPemesanan;
    private String idMenu;
    private String idPemesanan;
    private int    jumlahPemesanan;

    // ── Constructor kosong ────────────────────────────────────────────
    public DetailPemesanan() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public DetailPemesanan(String detailPemesanan, String idMenu,
                           String idPemesanan, int jumlahPemesanan) {
        this.detailPemesanan = detailPemesanan;
        this.idMenu          = idMenu;
        this.idPemesanan     = idPemesanan;
        this.jumlahPemesanan = jumlahPemesanan;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getDetailPemesanan() { return detailPemesanan; }
    public String getIdMenu()          { return idMenu; }
    public String getIdPemesanan()     { return idPemesanan; }
    public int    getJumlahPemesanan() { return jumlahPemesanan; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setDetailPemesanan(String detailPemesanan) { this.detailPemesanan = detailPemesanan; }
    public void setIdMenu(String idMenu)                   { this.idMenu          = idMenu; }
    public void setIdPemesanan(String idPemesanan)         { this.idPemesanan     = idPemesanan; }
    public void setJumlahPemesanan(int jumlahPemesanan)    { this.jumlahPemesanan = jumlahPemesanan; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "DetailPemesanan{" +
                "id="            + detailPemesanan +
                ", idMenu="      + idMenu          +
                ", idPemesanan=" + idPemesanan     +
                ", jumlah="      + jumlahPemesanan +
                "}";
    }
}
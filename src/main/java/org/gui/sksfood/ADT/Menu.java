package org.gui.sksfood.ADT;

public class Menu {

    private String idMenu;
    private String namaMenu;
    private String deskripsi;
    private int    harga;
    private String kategoriMenu;
    private String jenisKetersediaan;
    private int    stok;
    private String statusMenu;

    // ── Constructor kosong ────────────────────────────────────────────
    public Menu() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Menu(String idMenu, String namaMenu,
                String deskripsi, int harga,
                String kategoriMenu, String jenisKetersediaan,
                int stok, String statusMenu) {
        this.idMenu            = idMenu;
        this.namaMenu          = namaMenu;
        this.deskripsi         = deskripsi;
        this.harga             = harga;
        this.kategoriMenu      = kategoriMenu;
        this.jenisKetersediaan = jenisKetersediaan;
        this.stok              = stok;
        this.statusMenu        = statusMenu;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getIdMenu()            { return idMenu; }
    public String getNamaMenu()          { return namaMenu; }
    public String getDeskripsi()         { return deskripsi; }
    public int    getHarga()             { return harga; }
    public String getKategoriMenu()      { return kategoriMenu; }
    public String getJenisKetersediaan() { return jenisKetersediaan; }
    public int    getStok()              { return stok; }
    public String getStatusMenu()        { return statusMenu; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdMenu(String idMenu)                       { this.idMenu            = idMenu; }
    public void setNamaMenu(String namaMenu)                   { this.namaMenu          = namaMenu; }
    public void setDeskripsi(String deskripsi)                 { this.deskripsi         = deskripsi; }
    public void setHarga(int harga)                            { this.harga             = harga; }
    public void setKategoriMenu(String kategoriMenu)           { this.kategoriMenu      = kategoriMenu; }
    public void setJenisKetersediaan(String jenisKetersediaan) { this.jenisKetersediaan = jenisKetersediaan; }
    public void setStok(int stok)                              { this.stok              = stok; }
    public void setStatusMenu(String statusMenu)               { this.statusMenu        = statusMenu; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Menu{" +
                "id="         + idMenu       +
                ", nama="     + namaMenu     +
                ", harga="    + harga        +
                ", kategori=" + kategoriMenu +
                ", stok="     + stok         +
                ", status="   + statusMenu   +
                "}";
    }
}
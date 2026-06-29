package org.gui.sksfood.ADT;

import java.time.LocalDateTime;

public class UlasanMenu {

    private String        idUlasan;
    private String        idPelanggan;
    private String        idMenu;
    private String        rating;
    private String        komentar;
    private LocalDateTime tglUlasan;

    // ── Constructor kosong ────────────────────────────────────────────
    public UlasanMenu() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public UlasanMenu(String idUlasan, String idPelanggan,
                      String idMenu, String rating,
                      String komentar, LocalDateTime tglUlasan) {
        this.idUlasan   = idUlasan;
        this.idPelanggan = idPelanggan;
        this.idMenu     = idMenu;
        this.rating     = rating;
        this.komentar   = komentar;
        this.tglUlasan  = tglUlasan;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String        getIdUlasan()    { return idUlasan; }
    public String        getIdPelanggan() { return idPelanggan; }
    public String        getIdMenu()      { return idMenu; }
    public String        getRating()      { return rating; }
    public String        getKomentar()    { return komentar; }
    public LocalDateTime getTglUlasan()   { return tglUlasan; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdUlasan(String idUlasan)           { this.idUlasan    = idUlasan; }
    public void setIdPelanggan(String idPelanggan)     { this.idPelanggan = idPelanggan; }
    public void setIdMenu(String idMenu)               { this.idMenu      = idMenu; }
    public void setRating(String rating)               { this.rating      = rating; }
    public void setKomentar(String komentar)           { this.komentar    = komentar; }
    public void setTglUlasan(LocalDateTime tglUlasan)  { this.tglUlasan   = tglUlasan; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "UlasanMenu{" +
                "id="           + idUlasan    +
                ", idPelanggan=" + idPelanggan +
                ", idMenu="     + idMenu      +
                ", rating="     + rating      +
                ", tgl="        + tglUlasan   +
                "}";
    }
}
package org.gui.sksfood.ADT;

public class Tempat {

    private String idTempat;
    private String namaTempat;
    private String lokasi;
    private String jamBuka;
    private String jamTutup;
    private String statusTempat;

    // ── Constructor kosong ────────────────────────────────────────────
    public Tempat() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Tempat(String idTempat, String namaTempat,
                  String lokasi, String jamBuka,
                  String jamTutup, String statusTempat) {
        this.idTempat    = idTempat;
        this.namaTempat  = namaTempat;
        this.lokasi      = lokasi;
        this.jamBuka     = jamBuka;
        this.jamTutup    = jamTutup;
        this.statusTempat = statusTempat;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getIdTempat()     { return idTempat; }
    public String getNamaTempat()   { return namaTempat; }
    public String getLokasi()       { return lokasi; }
    public String getJamBuka()      { return jamBuka; }
    public String getJamTutup()     { return jamTutup; }
    public String getStatusTempat() { return statusTempat; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setIdTempat(String idTempat)         { this.idTempat     = idTempat; }
    public void setNamaTempat(String namaTempat)     { this.namaTempat   = namaTempat; }
    public void setLokasi(String lokasi)             { this.lokasi       = lokasi; }
    public void setJamBuka(String jamBuka)           { this.jamBuka      = jamBuka; }
    public void setJamTutup(String jamTutup)         { this.jamTutup     = jamTutup; }
    public void setStatusTempat(String statusTempat) { this.statusTempat = statusTempat; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Tempat{" +
                "id="       + idTempat     +
                ", nama="   + namaTempat   +
                ", lokasi=" + lokasi       +
                ", status=" + statusTempat +
                "}";
    }
}
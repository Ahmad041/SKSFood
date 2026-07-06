// file: Menu.java
package org.gui.sksfood.ADT;

import java.time.LocalDateTime;

public class Menu {

    private String        menId;
    private String        menNama;
    private String        menDeskripsi;
    private int           menHarga;
    private String        menKategori;
    private int           menStok;
    private String        menStatus;
    private String        createdBy;
    private LocalDateTime createDate;
    private String        modifiedBy;
    private LocalDateTime modifDate;

    // Field jenisKetersediaan DIHAPUS (tidak ada di database)

    // ── Constructor kosong ────────────────────────────────────────────
    public Menu() {}

    // ── Constructor lengkap ───────────────────────────────────────────
    public Menu(String menId, String menNama,
                String menDeskripsi, int menHarga,
                String menKategori, int menStok,
                String menStatus, String createdBy,
                LocalDateTime createDate, String modifiedBy,
                LocalDateTime modifDate) {
        this.menId         = menId;
        this.menNama       = menNama;
        this.menDeskripsi  = menDeskripsi;
        this.menHarga      = menHarga;
        this.menKategori   = menKategori;
        this.menStok       = menStok;
        this.menStatus     = menStatus;
        this.createdBy     = createdBy;
        this.createDate    = createDate;
        this.modifiedBy    = modifiedBy;
        this.modifDate     = modifDate;
    }

    // ── Getter ────────────────────────────────────────────────────────
    public String getMenId()            { return menId; }
    public String getMenNama()          { return menNama; }
    public String getMenDeskripsi()     { return menDeskripsi; }
    public int    getMenHarga()         { return menHarga; }
    public String getMenKategori()      { return menKategori; }
    public int    getMenStok()          { return menStok; }
    public String getMenStatus()        { return menStatus; }
    public String getCreatedBy()        { return createdBy; }
    public LocalDateTime getCreateDate() { return createDate; }
    public String getModifiedBy()       { return modifiedBy; }
    public LocalDateTime getModifDate() { return modifDate; }

    // ── Setter ────────────────────────────────────────────────────────
    public void setMenId(String menId)                       { this.menId         = menId; }
    public void setMenNama(String menNama)                   { this.menNama       = menNama; }
    public void setMenDeskripsi(String menDeskripsi)         { this.menDeskripsi  = menDeskripsi; }
    public void setMenHarga(int menHarga)                    { this.menHarga      = menHarga; }
    public void setMenKategori(String menKategori)           { this.menKategori  = menKategori; }
    public void setMenStok(int menStok)                      { this.menStok       = menStok; }
    public void setMenStatus(String menStatus)               { this.menStatus     = menStatus; }
    public void setCreatedBy(String createdBy)                { this.createdBy     = createdBy; }
    public void setCreateDate(LocalDateTime createDate)      { this.createDate    = createDate; }
    public void setModifiedBy(String modifiedBy)              { this.modifiedBy    = modifiedBy; }
    public void setModifDate(LocalDateTime modifDate)        { this.modifDate     = modifDate; }

    // ── toString ──────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Menu{" +
                "menId="       + menId      +
                ", menNama="   + menNama    +
                ", menDeskripsi=" + menDeskripsi +
                ", menHarga="   + menHarga   +
                ", menKategori=" + menKategori +
                ", menStok="  + menStok     +
                ", menStatus="  + menStatus +
                ", createdBy="   + createdBy +
                ", createDate="  + createDate +
                ", modifiedBy="  + modifiedBy +
                ", modifDate="  + modifDate +
                "}";
    }
}
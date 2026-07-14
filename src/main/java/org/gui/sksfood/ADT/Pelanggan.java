package org.gui.sksfood.ADT;

import java.time.LocalDateTime;

public class Pelanggan {

    private String id;
    private String nama;
    private String noTelp;
    private String email;
    private String departemen;
    private String statusAkun;
    private LocalDateTime createDate;

    public Pelanggan() {
    }

    public Pelanggan(String id, String nama, String noTelp, String email,
                     String departemen, String statusAkun, LocalDateTime createDate) {
        this.id = id;
        this.nama = nama;
        this.noTelp = noTelp;
        this.email = email;
        this.departemen = departemen;
        this.statusAkun = statusAkun;
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartemen() {
        return departemen;
    }

    public void setDepartemen(String departemen) {
        this.departemen = departemen;
    }

    public String getStatusAkun() {
        return statusAkun;
    }

    public void setStatusAkun(String statusAkun) {
        this.statusAkun = statusAkun;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
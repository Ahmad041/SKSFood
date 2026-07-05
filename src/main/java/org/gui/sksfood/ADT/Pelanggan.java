package org.gui.sksfood.ADT;


public class Pelanggan {

    private String id;
    private String nama;
    private String noTelp;
    private String email;
    private String password;
    private String departemen;
    private String statusAkun;

    public Pelanggan() {
    }

    public Pelanggan(String id, String nama, String noTelp, String email,
                     String password, String departemen, String statusAkun) {
        this.id = id;
        this.nama = nama;
        this.noTelp = noTelp;
        this.email = email;
        this.password = password;
        this.departemen = departemen;
        this.statusAkun = statusAkun;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
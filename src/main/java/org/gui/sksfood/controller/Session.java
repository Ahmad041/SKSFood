package org.gui.sksfood.controller;

import org.gui.sksfood.ADT.Karyawan;

public class Session {

    private static Karyawan loggedInKaryawan;

    private Session() {}

    public static Karyawan getLoggedInKaryawan() {
        return loggedInKaryawan;
    }

    public static void setLoggedInKaryawan(Karyawan karyawan) {
        loggedInKaryawan = karyawan;
    }

    public static void clear() {
        loggedInKaryawan = null;
    }
}
package SQL;

import java.sql.*;

public class DBConnect {
    public Connection conn;
    public Statement stat;

    public DBConnect() {
        try {
            String url = "jdbc:sqlserver://127.0.0.2:1435;databaseName=ProjekPendidikan2627;encrypt=false";

            String user = "Pendidikan";
            String password = "P3ndidikan@2025!";

            conn = DriverManager.getConnection(url, user, password);
            stat = conn.createStatement();

            System.out.println("Connection berhasil");
        } catch (Exception e) {
            System.out.println("Error saat connect database: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        DBConnect connect = new DBConnect();
    }

}


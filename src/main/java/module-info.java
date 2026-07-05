module org.gui.sksfood {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    // Package utama (MainApp)
    opens org.gui.sksfood to javafx.fxml;
    exports org.gui.sksfood;

    opens org.gui.sksfood.controller to javafx.fxml;

    // Model/ADT perlu dibuka ke javafx.base supaya PropertyValueFactory
    // (dipakai TableColumn.setCellValueFactory) bisa akses getter lewat reflection.
    opens org.gui.sksfood.ADT to javafx.base;
}
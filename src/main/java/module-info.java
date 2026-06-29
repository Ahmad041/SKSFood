module org.gui.sksfood {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.gui.sksfood to javafx.fxml;
    exports org.gui.sksfood;
}
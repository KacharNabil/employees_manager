module com.example.javafx_projcet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.javafx_projcet to javafx.fxml;
    exports com.example.javafx_projcet;
}
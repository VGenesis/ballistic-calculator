module com.example.demo {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}
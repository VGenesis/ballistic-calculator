module com.example.demo {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.poi.ooxml;

    exports com.example.demo;
    opens com.example.demo to javafx.fxml;
    exports com.example.demo.controllers;
    opens com.example.demo.controllers to javafx.fxml;
    exports com.example.demo.views;
    opens com.example.demo.views to javafx.fxml;
    exports com.example.demo.logic;
    opens com.example.demo.logic to javafx.fxml;
}
package com.example.demo.views;

import com.example.demo.AppWindow;
import com.example.demo.controllers.AppController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AboutWindow implements Initializable {
    AppController parentApplication;
    Stage window;
    public AboutWindow(AppController parentApplication) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("aboutWindow.fxml"));
            Scene scene = new Scene(loader.load(), 430, 300);
            this.parentApplication = parentApplication;
            this.window = new Stage();

            window.setTitle("About");
            window.setScene(scene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.setOnCloseRequest(e -> parentApplication.aboutWindow = null);

            String iconURL = Objects.requireNonNull(AppWindow.class.getResource("icon.png")).toString();
            if(iconURL != null) window.getIcons().add(new Image(iconURL));

            window.show();
        } catch (IOException e) {
            System.out.println("Cannot load 'About' window.");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

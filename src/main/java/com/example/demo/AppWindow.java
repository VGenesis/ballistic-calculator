package com.example.demo;

import com.example.demo.controllers.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class AppWindow extends Application {
    private Stage window;
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("projectileWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        this.window = stage;

        window.setTitle("Ballistic Calculator");
        window.setScene(scene);
        window.setResizable(false);
        window.getIcons().add(new Image(Objects.requireNonNull(AppWindow.class.getResource("icon.png")).toString()));
        window.show();
    }

}

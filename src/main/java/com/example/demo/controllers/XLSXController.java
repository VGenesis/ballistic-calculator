package com.example.demo.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class XLSXController{
    @FXML
    private Button exportButton;
    @FXML
    private TextField measureCountField;
    @FXML
    private TextField precisionField;

    private Stage window;
    private int measureCount;
    private int precision;

    @FXML
    void updateExportParams(KeyEvent event) {
        try{
            this.measureCount = Integer.parseInt(measureCountField.getText());
            this.precision = Integer.parseInt(precisionField.getText());
        }catch(NumberFormatException ignored){}
    }

    @FXML
    void export(ActionEvent event){
        return;
    }

    public int[] display(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("exportWindowXLSX.fxml"));
        Scene scene = new Scene(loader.load(), Color.WHITE);
        this.window = stage;

        stage.setTitle("Export XLSX...");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        exportButton.setOnAction(e -> window.close());

        return new int[]{measureCount, precision};
    }
}

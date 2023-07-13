package com.example.demo.controllers;

import com.aspose.cells.*;

import com.example.demo.logic.Conversions;
import com.example.demo.logic.ProjectileModel;
import com.example.demo.logic.Vector;
import com.example.demo.views.AboutWindow;
import com.example.demo.views.AppGraphics;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private TextField angleField;
    @FXML
    private TextField densityField;
    @FXML
    private TextField diameterField;
    @FXML
    private MenuItem exportMenuCSV;
    @FXML
    private MenuItem exportMenuJSON;
    @FXML
    private MenuItem exportMenuXLSX;
    @FXML
    private TextField heightField;
    @FXML
    private MenuItem importMenuCSV;
    @FXML
    private MenuItem importMenuJSON;
    @FXML
    private MenuItem importMenuParams;
    @FXML
    private MenuItem importMenuXLSX;
    @FXML
    private TextField massField;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private TextField scaleField;
    @FXML
    private TextField speedField;
    @FXML
    private Canvas trajectoryCanvas;
    private AppGraphics graphics;
    public Stage window;
    public AboutWindow aboutWindow;
    private double[] parameters = new double[7];
    private final int dataCount = 30;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.graphics = new AppGraphics(trajectoryCanvas);
        this.aboutWindow = null;
        this.parameters = new double[7];
        Arrays.fill(parameters, 0);
    }

    @FXML
    void exportCSV(ActionEvent ignoredEvent) {

    }

    @FXML
    void exportJSON(ActionEvent ignoredEvent) {

    }

    @FXML
    void exportXLSX(ActionEvent ignoredEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export CSV file");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XLSX File", ".xlsx"));
        Stage stage = new Stage();
        File output = fc.showSaveDialog(stage);
        if(output == null) return;

        try {
            if(!output.exists()) output.createNewFile();

            Workbook wb = new Workbook();
            Worksheet ws = wb.getWorksheets().get(0);
            Style centered = new Style();
            centered.setHorizontalAlignment(TextAlignmentType.RIGHT);
            for(int i = 1; i <= 3; i++){
                ws.getCells().setColumnWidth(i, 15);
                ws.getCells().setStyle(centered);
            }

            ws.getCells().importArray(getParamsString(), 1, 1);

            ProjectileModel model = new ProjectileModel(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
            model.setDataPoints(dataCount);
            model.plotTrajectory();
            ArrayList<Vector> trajectoryPlot = model.getTrajectoryPlot();
            ArrayList<Double> timePlot = model.getTimePlot();

            double maxDistance = model.distance;
            int maxDigits = (int) Math.floor(Math.log10(maxDistance));
            int decimalCount = (maxDigits < 3)? 3 - maxDigits : 0;

            DecimalFormat df = new DecimalFormat("#." + "#".repeat(decimalCount));
            double[][] trajectoryData = Conversions.vectorToMatrix(trajectoryPlot);
            double[] timeData = Conversions.listToArray(timePlot);

            double[][] trajectory = new double[3][dataCount];
            trajectory[0] = timeData;
            trajectory[1] = trajectoryData[0];
            trajectory[2] = trajectoryData[1];

            double[][] trajectoryVertical = new double[dataCount][3];
            for(int i = 0; i < trajectory.length; i++){
                for(int j = 0; j < trajectory[0].length; j++){
                    trajectoryVertical[j][i] = Double.parseDouble(df.format(trajectory[i][j]));
                }
            }

            ws.getCells().importArray(new String[]{"Time:", "Distance:", "Height:"}, 1, 5, false);
            ws.getCells().importArray(trajectoryVertical, 2, 5);
            ws.getCharts().clear();

            int chartIndex = ws.getCharts().add(ChartType.LINE, 1, 9, 30, 24);
            Chart lineChart = ws.getCharts().get(chartIndex);
            lineChart.setName("Trajectory");

            String seriesRegex = String.format("H3:H%d", dataCount + 2);
            lineChart.setChartDataRange(seriesRegex, true);
            lineChart.getCategoryAxis().setMinValue(0);
            lineChart.getCategoryAxis().setMaxValue(maxDistance);
            lineChart.getNSeries().clear();

            String categoryRegex = String.format("F3:G%d", dataCount + 2);
            int seriesIndex = lineChart.getNSeries().add(seriesRegex, true);
            Series series = lineChart.getNSeries().get(seriesIndex);
            series.setXValues(categoryRegex);
            series.setName("");

            wb.save(output.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void importCSV(ActionEvent ignoredEvent) {

    }

    @FXML
    void importJSON(ActionEvent ignoredEvent) {

    }

    @FXML
    void importParams(ActionEvent ignoredEvent) {

    }

    @FXML
    void importXLSX(ActionEvent ignoredEvent) {

    }

    @FXML
    void onParameterUpdate(KeyEvent ignoredEvent) {
        try {
            parameters[0] = Double.parseDouble(speedField.getText());
            parameters[1] = Double.parseDouble(angleField.getText());
            parameters[2] = Double.parseDouble(heightField.getText());
            parameters[3] = Double.parseDouble(massField.getText());
            parameters[4] = Double.parseDouble(diameterField.getText());
            parameters[5] = Double.parseDouble(densityField.getText());
            parameters[6] = Double.parseDouble(scaleField.getText());

            graphics.updateParams(parameters);
        }catch(NumberFormatException ignored){}
    }

    @FXML
    void openAboutWindow(ActionEvent ignoredEvent) throws IOException {
        aboutWindow = new AboutWindow(this);
    }

    @FXML
    void quit(ActionEvent ignoredEvent) {
        Platform.exit();
    }

    private double[] parseParams(String[] params){
        double[] res = new double[6];
        for(int i = 0; i < 6; i++){
            res[i] = Double.parseDouble(params[i]);
        }
        return res;
    }
    private String[][] getParamsString(){
        String[] paramNames = {"Starting Speed:", "Starting Angle:", "Starting height", "Bullet mass", "Bullet diameter", "Air density"};
        String[] paramUnits = {"m/s", "degrees", "m", "g", "mm", "kg/m3"};

        String[][] res = new String[6][3];
        for(int i = 0; i < parameters.length - 1; i++){
            res[i][0] = paramNames[i];
            res[i][1] = Double.toString(parameters[i]);
            res[i][2] = paramUnits[i];
        }

        return res;
    }

}

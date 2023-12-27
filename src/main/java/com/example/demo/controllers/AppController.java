package com.example.demo.controllers;

import com.example.demo.logic.Vector;
import com.example.demo.logic.RetentionFileChooser;
import javafx.application.Application;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;

import com.example.demo.logic.ProjectileModel;
import com.example.demo.views.AboutWindow;
import com.example.demo.views.AppGraphics;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Optional;

public class AppController implements Initializable {
    @FXML
    private TextField angleField;
    @FXML
    private TextField densityField;
    @FXML
    private TextField diameterField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField massField;
    @FXML
    private TextField speedField;
    @FXML
    private TextField dragCoeffField;
    @FXML
    private TextField distanceField;

    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem exportMenu;
    @FXML
    private MenuItem importMenuParams;
    @FXML
    private MenuItem importMenuGraph;
    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private Canvas trajectoryCanvas;

    private AppGraphics graphics;
    public Stage window;
    public AboutWindow aboutWindow = null;
    private double[] parameters = null;
    private final int dataCount = 30;
    private Path exportFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.graphics = new AppGraphics(trajectoryCanvas);
    }

    @FXML
    void export(){
        try{
            RetentionFileChooser rfc = new RetentionFileChooser();

            if (exportFile == null) {
                URL url = this.getClass().getResource("export_options.txt");
                exportFile = Files.createTempFile(null, ".txt");
                assert url != null;
                InputStream in = url.openStream();
                OutputStream out = Files.newOutputStream(exportFile);
                in.transferTo(out);
                in.close();
                out.close();
            }

            Stage stage = new Stage();
            File output = rfc.showSaveDialog(stage, "Title".describeConstable());
            if(output == null) return;

            FileOutputStream outputStream = new FileOutputStream(output);
            XSSFWorkbook workbook = new XSSFWorkbook();

            ProjectileModel model = new ProjectileModel(parameters[0], parameters[1], parameters[2], parameters[3], parameters[4], parameters[5]);
            try {
                writeXSSFSheet(workbook, model);
            }catch(Exception e){
                System.out.println("Failed to create Excel sheet.");
                return;
            }

            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @FXML
    void importParams(ActionEvent ignoredEvent) {

    }

    @FXML
    void importGraph(ActionEvent ignoredEvent) {

    }

    @FXML
    void onParameterUpdate(KeyEvent ignoredEvent) {
        try {
            parameters = new double[7];
            parameters[0] = Double.parseDouble(speedField.getText());
            parameters[1] = Double.parseDouble(angleField.getText());
            parameters[2] = Double.parseDouble(heightField.getText());
            parameters[3] = Double.parseDouble(massField.getText());
            parameters[4] = Double.parseDouble(diameterField.getText());
            parameters[5] = Double.parseDouble(densityField.getText());
            parameters[6] = Double.parseDouble(dragCoeffField.getText());

            graphics.updateParams(parameters);
            distanceField.setText(String.format("%.2f", graphics.maxMeasure.x));
        }catch(NumberFormatException e){
            parameters = null;
        }
    }

    @FXML
    void openAboutWindow(ActionEvent ignoredEvent) throws IOException {
        aboutWindow = new AboutWindow(this);
    }

    @FXML
    void quit(ActionEvent ignoredEvent) {
        Platform.exit();
    }

    private String[][] getParamsString() throws IllegalArgumentException{
        if(parameters == null) throw new IllegalArgumentException("Simulation parameters are uninitialized");

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

    private void writeXSSFSheet(XSSFWorkbook workbook, ProjectileModel model){
        XSSFSheet sheet = workbook.createSheet();
        for(int i = 0; i < dataCount + 5; i++) sheet.createRow(i);
        for(int i = 1; i <= 3; i++) sheet.setColumnWidth(i, 5000);

        try {
            String[][] paramData = getParamsString();

            for(int i = 0; i < paramData.length; i++){
                XSSFRow row = (XSSFRow) sheet.getRow(i + 1);
                for(int j = 0; j < paramData[0].length; j++){
                    Cell cell =  row.createCell(j + 1);
                    cell.setCellValue(paramData[i][j]);
                }
            }
        }catch(IllegalArgumentException e){
            System.out.println(e.getLocalizedMessage());
            return;
        }


        model.plot(dataCount);
        ArrayList<Vector> dataPlot = model.getDataPlot();
        ArrayList<Double> timePlot = model.getTimePlot();

        System.out.println("Here");

        XSSFRow headerRow = (XSSFRow) sheet.getRow(1);
        headerRow.createCell(5).setCellValue("Time:");
        headerRow.createCell(6).setCellValue("Distance:");
        headerRow.createCell(7).setCellValue("Height:");

        for(int i = 0; i < dataPlot.size(); i++){
            XSSFRow row = (XSSFRow) sheet.getRow(i + 2);
            row.createCell(5).setCellValue(timePlot.get(i));
            row.createCell(6).setCellValue(dataPlot.get(i).x);
            row.createCell(7).setCellValue(dataPlot.get(i).y);
        }

        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 9, 1, 24, 30);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Trajectory");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Distance/Time");

        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Height");

        XDDFNumericalDataSource<Double> timeData = XDDFDataSourcesFactory.fromNumericCellRange(
                sheet, new CellRangeAddress(2, dataCount + 1, 5, 5)
        );
        XDDFNumericalDataSource<Double> distanceData = XDDFDataSourcesFactory.fromNumericCellRange(
                sheet, new CellRangeAddress(2, dataCount + 1, 6, 6)
        );
        XDDFNumericalDataSource<Double> heightData = XDDFDataSourcesFactory.fromNumericCellRange(
                sheet, new CellRangeAddress(2, dataCount + 1, 7, 7)
        );

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(timeData, heightData);
        series1.setTitle("Trajectory", null);
        series1.setSmooth(true);
        series1.setMarkerStyle(MarkerStyle.CIRCLE);

        XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.addSeries(distanceData, heightData);
        series2.setTitle("Trajectory", null);
        series2.setSmooth(true);
        series2.setMarkerStyle(MarkerStyle.CIRCLE);

        chart.plot(data);
    }

}

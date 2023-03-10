package com.example.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

public class AppWindow extends Application implements EventHandler<KeyEvent> {
    @FXML
    private ArrayList<TextField> formTextFields;
    private ArrayList<Double> formData;
    private TextField resultTextField;
    @FXML
    private Canvas trajectoryCanvas;
    private Vector canvasMousePosition;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        this.formTextFields = new ArrayList<>();
        this.formData = new ArrayList<>();
        this.resultTextField = null;
        this.trajectoryCanvas = null;
        this.canvasMousePosition = new Vector(0, 0);

        FXMLLoader loader = new FXMLLoader(AppWindow.class.getResource("projectileWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        ArrayList<Node> nodes = getNodes(scene.getRoot());
        for(Node node : nodes){
            if(node instanceof TextField textField){
                if(textField.isEditable()) {
                    formTextFields.add(textField);
                    textField.setOnKeyReleased(this);
                }else{
                    resultTextField = textField;
                }
            }
            if(node instanceof Canvas canvas){
                this.trajectoryCanvas = canvas;
                canvas.setOnMouseMoved(mouseEvent -> {
                        canvasMousePosition = new Vector(
                                mouseEvent.getX(),
                                mouseEvent.getY()
                        );
                        drawGraphPointDetails();
                    }
                );
                this.clearCanvas();
            }
        }

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private ArrayList<Node> getNodes(Parent parent){
        ArrayList<Node> nodes = new ArrayList<>();
        for(Node node : parent.getChildrenUnmodifiable()){
            nodes.add(node);
            if(node instanceof Parent nodeParent){
                nodes.addAll(getNodes(nodeParent));
            }
        }
        return nodes;
    }

    @Override
    public void handle(KeyEvent actionEvent) {
        Node actionCaller = (Node) actionEvent.getSource();
        if(actionCaller instanceof TextField) {
            updateFormData();
        }
    }
    ArrayList<Vector> measures = null;
    Vector maxMeasure = null;
    private void updateFormData() {
        formData = new ArrayList<>();
        for (TextField textField : this.formTextFields) {
            String text = textField.getText();
            if(!text.isEmpty()) {
                Double textData = Double.parseDouble(text);
                formData.add(textData);
            }
        }

        if (formData.size() == 6){
            ProjectileModel model = new ProjectileModel(formData.get(0), formData.get(1), formData.get(2), formData.get(3), formData.get(4), formData.get(5));
            this.measures = model.plotTrajectory(100);

            this.maxMeasure = new Vector(0, 0);
            for (Vector measure : measures) {
                if (maxMeasure.x < measure.x) maxMeasure.x = measure.x;
                if (maxMeasure.y < measure.y) maxMeasure.y = measure.y;
            }
            String resultText = String.format("%8.2f", maxMeasure.x);
            resultTextField.setText(resultText);
            updateChart();
        }
    }

    private void updateChart(){
        this.clearCanvas();
        this.drawGraphBase();
        this.drawGraphPoints();
    }

    private void clearCanvas(){
        GraphicsContext gc = trajectoryCanvas.getGraphicsContext2D();
        double canvasWidth = trajectoryCanvas.getWidth();
        double canvasHeight = trajectoryCanvas.getHeight();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    private void drawGraphBase(){
        double canvasWidth = trajectoryCanvas.getWidth();
        double canvasHeight = trajectoryCanvas.getHeight();
        double canvasMargin = 40;

        double graphOriginX = 2 * canvasMargin;
        double graphOriginY = canvasHeight - canvasMargin;
        double graphWidth = canvasWidth - 4 * canvasMargin;
        double graphHeight = canvasHeight - 2 * canvasMargin;

        double graphMeasureLineLength = 10;
        double graphMeasureCountX = 10;
        double graphMeasureCountY = 5;
        double measureGapX = graphWidth / graphMeasureCountX;
        double measureGapY = graphHeight / graphMeasureCountY;

        GraphicsContext gc = trajectoryCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.strokeLine(graphOriginX, graphOriginY, graphOriginX + graphWidth, graphOriginY);
        gc.strokeLine(graphOriginX, graphOriginY, graphOriginX, graphOriginY - graphHeight);

        for(int i = 1; i <= graphMeasureCountX; i++){
            gc.strokeLine(
                    graphOriginX + i * measureGapX,
                    graphOriginY - graphMeasureLineLength,
                    graphOriginX + i * measureGapX,
                    graphOriginY + graphMeasureLineLength
            );
            gc.strokeText(
                    String.format("%7.2f", maxMeasure.x * i / graphMeasureCountX),
                    graphOriginX + i * measureGapX - 20,
                    graphOriginY + canvasMargin / 2
            );
        }

        for(int i = 1; i <= graphMeasureCountY; i++){
            gc.strokeLine(
                    graphOriginX - graphMeasureLineLength,
                    graphOriginY - i * measureGapY,
                    graphOriginX + graphMeasureLineLength,
                    graphOriginY - i * measureGapY
            );
            gc.strokeText(
                    String.format("%7.2f", maxMeasure.y * i / graphMeasureCountY),
                    graphOriginX - 1.75 * canvasMargin,
                    graphOriginY - i * measureGapY + 5
            );
        }
    }

    ArrayList<Vector> trajectoryDrawnPoints = new ArrayList<>();
    ArrayList<Vector> graphPlotPoints = new ArrayList<>();
    private void drawGraphPoints(){
        double canvasWidth = trajectoryCanvas.getWidth();
        double canvasHeight = trajectoryCanvas.getHeight();
        double canvasMargin = 40;

        double graphOriginX = 2 * canvasMargin;
        double graphOriginY = canvasHeight - canvasMargin;
        double graphWidth = canvasWidth - 4 * canvasMargin;
        double graphHeight = canvasHeight - 2 * canvasMargin;

        double graphMeasureSize = 4;

        graphPlotPoints = new ArrayList<>();
        for(Vector measure: measures){
            graphPlotPoints.add(new Vector(
                   graphOriginX + graphWidth * (measure.x / maxMeasure.x),
                   graphOriginY - graphHeight * (measure.y / maxMeasure.y)
            ));
        }

        GraphicsContext gc = trajectoryCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        for(Vector plotPoint : graphPlotPoints){
            gc.strokeRect(
                    plotPoint.x - graphMeasureSize / 2,
                    plotPoint.y - graphMeasureSize / 2,
                    graphMeasureSize,
                    graphMeasureSize
            );
        }
        for(int i = 1; i < graphPlotPoints.size(); i++){
            Vector graphLineStartPoint = graphPlotPoints.get(i - 1);
            Vector graphLineEndPoint = graphPlotPoints.get(i);
            gc.strokeLine(
                    graphLineStartPoint.x,
                    graphLineStartPoint.y,
                    graphLineEndPoint.x,
                    graphLineEndPoint.y
            );
        }

    }


    private void drawGraphPointDetails(){
        double canvasWidth = trajectoryCanvas.getWidth();
        double canvasHeight = trajectoryCanvas.getHeight();
        double canvasMargin = 40;

        double graphOriginX = 2 * canvasMargin;
        double graphOriginY = canvasHeight - canvasMargin;
        double graphWidth = canvasWidth - 4 * canvasMargin;

        double graphPointDistance = graphWidth / graphPlotPoints.size();
        double mousePositionX = canvasMousePosition.x;
        double mouseGraphPosition = mousePositionX - graphOriginX;
        double mousePointPosition = Math.round(mouseGraphPosition / graphPointDistance);

        try {
            Vector selectedGraphPoint = graphPlotPoints.get((int) mousePointPosition);
            Vector selectedTrajectoryPoint = measures.get((int) mousePointPosition);

            this.updateChart();

            if (selectedGraphPoint != null && selectedTrajectoryPoint != null) {
                GraphicsContext gc = this.trajectoryCanvas.getGraphicsContext2D();
                gc.setFill(Color.BLUE);
                gc.strokeText(
                        String.format("X: %7.2fm\nY: %7.2fm", selectedTrajectoryPoint.x, selectedTrajectoryPoint.y),
                        selectedGraphPoint.x + 10,
                        selectedGraphPoint.y - 30
                );
                gc.strokeLine(
                        selectedGraphPoint.x,
                        selectedGraphPoint.y,
                        selectedGraphPoint.x,
                        graphOriginY
                );
            }

        }catch(IndexOutOfBoundsException | NullPointerException ignored){

        }

    }

}

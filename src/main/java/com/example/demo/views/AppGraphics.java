package com.example.demo.views;

import com.example.demo.logic.ProjectileModel;
import com.example.demo.logic.Vector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class AppGraphics {
    private final Canvas canvas;
    public Vector canvasMousePosition;
    public ArrayList<Vector> dataPlot = null;
    public ArrayList<Double> timePlot = null;
    public Vector maxMeasure = null;

    private ArrayList<Vector> graphPlotPoints = new ArrayList<>();
    private final Color bgColor = Color.valueOf("#111");
    private final Color darkgreen = Color.DARKGREEN;
    private final Color green = Color.GREEN;
    private final Color lightgreen = Color.LIGHTGREEN;

    public AppGraphics(Canvas canvas) {
        this.canvas = canvas;
        canvas.setOnMouseMoved(mouseEvent -> {
            this.canvasMousePosition = new Vector(
                    mouseEvent.getX(),
                    mouseEvent.getY()
            );
            this.drawGraphPointDetails();
        });
        this.clearCanvas();
    }

    public void updateParams(double[] formData){
        if (formData.length == 7){
            ProjectileModel model = new ProjectileModel(formData[0], formData[1], formData[2], formData[3], formData[4], formData[5]);

            int dataPoints = 100;
            model.plot(dataPoints);
            this.dataPlot = model.getDataPlot();
            this.timePlot = model.getTimePlot();


            this.maxMeasure = new Vector(0, 0);
            for (Vector measure : dataPlot) {
                if (maxMeasure.x < measure.x) maxMeasure.x = measure.x;
                if (maxMeasure.y < measure.y) maxMeasure.y = measure.y;
            }

            this.updateChart();
        }
    }

    private void updateChart(){
        this.clearCanvas();
        this.drawGraphBase();
        this.drawGraphPoints();
    }

    public void clearCanvas(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    public void drawGraphBase(){
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
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

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(darkgreen);
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

    public void drawGraphPoints(){
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double canvasMargin = 40;

        double graphOriginX = 2 * canvasMargin;
        double graphOriginY = canvasHeight - canvasMargin;
        double graphWidth = canvasWidth - 4 * canvasMargin;
        double graphHeight = canvasHeight - 2 * canvasMargin;

        double graphPointSize = 4;

        graphPlotPoints = new ArrayList<>();
        for(Vector measure: dataPlot){
            graphPlotPoints.add(new Vector(
                    graphOriginX + graphWidth * (measure.x / maxMeasure.x),
                    graphOriginY - graphHeight * (measure.y / maxMeasure.y)
            ));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(green);
        for(Vector plotPoint : graphPlotPoints){
            gc.strokeOval(
                    plotPoint.x - graphPointSize / 2,
                    plotPoint.y - graphPointSize / 2,
                    graphPointSize,
                    graphPointSize
            );
        }
        gc.setStroke(darkgreen);
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

    public void drawGraphPointDetails(){
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double canvasMargin = 40;

        double graphOriginY = canvasHeight - canvasMargin;
        double graphWidth = canvasWidth - 4 * canvasMargin;
        double mousePositionX = canvasMousePosition.x;

        int closestPointIndex = 0;
        double closestDistance = graphWidth;
        for(int i = 0; i < graphPlotPoints.size(); i++){
            Vector point = graphPlotPoints.get(i);
            double distance = Math.abs(mousePositionX - point.x);
            if(distance < closestDistance){
                closestPointIndex = i;
                closestDistance = distance;
            }
        }

        try {
            Vector selectedGraphPoint = graphPlotPoints.get(closestPointIndex);
            Vector selectedTrajectoryPoint = dataPlot.get(closestPointIndex);
            double selectedTimePoint = timePlot.get(closestPointIndex);

            this.updateChart();

            if (selectedGraphPoint != null && selectedTrajectoryPoint != null) {
                GraphicsContext gc = this.canvas.getGraphicsContext2D();

                gc.setStroke(lightgreen);
                gc.strokeText(
                        String.format("t:   %7.2f s\nX: %7.2f m\nY: %7.2f m", selectedTimePoint, selectedTrajectoryPoint.x,  selectedTrajectoryPoint.y),
                        selectedGraphPoint.x + 10,
                        selectedGraphPoint.y - 30
                );
                gc.setStroke(green);
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

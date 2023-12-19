package com.example.demo.logic;

import java.util.ArrayList;

public class ProjectileModel {
    private final double gravity = 9.81;
    private final double dragCoefficient = 0.05;
    private final double delta = 0.01;
    private final Vector startingVelocity;
    private final Vector startingPosition;
    private final double projectileMass;
    private double projectileDiameter = 0;
    private double airDensity = 0;

    private ArrayList<Vector> dataPlot;
    private ArrayList<Double> timePlot;

    public ProjectileModel(double startingVelocity, double startingAngle, double startingHeight, double projectileMass, double projectileDiameterInMM, double airDensity){
        this.startingVelocity = new Vector(
                startingVelocity * Math.cos(Math.toRadians(startingAngle)),
                startingVelocity * Math.sin(Math.toRadians(startingAngle))
        );
        this.startingPosition = new Vector(0, startingHeight);
        this.projectileMass = projectileMass / 1000.0;
        this.projectileDiameter = projectileDiameterInMM / 1000;
        this.airDensity = (airDensity != 0)? airDensity : 1.204;

        this.dataPlot = new ArrayList<>();
        this.timePlot = new ArrayList<>();
    }

    public ArrayList<Vector> getDataPlot() {return dataPlot;}
    public ArrayList<Double> getTimePlot() {return timePlot;}


    public void plot(int plotPointCount) {
        Vector currentPosition = new Vector(startingPosition.x, startingPosition.y);
        Vector currentVelocity = startingVelocity;
        double currentTime = 0;

        double projectileArea = Math.PI * Math.pow(projectileDiameter, 2);
        double dragConst = 0.5 * dragCoefficient * airDensity * projectileArea;

        while(true){
            dataPlot.add(new Vector(currentPosition.x, currentPosition.y));
            timePlot.add(currentTime);

            currentPosition.x += currentVelocity.x * delta;
            currentPosition.y += currentVelocity.y * delta;

            double drag = dragConst * Math.pow(currentVelocity.intensity(), 2);
            double deceleration = drag / projectileMass * delta;

            currentVelocity.x -= deceleration * Math.cos(currentVelocity.angle());
            currentVelocity.y -= deceleration * Math.sin(currentVelocity.angle()) * Math.signum(currentVelocity.y);
            currentVelocity.y -= gravity * delta;

            if(finishedMoving(currentVelocity, currentPosition)) {
                dataPlot = Interpolation.interpolatePositionTrajectory(dataPlot, plotPointCount);
                dataPlot.add(currentPosition);
                timePlot = Interpolation.interpolateRealTrajectory(timePlot, plotPointCount);
                timePlot.add(currentTime);
                return;
            }

            currentTime += delta;
        }
    }

    private boolean finishedMoving(Vector currentVelocity, Vector currentPosition) {
        return currentVelocity.y < 0 && currentPosition.y < 0;
    }

}

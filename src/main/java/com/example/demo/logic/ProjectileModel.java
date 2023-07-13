package com.example.demo.logic;

import java.util.ArrayList;

public class ProjectileModel {
    private final double delta = 0.01;
    private final Vector startingVelocity;
    private final Vector startingPosition;
    private final double projectileMass;
    private double projectileDiameter = 0;
    private double airDensity = 0;

    private int dataPoints;
    private ArrayList<Vector> trajectoryPlot;
    private ArrayList<Double> timePlot;
    public double distance;
    public double height;

    public ProjectileModel(double startingVelocity, double startingAngle, double startingHeight, double projectileMass, double projectileDiameterInMM, double airDensity){
        this.startingVelocity = new Vector(
                startingVelocity * Math.cos(Math.toRadians(startingAngle)),
                startingVelocity * Math.sin(Math.toRadians(startingAngle))
        );
        this.startingPosition = new Vector(0, startingHeight);
        this.projectileMass = projectileMass;
        this.projectileDiameter = projectileDiameterInMM / 1000;
        this.airDensity = (airDensity != 0)? airDensity : 1.204;

        this.dataPoints = 100;
        this.trajectoryPlot = new ArrayList<>();
        this.timePlot = new ArrayList<>();
        this.distance = 0;
        this.height = startingHeight;
    }

    public ArrayList<Vector> getTrajectoryPlot() {return trajectoryPlot;}

    public ArrayList<Double> getTimePlot() {return timePlot;}

    public void setDataPoints(int dataPoints) {this.dataPoints = dataPoints;}

    public void plotTrajectory() {
        Vector currentPosition = new Vector(startingPosition.x, startingPosition.y);
        Vector previousPosition = new Vector(startingPosition.x, startingPosition.y);
        Vector currentVelocity = startingVelocity;

        double projectileArea = Math.PI * Math.pow(projectileDiameter, 2);
        double dragCoefficient = 0.0052834;
        double airResistance = dragCoefficient * airDensity * projectileArea * delta / (2 * projectileMass);

        ArrayList<Vector> trajectory = new ArrayList<>();
        ArrayList<Double> time = new ArrayList<>();

        int i = 0;
        while(true){
            trajectory.add(new Vector(currentPosition.x, currentPosition.y));
            time.add(delta * (i++));

            currentPosition.x += currentVelocity.x * delta;
            currentPosition.y += currentVelocity.y * delta;

            currentVelocity.x -= airResistance * Math.pow(currentVelocity.x, 2);
            currentVelocity.y -= airResistance * Math.pow(currentVelocity.y, 2) * Math.signum(currentVelocity.y);
            double gravity = 9.81;
            currentVelocity.y -= gravity * delta;

            if(finishedMoving(currentVelocity, currentPosition)) {
                time.add(delta * i);
                trajectory.add(currentPosition);
                this.timePlot = Interpolation.interpolateRealTrajectory(time, dataPoints);
                this.trajectoryPlot = Interpolation.interpolatePositionTrajectory(trajectory, dataPoints);
                return;
            }

            if(distance < currentPosition.x) distance = currentPosition.x;
            if(height < currentPosition.y) height = currentPosition.y;

            previousPosition.x = currentPosition.x;
            previousPosition.y = currentPosition.y;
        }
    }

    private boolean finishedMoving(Vector currentVelocity, Vector currentPosition) {
        return currentVelocity.y < 0 && currentPosition.y < 0;
    }

}

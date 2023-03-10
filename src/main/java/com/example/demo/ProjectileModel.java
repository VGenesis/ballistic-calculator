package com.example.demo;

import java.util.ArrayList;

public class ProjectileModel {
    private final double gravity = 9.81;
    private final double dragCoefficient = 0.0052834;
    private final double delta = 0.01;
    private final Vector startingVelocity;
    private final Vector startingPosition;
    private final double projectileMass;
    private double projectileDiameter = 0;
    private double airDensity = 0;

    public ProjectileModel(double startingVelocity, double startingAngle, double startingHeight, double projectileMass, double projectileDiameterInMM, double airDensity){
        this.startingVelocity = new Vector(
                startingVelocity * Math.cos(Math.toRadians(startingAngle)),
                startingVelocity * Math.sin(Math.toRadians(startingAngle))
        );
        this.startingPosition = new Vector(0, startingHeight);
        this.projectileMass = projectileMass;
        this.projectileDiameter = projectileDiameterInMM / 1000;
        this.airDensity = (airDensity != 0)? airDensity : 1.204;
    }

    public ArrayList<Vector> plotTrajectory(int plotPointCount) {
        Vector currentPosition = new Vector(startingPosition.x, startingPosition.y);
        Vector previousPosition = new Vector(startingPosition.x, startingPosition.y);
        Vector currentVelocity = startingVelocity;

        double projectileArea = Math.PI * Math.pow(projectileDiameter, 2);
        double airResistance = dragCoefficient * airDensity * projectileArea * delta / 2 / projectileMass;

        ArrayList<Vector> trajectory = new ArrayList<>();

        while(true){
            trajectory.add(new Vector(currentPosition.x, currentPosition.y));

            currentPosition.x += currentVelocity.x * delta;
            currentPosition.y += currentVelocity.y * delta;

            currentVelocity.x -= airResistance * Math.pow(currentVelocity.x, 2);
            currentVelocity.y -= airResistance * Math.pow(currentVelocity.y, 2) * Math.signum(currentVelocity.y);
            currentVelocity.y -= gravity * delta;

            if(finishedMoving(currentVelocity, currentPosition)) {
                ArrayList<Vector> trajectoryPlot = Interpolation.interpolateTrajectory(trajectory, plotPointCount);
                trajectoryPlot.add(currentPosition);
                return trajectoryPlot;
            }

            previousPosition.x = currentPosition.x;
            previousPosition.y = currentPosition.y;
        }
    }

    private boolean finishedMoving(Vector currentVelocity, Vector currentPosition) {
        return currentVelocity.y < 0 && currentPosition.y < 0;
    }

}

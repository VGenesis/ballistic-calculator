package com.example.demo;

import java.util.ArrayList;

public class Interpolation {
    public static ArrayList<Vector> interpolateTrajectory(ArrayList<Vector> trajectory, int dataCount){
        ArrayList<Vector> interpolatedTrajectory = new ArrayList<>();

        for(double i = 0; i < dataCount; i++){
            interpolatedTrajectory.add(interpolateData(i / dataCount, trajectory));
        }

        return interpolatedTrajectory;
    }

    private static Vector interpolateData(double delta, ArrayList<Vector> data){
        double maxData = 0;
        for(Vector v : data){
            maxData = Math.max(v.x, maxData);
        }

        ArrayList<Double> deltaData = new ArrayList<>();
        for(Vector v : data){
            deltaData.add(v.x / maxData);
        }

        int hit = 1;
        while(deltaData.get(hit) < delta) hit++;

        Vector firstHigher = data.get(hit);
        Vector lastLower = data.get(hit - 1);

        double deltaInterval = deltaData.get(hit) - deltaData.get(hit - 1);
        double intervalDelta = (delta - deltaData.get(hit - 1)) / deltaInterval;

        return new Vector(
                interpolate(intervalDelta, lastLower.x, firstHigher.x),
                interpolate(intervalDelta, lastLower.y, firstHigher.y)
        );
    }

    private static double interpolate(double delta, double minValue, double maxValue){
        double range = maxValue - minValue;
        return minValue + delta * range;
    }
}

package com.example.demo.logic;

import java.util.ArrayList;

public class Interpolation {
    public static ArrayList<Double> interpolateRealTrajectory(ArrayList<Double> trajectory, int dataCount){
        if(trajectory.size() < dataCount) return trajectory;

        ArrayList<Double> interpolatedTrajectory = new ArrayList<>();
        for(double i = 0; i < dataCount; i++){
            interpolatedTrajectory.add(interpolateRealData(i / dataCount, trajectory));
        }
        return interpolatedTrajectory;
    }

    public static ArrayList<Vector> interpolatePositionTrajectory(ArrayList<Vector> trajectory, int dataCount){
        if(trajectory.size() < dataCount) return trajectory;

        ArrayList<Vector> interpolatedTrajectory = new ArrayList<>();
        for(double i = 0; i < dataCount; i++){
            interpolatedTrajectory.add(interpolatePositionData(i / dataCount, trajectory));
        }
        return interpolatedTrajectory;
    }

    private static double interpolateRealData(double delta, ArrayList<Double> data){
        double maxData = 0;
        for(double v : data){
            maxData = Math.max(v, maxData);
        }

        ArrayList<Double> deltaData = new ArrayList<>();
        for(double v : data){
            deltaData.add(v / maxData);
        }
        double ind = delta * data.size();
        int indL = (int)Math.floor(ind);
        int indH = (int)Math.ceil(ind);
        if(indH == indL) return data.get(indH);

        double firstHigher = data.get(indH);
        double lastLower = data.get(indL);

        double deltaInterval = deltaData.get(indH) - deltaData.get(indL);
        double intervalDelta = (delta - deltaData.get(indL)) / deltaInterval;

        return interpolate(intervalDelta, lastLower, firstHigher);
    }

    private static Vector interpolatePositionData(double delta, ArrayList<Vector> data){
        double maxData = 0;
        for(Vector v : data){
            maxData = Math.max(v.x, maxData);
        }

        ArrayList<Double> deltaData = new ArrayList<>();
        for(Vector v : data){
            deltaData.add(v.x / maxData);
        }

        double ind = delta * data.size();
        int indL = (int)Math.floor(ind);
        int indH = (int)Math.ceil(ind);
        if(indH == indL) return data.get(indH);

        Vector firstHigher = data.get(indH);
        Vector lastLower = data.get(indL);

        double deltaInterval = deltaData.get(indH) - deltaData.get(indL);
        double intervalDelta = (delta - deltaData.get(indL)) / deltaInterval;

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

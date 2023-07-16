package com.example.demo.logic;

import java.util.ArrayList;

public class Interpolation {
    public static ArrayList<Double> interpolateRealTrajectory(ArrayList<Double> trajectory, int dataCount){
        if(trajectory.size() < dataCount) return trajectory;

        ArrayList<Double> interpolatedTrajectory = new ArrayList<>();
        for(int i = 0; i < dataCount; i++){
            interpolatedTrajectory.add(interpolateRealData(i, dataCount, trajectory));
        }
        return interpolatedTrajectory;
    }

    public static ArrayList<Vector> interpolatePositionTrajectory(ArrayList<Vector> trajectory, int dataCount){
        if(trajectory.size() < dataCount) return trajectory;

        ArrayList<Vector> interpolatedTrajectory = new ArrayList<>();
        for(int i = 0; i < dataCount; i++){
            interpolatedTrajectory.add(interpolatePositionData(i, dataCount, trajectory));
        }
        return interpolatedTrajectory;
    }

    private static double interpolateRealData(int i, int size, ArrayList<Double> data){
        double ind = data.size() * (double) i / (double) size;

        int indL = (int) Math.floor(ind);
        int indH = (int) Math.ceil(ind);
        if(indL == indH) return data.get(indL);

        return interpolate(ind - indL, data.get(indL), data.get(indH));
    }

    private static Vector interpolatePositionData(int i, int size, ArrayList<Vector> data){
        double ind = data.size() * (double) i / (double) size;

        int indL = (int) Math.floor(ind);
        int indH = (int) Math.ceil(ind);
        if(indL == indH) return data.get(indL);

        double delta = ind - indL;
        Vector low = data.get(indL);
        Vector high = data.get(indH);
        return new Vector(
                interpolate(delta, low.x, high.x),
                interpolate(delta, low.y, high.y)
        );
    }

    private static double interpolate(double delta, double minValue, double maxValue){
        double range = maxValue - minValue;
        return minValue + delta * range;
    }
}

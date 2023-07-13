package com.example.demo.logic;

import java.util.ArrayList;

public class Conversions {
    public static double[][] vectorToMatrix(ArrayList<Vector> array){
        double[][] res = new double[2][array.size()];
        for(int i = 0; i < array.size(); i++){
            res[0][i] = array.get(i).x;
            res[1][i] = array.get(i).y;
        }
        return res;
    }

    public static double[] listToArray(ArrayList<Double> array){
        double[] res = new double[array.size()];
        for(int i = 0; i < array.size(); i++){
                res[i] = array.get(i);
        }
        return res;
    }

    public static String[][] matrixToString(double[][] array, int precision){
        String[][] res = new String[array.length][array[0].length];
        String format = "%" + String.format(".%d", precision) + "f";
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j < array[0].length; j++){
                res[i][j] = String.format(format, array[i][j]);
            }
        }
        return res;
    }

}

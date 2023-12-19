package com.example.demo.logic;

public class Vector {
    public double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double intensity(){
        return Math.sqrt(x*x + y*y);
    }

    public double angle(){
        return Math.atan2(y, x);
    }

    @Override
    public String toString(){
        return "[ " + x + ", " + y + "]";
    }
}

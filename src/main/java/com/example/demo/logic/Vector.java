package com.example.demo.logic;

public class Vector {
    public double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "[ " + x + ", " + y + "]";
    }
}

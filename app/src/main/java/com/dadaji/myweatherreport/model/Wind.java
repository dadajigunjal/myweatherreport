package com.dadaji.myweatherreport.model;

import java.io.Serializable;

public class Wind implements Serializable {
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getDeg() {
        return deg;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "speed=" + speed +
                ", deg=" + deg +
                '}';
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public double speed;
    public int deg;
}

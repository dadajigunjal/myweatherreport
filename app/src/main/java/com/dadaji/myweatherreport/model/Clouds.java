package com.dadaji.myweatherreport.model;

import java.io.Serializable;

public class Clouds implements Serializable {
    public int getAll() {
        return all;
    }

    @Override
    public String toString() {
        return "Clouds{" +
                "all=" + all +
                '}';
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int all;
}

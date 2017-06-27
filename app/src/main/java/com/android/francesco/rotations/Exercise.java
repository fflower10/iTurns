package com.android.francesco.rotations;

/**
 * Created by Francesco on 18/05/2017.
 */

public class Exercise {

    private String name;
    private int numSeries;
    private int numRips;
    private double restTime;
    //private Uri _imageURI;
    private int id;

    public Exercise(String name, int numSeries, int numRips, double restTime, int id) {
        this.name = name;
        this.numSeries = numSeries;
        this.numRips = numRips;
        this.restTime = restTime;
        this.id = id;
    }

    public Exercise(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumSeries() {
        return numSeries;
    }

    public void setNumSeries(int numSeries) {
        this.numSeries = numSeries;
    }

    public int getNumRips() {
        return numRips;
    }

    public void setNumRips(int numRips) {
        this.numRips = numRips;
    }

    public double getRestTime() {
        return restTime;
    }

    public void setRestTime(double restTime) {
        this.restTime = restTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
package de.ami.team1.contacttracking.util;

/**
 * Representation of a point on a coordinate grid
 */
public class Point {
    public double x;
    public double y;

    /**
     * Constructor for a Point with x and y coordinate
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * default Constructor
     */
    public Point(){

    }

    //getter and setter
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}

package de.ami.team1.contacttracking.util;

/**
 * Wrapperclass for a Line representation with all values needed for caluclating intersections
 */
public class Line {
    private double x1, x2, y1, y2, m, b;

    /**
     * Constructor for a line representation
     *
     * @param x1 double x1
     * @param y1 double y1
     * @param x2 double x2
     * @param y2 double y2
     * @param m  double m
     * @param b  double b
     */
    public Line(double x1, double y1, double x2, double y2, double m, double b) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.m = m;
        this.b = b;
    }

    /**
     * Default Constructors
     */
    public Line() {

    }

    //getter and setter
    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
}

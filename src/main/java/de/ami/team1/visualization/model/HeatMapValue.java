package de.ami.team1.visualization.model;


public class HeatMapValue extends MapPoint {

    private double intensity;

    public HeatMapValue() {
    }

    public HeatMapValue(double latitude, double longitude, double intensity) {
        super(latitude, longitude);
        this.intensity = intensity;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
}

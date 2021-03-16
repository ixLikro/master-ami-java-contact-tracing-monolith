package de.ami.team1.visualization.model;

import java.awt.Polygon;
import java.util.List;

public class Region {

    private String name;
    private int cases;
    private int population;
    private double sevenDayIncidence;
    private List<Polygon> polygons;
    private int featureIndex;
    private State state;
    private Chart chart;

    public Region(String name, int cases) {
        this.name = name;
        this.cases = cases;
        this.chart = new Chart();
    }

    public Region(String name, int cases, double sevenDayIncidence, List<Polygon> polygons, int featureIndex) {
        this.name = name;
        this.cases = cases;
        this.sevenDayIncidence = sevenDayIncidence;
        this.polygons = polygons;
        this.featureIndex = featureIndex;
        this.chart = new Chart();
    }

    public Region(String name, List<Polygon> polygons, int featureIndex) {
        this.name = name;
        this.cases = 0;
        this.sevenDayIncidence = 0;
        this.polygons = polygons;
        this.featureIndex = featureIndex;
        this.chart = new Chart();
    }

    public double getSevenDayIncidence() {
        if (sevenDayIncidence != 0 && population != 0) {
            double rate = sevenDayIncidence / population;
            rate *= 100000;
            double scale = Math.pow(10, 2);
            return Math.round(rate * scale) / scale;
        } else {
            return 0;
        }
    }

    public void incrementCases() {
        cases++;
    }

    public void incrementIncidence() {
        sevenDayIncidence++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }

    public void setFeatureIndex(int featureIndex) {
        this.featureIndex = featureIndex;
    }

    public void setSevenDayIncidence(double sevenDayIncidence) {
        this.sevenDayIncidence = sevenDayIncidence;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }
}

package de.ami.team1.visualization.model;

public enum ChartHistory {
    MONTH(5);

    private final int month;

    ChartHistory(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }
}

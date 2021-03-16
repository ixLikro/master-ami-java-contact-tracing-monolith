package de.ami.team1.util;

public class GridConverter {

    public static int toGrid(double xTude){ //Latitude or Longitude
        return (int) (xTude * 1000);
    };

    public static double toTude(int grid){ //Latitude or Longitude
        return ((double)grid / 1000);
    };
}

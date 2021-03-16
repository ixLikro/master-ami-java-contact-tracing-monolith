package de.ami.team1.dataacceptance.util;


/**
 * Default Interfaces for Calculations with Coordinates
 */
public interface CoordinatesInterface {
    //radius of earth in meter
    int R = 6373000;

    /**
     * Calculating the heading and distance between two coordinatepoints in decimal system
     *
     * @param lat1 latitude 1
     * @param lon1 longitude 1
     * @param lat2 latitude 2
     * @param lon2 longitude 2
     * @return DirectionStruct with heading and distance in meter
     */
    default DirectionStruct calcDirection(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double d = R * c;

        double x = Math.cos(lat2) * Math.sin(dLon);
        double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double heading = Math.toDegrees(Math.atan2(x, y));
        DirectionStruct struct = new DirectionStruct();
        struct.heading = heading;
        struct.length = d;

        return struct;

    }
}

package de.ami.team1.analyse;

import de.ami.team1.analyse.services.AnalyseMovementService;
import de.ami.team1.util.*;
import de.ami.team1.visualization.control.VisualizationDataProducer;
import de.ami.team1.visualization.model.HeatMapValue;
import de.ami.team1.visualization.model.MapPoint;
import de.ami.team1.visualization.model.Movement;
import de.ami.team1.visualization.model.User;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class VisualizationDataAnalyzer implements VisualizationDataProducer {

    @Inject
    AnalyseMovementService movementService;
    UserConverter userConv = new UserConverter();
    MovementConverter moveConv = new MovementConverter();

    /**
     * Retrieve the heatmap of a specific area. The area is defined as a rectangle between the given points.
     * @param p1 Point A
     * @param p2 Point B
     * @return List of HeatMapValue for each subarea between the given points.
     */
    public List<HeatMapValue> getHeatmapData(MapPoint p1, MapPoint p2) {
        double minLongitude, maxLongitude, minLatitude, maxLatitude;

        //1. convert map part
        if (p1.getLatitude() <= p2.getLatitude()) {
            minLatitude = p1.getLatitude();
            maxLatitude = p2.getLatitude();
        } else {
            minLatitude = p2.getLatitude();
            maxLatitude = p1.getLatitude();
        }
        if (p1.getLongitude() <= p2.getLongitude()) {
            minLongitude = p1.getLongitude();
            maxLongitude = p2.getLongitude();
        } else {
            minLongitude = p2.getLongitude();
            maxLongitude = p1.getLongitude();
        }

        //2. retrieve data from boundaries
        return movementService.readHeatMap(minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

    /**
     * Get the last 1000 movements from given user.
     * @param user User to get data from
     * @return List of Movement Points
     */
    public List<Movement> getMovementFromUser(User user) {

        List<Movement> ret = new ArrayList<>();
        List<de.ami.team1.dataacceptance.entities.Movement> movements
                = movementService.readMovementByUserID(userConv.toDA(user), 1000);
        for (de.ami.team1.dataacceptance.entities.Movement move : movements) {
            ret.add(moveConv.toV(move));
        }

        return  ret;
    }

    /**
     * Amount of users with matching filter.
     * @param emailFilter email filter
     * @return Amount of matching entries.
     */
    public int countUserWithFilter(String emailFilter) {
        return movementService.countUsersWithEmailFilter(emailFilter);
    }

    /**
     * Get users with a given filter and page.
     * @param filter filter for email address
     * @param page page
     * @param pageSize items per page
     * @return List of users.
     */
    public List<User> getUsersWithFilter(String filter, int page, int pageSize) {
        List<User> filteredUsers = userConv.toV(movementService.getUsersWithMailFilter(filter, page, pageSize));
        return  filteredUsers;
    }

    /**
     * Get the latest Point of a user.
     * @return List with the last movement of an infected user.
     */
    @Deprecated
    public List<MapPoint> getLatestCoordinatesByInfectedUsers() {
        return movementToMapPointList(movementService.readLatestMovementByInfectedUsers());
    }

    /**
     * Get the first Point of a user after an infection.
     * @return List with the first movement of an infected user.
     */
    @Deprecated
    public List<MapPoint> getFirstCoordinatesOfInfectedUsers() {
        return movementToMapPointList(movementService.readFirstPositionOfInfectedUsers());
    }

    /**
     * Get the coordinates of all infected users of the last x months.
     * @param minusMonth amount of months
     * @return List of Map Point from infected users.
     */
    public List<MapPoint> getCoordinatesOfInfectedUsersByMonth(int minusMonth) {
        LocalDate initial = LocalDate.now();
        if (minusMonth != 0) {
            initial = initial.minusMonths(minusMonth);
        }
        LocalDate start = initial.withDayOfMonth(1);
        LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        List<de.ami.team1.dataacceptance.entities.Movement> activeCases = new ArrayList<>();
        List<de.ami.team1.dataacceptance.entities.User> infected = movementService.readInfectedUsers(start, end);
        for (de.ami.team1.dataacceptance.entities.User user : infected) {
            activeCases.add(movementService.readLatestMovementByUserID(user));
        }
        return movementToMapPointList(activeCases);
    }

    /**
     * Convert Movement List ot MapPoint List
     * @param movements movements to parse
     * @return map point list representation
     */
    private List<MapPoint> movementToMapPointList(List<de.ami.team1.dataacceptance.entities.Movement> movements) {
        List<MapPoint> mapPoints = new ArrayList<>();
        if (movements != null) {
            for (de.ami.team1.dataacceptance.entities.Movement movement : movements) {
                if (movement != null) {
                    mapPoints.add(new MapPoint(movement.getLatitude(), movement.getLongitude()));
                }
            }
            return mapPoints;
        } else {
            return null;
        }
    }

    public List<List<MapPoint>> readLatestMovementByInfectedUsers2() {
        List<de.ami.team1.dataacceptance.entities.User> infected = movementService.readInfectedUsers();
        List<List<MapPoint>> trendData = new ArrayList<>();
        List<de.ami.team1.dataacceptance.entities.Movement> activeCases = new ArrayList<>();
        List<de.ami.team1.dataacceptance.entities.Movement> incidences = new ArrayList<>();
        for (de.ami.team1.dataacceptance.entities.User user : infected) {
            activeCases.add(movementService.readLatestMovementByUserID(user));
            incidences.add(movementService.readFirstMovementByInfectedUser(user));
        }
        trendData.add(movementToMapPointList(activeCases));
        trendData.add(movementToMapPointList(incidences));
        return trendData;
    }

}

package de.ami.team1.visualization.control;

import de.ami.team1.util.NamePool;
import de.ami.team1.util.RandomHelper;
import de.ami.team1.visualization.model.HeatMapValue;
import de.ami.team1.visualization.model.MapPoint;
import de.ami.team1.visualization.model.Movement;
import de.ami.team1.visualization.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Vetoed;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * For debugging purposes, this data will be provided by the analyse block
 */
@ApplicationScoped
@Vetoed
public class RandomDataGenerator implements VisualizationDataProducer {

    // heatmap
    private static final int HEATMAP_DATA_POINTS_TO_GENERATE = 100000;

    //movement
    private static final int MAX_MOVEMENT_DATA_POINTS_TO_GENERATE = 8000;
    private static final int MIN_MOVEMENT_DATA_POINTS_TO_GENERATE = 200;
    private static final double MIN_START_MOVEMENT_LATITUDE = 51.49383;
    private static final double MIN_START_MOVEMENT_LONGITUDE = 7.20629;
    private static final double MAX_START_MOVEMENT_LATITUDE = 53.37013;
    private static final double MAX_START_MOVEMENT_LONGITUDE = 10.92257;
    private static final double MIN_MOVEMENT_PER_TICK = 0.00009;
    private static final double MAX_MOVEMENT_PER_TICK = 0.00199;

    //user
    private static final int USER_POOL_SIZE= 10000;
    private final List<User> allUsers;

    public RandomDataGenerator() {
        allUsers = new ArrayList<>();
        for (int i = 0; i < USER_POOL_SIZE; i++){
            String lastName = NamePool.getLastname();
            String firstName = null;
            char gender = ' ';
            if (RandomHelper.hitPercentChance(1d)){
                gender = 'd';
                firstName = RandomHelper.hitPercentChance(50d) ? NamePool.getFemaleFirstname() : NamePool.getMaleFirstname();
            }else {
                if(RandomHelper.hitPercentChance(50d)){
                    gender = 'm';
                    firstName = NamePool.getMaleFirstname();
                }else {
                    gender = 'w';
                    firstName = NamePool.getFemaleFirstname();
                }
            }
            allUsers.add(new User(i, firstName.toLowerCase()+"@"+lastName.toLowerCase()+".de", RandomHelper.getDateOfBirth(16, 120) ,gender, null));
        }
    }

    @Override
    public List<HeatMapValue> getHeatmapData(MapPoint p1, MapPoint p2) {
        double minLongitude, maxLongitude, minLatitude, maxLatitude;
        List<HeatMapValue> ret = new ArrayList<>();

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

        for (int i = 0; i < HEATMAP_DATA_POINTS_TO_GENERATE; i++) {
            ret.add(new HeatMapValue(
                    RandomHelper.getDouble(minLatitude, maxLatitude),
                    RandomHelper.getDouble(minLongitude, maxLongitude),
                    RandomHelper.getDouble(0.05d, 1d)));
        }

        return ret;
    }

    @Override
    public List<Movement> getMovementFromUser(User user) {
        MapPoint lastPoint = new MapPoint(
                RandomHelper.getDouble(MIN_START_MOVEMENT_LATITUDE, MAX_START_MOVEMENT_LATITUDE),
                RandomHelper.getDouble(MIN_START_MOVEMENT_LONGITUDE, MAX_START_MOVEMENT_LONGITUDE)
        );
        List<Movement> ret = new ArrayList<>();
        long startDate = new Date().getTime();
        int pointToGenerate = RandomHelper.getInteger(MIN_MOVEMENT_DATA_POINTS_TO_GENERATE, MAX_MOVEMENT_DATA_POINTS_TO_GENERATE);
        double direction = RandomHelper.getDouble(20, 80);

        for (int i = 0; i < pointToGenerate; i++) {
            double relativeChangeLatitude = RandomHelper.getDouble(MIN_MOVEMENT_PER_TICK, MAX_MOVEMENT_PER_TICK);
            double relativeChangeLongitude = RandomHelper.getDouble(MIN_MOVEMENT_PER_TICK, MAX_MOVEMENT_PER_TICK);
            if (RandomHelper.hitPercentChance(60d)) relativeChangeLatitude *= -1;
            if (RandomHelper.hitPercentChance(direction)) relativeChangeLongitude *= -1;
            MapPoint newPoint = new MapPoint(
                    lastPoint.getLatitude() + relativeChangeLatitude,
                    lastPoint.getLongitude() + relativeChangeLongitude
            );

            lastPoint = newPoint;
            ret.add(new Movement(user.getId(), newPoint, LocalDateTime.ofInstant(new Date(startDate + (i * 1000L * 60L)).toInstant(), ZoneId.systemDefault())));
        }
        return ret;
    }

    private List<User> filterUsers(String filter){
        List<User> ret = new ArrayList<>();
        for (User u: allUsers) {
            if(filter == null || filter.isBlank() || u.getMail().contains(filter.toLowerCase(Locale.ROOT))){
                ret.add(u);
            }
        }
        return ret;
    }

    @Override
    public int countUserWithFilter(String emailFilter) {
        return filterUsers(emailFilter).size();
    }

    @Override
    public List<User> getUsersWithFilter(String filter, int page, int pageSize) {
        List<User> filteredUsers = filterUsers(filter);
        int toIndex = (page + 1) * pageSize;
        if(toIndex > filteredUsers.size()){
            toIndex = filteredUsers.size();
        }

        return  filteredUsers.subList(page * pageSize, toIndex);
    }
}

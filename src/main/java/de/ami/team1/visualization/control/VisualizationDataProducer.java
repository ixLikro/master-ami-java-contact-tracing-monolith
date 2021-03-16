package de.ami.team1.visualization.control;

import de.ami.team1.visualization.model.HeatMapValue;
import de.ami.team1.visualization.model.MapPoint;
import de.ami.team1.visualization.model.Movement;
import de.ami.team1.visualization.model.User;

import javax.ejb.Stateless;
import javax.inject.Named;
import java.util.List;

@Stateless
public interface VisualizationDataProducer {

    /**
     * @param p1 boundaries {@link de.ami.team1.visualization.model.MapPoint} 1
     * @param p2 boundaries {@link de.ami.team1.visualization.model.MapPoint} 2
     * @return all {@link de.ami.team1.visualization.model.HeatMapValue}s between the given points
     */
    List<HeatMapValue> getHeatmapData(MapPoint p1, MapPoint p2);

    /**
     * @param user the {@link de.ami.team1.visualization.model.User} that {@link de.ami.team1.visualization.model.Movement}s should be loaded
     * @return all {@link de.ami.team1.visualization.model.Movement}-Objects from the given user
     */
    List<Movement> getMovementFromUser(User user);

    /**
     * @param emailFilter the filter string (search string) that should be applied.
     *                    If null or empty the count of total {@link de.ami.team1.visualization.model.User}s should be returned!
     * @return the count of {@link de.ami.team1.visualization.model.User}s, assuming the given filter is applied
     */
    int countUserWithFilter(String emailFilter);

    /**
     * @param filter   the filter string (search string) that should be applied.
     *                 If null or empty a page of all {@link de.ami.team1.visualization.model.User}s should be returned!
     * @param page     the given page index
     * @param pageSize the count of {@link de.ami.team1.visualization.model.User}s per page
     * @return a page of {@link de.ami.team1.visualization.model.User}s
     */
    List<User> getUsersWithFilter(String filter, int page, int pageSize);
}

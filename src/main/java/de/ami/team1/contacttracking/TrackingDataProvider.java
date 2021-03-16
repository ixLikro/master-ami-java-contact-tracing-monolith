package de.ami.team1.contacttracking;

import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides all Methods which are needed to load
 */
public interface TrackingDataProvider {

    /**
     * Returns all Movements of a given User which occurred after the given date ordered by Timestamp of Movement ascending
     *
     * @param user         infected User {@link User}
     * @param trackingDate {@link LocalDate}
     * @return List of Movement
     */
    List<Movement> getListOfUserMovemntsSinceDate(User user, LocalDate trackingDate);


    /**
     * Returns the first Movement of a given User which occurred after the given date
     *
     * @param user         infected User {@link User}
     * @param trackingDate {@link LocalDate}
     * @return {@link Movement}
     */
    Movement getFirstMovementOfDay(User user, LocalDate trackingDate);

    /**
     * Returns all tracked Movements with a given time interval and grid, but not for a given user
     *
     * @param x    Grid x
     * @param y    grid y
     * @param from Timestamp begin Interval
     * @param to   Timestamp end Interval
     * @param user User which shall not be tracked
     * @return List of Movement
     */
    List<Movement> getListOfMovementsInGridAndTimeIntervallWithoutUser(int x, int y, LocalDateTime from, LocalDateTime to, User user);
}

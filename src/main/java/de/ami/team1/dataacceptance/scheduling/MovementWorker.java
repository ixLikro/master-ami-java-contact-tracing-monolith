package de.ami.team1.dataacceptance.scheduling;


import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.services.MovementService;
import de.ami.team1.dataacceptance.services.RawMovementService;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.dataacceptance.util.DirectionStruct;
import de.ami.team1.dataacceptance.util.MovementHelper;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduler and Executer for clearing and converting the rawmovementbuffer and creating the correspondend movements
 */
@Stateless
public class MovementWorker {
    @Inject
    RawMovementService rawMovementService;
    @Inject
    MovementService movementService;
    @Inject
    MovementHelper movementHelper;
    @Inject
    UserService userService;


    /**
     * Clearing the RawMovementDatabase, checking if the new Movementpoints are needed and if yes creates this movment points
     * Scheduled to run every 5 minutes
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void clearBufferAndConvert(List<RawMovement> rawList) {
        for (RawMovement raw : rawList) {
            Movement next = movementHelper.upcycle(raw);
            Movement prevMovement = movementService.readLatestMovementByUserID(userService.read((int) raw.getUserId()));
            if (prevMovement != null) {
                if (!discardMovement(prevMovement, next)) {
                    next = movementService.save(next);
                    prevMovement.setNextMovementPoinnt(next);
                    movementService.merge(prevMovement);
                }
            } else {
                movementService.save(next);
            }
            movementService.flush();

        }


    }

    /**
     * Loading all RawMovments ordered by Timestamp
     * @return List of Rawmovements ordered by Timestamp
     */
    @Transactional
    public List<RawMovement> loadMovements() {
        return rawMovementService.readAllOrderByTimestamp();
    }

    /**
     * Deleting all RawMovements given as parameter
     * @param rawList List of Rawmovements to be deleted
     */
    @Transactional
    public void deleteOldMovements(List<RawMovement> rawList) {
        while (rawList.size() > 0) {
            RawMovement raw = rawList.remove(0);
            rawMovementService.delete(raw);
        }
    }

    /**
     * Checking if the moved Distance between two Time more than 1 meters or the timeintervall larger than an hour
     *
     * @param prev previous movement
     * @param next next movement
     * @return bool
     */
    public boolean discardMovement(Movement prev, Movement next) {
        DirectionStruct struct = calcDirection(prev.getLatitude(), prev.getLongitude(), next.getLatitude(), next.getLongitude());
        boolean returner = true;
        if (Math.abs(struct.length) > 1) {
            returner = false;
        }
        if (Math.abs(prev.getTimestamp().until(next.getTimestamp(), ChronoUnit.HOURS)) > 1) {
            returner = false;
        }
        return returner;


    }

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
    private DirectionStruct calcDirection(double lat1, double lon1, double lat2, double lon2) {

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

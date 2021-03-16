package de.ami.team1.dataacceptance.scheduling;

import de.ami.team1.dataacceptance.entities.RawMovement;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Schedules Jobs for execution
 */

@Singleton
@Startup
public class MovementScheduler {

    @Inject
    private MovementWorker movementWorker;

    /**
     * Starts every 5 minutes an etl process to clear the buffer database
     */
    @Transactional
    @Schedule(hour = "*", minute = "*/1", persistent = false)
    public void atSchedule() {
        List<RawMovement> rawMovementList = movementWorker.loadMovements();
        movementWorker.clearBufferAndConvert(rawMovementList);
        movementWorker.deleteOldMovements(rawMovementList);
    }


}

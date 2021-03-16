package de.ami.team1.dataacceptance.util;

import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.util.GridConverter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * A Helper Class for Converting MovementData
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class MovementHelper {

    @Inject
    private UserService userService;

    /**
     * Creates a Movement-Object from a raw Movement
     *
     * @param rawMovement Raw Movement Object
     * @return Movement Object
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Movement upcycle(RawMovement rawMovement) {
        int xgrid = GridConverter.toGrid(rawMovement.getLatitude());
        int ygrid = GridConverter.toGrid(rawMovement.getLongitude());
        Movement movement = new Movement();
        movement.setGridX(xgrid);
        movement.setGridY(ygrid);
        movement.setLatitude(rawMovement.getLatitude());
        movement.setLongitude(rawMovement.getLongitude());
        movement.setTimestamp(rawMovement.getTimestamp());
        movement.setUser(userService.read((int) rawMovement.getUserId()));
        return movement;


    }

}

package de.ami.team1;

import de.ami.team1.crud.CrudEntity;
import de.ami.team1.dataacceptance.controller.MovementController;
import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
import de.ami.team1.dataacceptance.scheduling.MovementScheduler;
import de.ami.team1.dataacceptance.scheduling.MovementWorker;
import de.ami.team1.dataacceptance.services.MovementService;
import de.ami.team1.dataacceptance.services.RawMovementService;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.dataacceptance.util.MovementHelper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(Arquillian.class)
public class MovementTest {
    private Movement movement;
    private Movement lastMovement;
    private User user;
    private RawMovement rawMovement;
    @Inject
    private UserService userService;
    @Inject
    private MovementService movementService;
    @Inject
    private RawMovementService rawMovementService;
    private RawMovement lastRawMovement;
    @Inject
    private MovementWorker movementWorker;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(RawMovementPojo.class.getPackage())
                .addPackage(RawMovement.class.getPackage())
                .addPackage(RawMovementService.class.getPackage())
                .addPackage(MovementController.class.getPackage())
                .addPackage(MovementHelper.class.getPackage())
                .addPackage(CrudEntity.class.getPackage())
                .addPackage(MovementScheduler.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
    }


    @Before
    public void init() {
        user = new User();
        user.setDateOfInfection(null);
        user.setGender('m');
        user.setDateOfBirth(LocalDate.now());
        user.setMail("test@test.de");
        user = userService.save(user);

        movement = new Movement();
        movement.setGridY(2);
        movement.setLatitude(20);
        movement.setLongitude(20);
        movement.setTimestamp(LocalDateTime.now());
        movement.setUser(user);
        movement.setGridX(30);
        movementService.save(movement);
        lastMovement = new Movement();
        lastMovement.setGridY(2);
        lastMovement.setLatitude(20);
        lastMovement.setLongitude(20);
        lastMovement.setTimestamp(LocalDateTime.now());
        lastMovement.setUser(user);
        lastMovement.setGridX(30);
        movementService.save(lastMovement);
        movement.setNextMovementPoinnt(lastMovement);
        movementService.merge(movement);

        rawMovement = new RawMovement();
        rawMovement.setLatitude(20);
        rawMovement.setLongitude(20);
        rawMovement.setTimestamp(LocalDateTime.now().minusHours(2));
        rawMovement.setUserId(user.getId());
        rawMovementService.save(rawMovement);
        lastRawMovement = new RawMovement();
        lastRawMovement.setLatitude(20);
        lastRawMovement.setLongitude(20);
        lastRawMovement.setTimestamp(LocalDateTime.now());
        lastRawMovement.setUserId(user.getId());
        rawMovementService.save(lastRawMovement);

    }

    @Test
    public void testMovementPersistAndUpdate() {
        Assert.assertEquals(lastMovement.getId(), movementService.readLatestMovementByUserID(user).getId());
        Assert.assertEquals(lastMovement.getId(), movement.getNextMovment().getId());
    }

    @Test
    public void testRawMovementOrder() {
        List<RawMovement> rawMovements = rawMovementService.readAllOrderByTimestamp();
        Assert.assertEquals(rawMovements.get(rawMovements.size() - 1).getId(), lastRawMovement.getId());
    }

    @Test
    public void testDiscardMovement() {
        boolean discard = movementWorker.discardMovement(movement, lastMovement);
        Assert.assertTrue(discard);
        lastMovement.setTimestamp(LocalDateTime.now().plusHours(2));
        discard = movementWorker.discardMovement(movement, lastMovement);
        Assert.assertFalse(discard);
        lastMovement.setLongitude(22);
        discard = movementWorker.discardMovement(movement, lastMovement);
        Assert.assertFalse(discard);
        LocalDateTime time = LocalDateTime.now();
        movement.setTimestamp(time);
        lastMovement.setTimestamp(time);
        discard = movementWorker.discardMovement(movement, lastMovement);
        Assert.assertFalse(discard);
    }


}

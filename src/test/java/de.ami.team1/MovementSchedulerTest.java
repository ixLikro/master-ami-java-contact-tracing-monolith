package de.ami.team1;

import de.ami.team1.crud.CrudEntity;
import de.ami.team1.dataacceptance.controller.MovementController;
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
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(Arquillian.class)
public class MovementSchedulerTest {
    @Inject
    private RawMovementService rawMovementService;
    @Inject
    private UserService userService;
    @Inject
    private MovementWorker movementWorker;
    @Inject
    private MovementService movementService;
    private User user;

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
    public void addRawMovementData() {
        user = new User();
        user.setDateOfInfection(null);
        user.setGender('m');
        user.setDateOfBirth(LocalDate.now());
        user.setMail("test@test.de");
        user = userService.save(user);
        RawMovement rawMovement = new RawMovement(user.getId(), 20.50, 20.70, LocalDateTime.now());
        RawMovement rawMovement2 = new RawMovement(user.getId(), 23.50, 20.70, LocalDateTime.now().plusDays(1));
        RawMovement rawMovement3 = new RawMovement(user.getId(), 21.50, 22.70, LocalDateTime.now().plusDays(1));
        RawMovement rawMovement5 = new RawMovement(user.getId(), 21.50, 22.70, LocalDateTime.now().plusDays(1));
        RawMovement rawMovement4 = new RawMovement(user.getId(), 21.50, 22.70, LocalDateTime.now().plusDays(2));
        rawMovementService.save(rawMovement);
        rawMovementService.save(rawMovement2);
        rawMovementService.save(rawMovement3);
        rawMovementService.save(rawMovement4);
        rawMovementService.save(rawMovement5);
    }

    @Test
    public void testDataTransfer() {
        Long amount = rawMovementService.countRawMovements();
        Assert.assertTrue(amount.longValue() >= 4);
        List<RawMovement> rawMovementList = movementWorker.loadMovements();
        movementWorker.clearBufferAndConvert(rawMovementList);
        movementWorker.deleteOldMovements(rawMovementList);
        amount = rawMovementService.countRawMovements();
        Assert.assertEquals(amount.longValue(), 0);
        Assert.assertEquals(movementService.readLatestMovementByUserID(user).getTimestamp().getDayOfYear(), LocalDateTime.now().plusDays(2).getDayOfYear());
        Assert.assertEquals(movementService.readAmountOfEntries(user).longValue(), 4);
    }

    @Test
    public void testGetLatestMovement(@ArquillianResteasyResource MovementController movementController) {
        List<RawMovement> rawMovementList = movementWorker.loadMovements();
        movementWorker.clearBufferAndConvert(rawMovementList);
        movementWorker.deleteOldMovements(rawMovementList);
        RawMovementPojo rawMovementPojo = movementController.getLastMovementOfUserIfExisting(user.getId());
        Assert.assertEquals(user.getId(), rawMovementPojo.getUserId());
    }



}

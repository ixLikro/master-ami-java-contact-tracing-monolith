package de.ami.team1;

import de.ami.team1.crud.CrudEntity;
import de.ami.team1.dataacceptance.controller.MovementController;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
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
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(Arquillian.class)
public class RawMovementRestTest {

    @Inject
    private UserService userService;
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
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");

    }

    @Before
    public void createBufferUser() {
        user = new User();
        user.setDateOfInfection(null);
        user.setGender('m');
        user.setDateOfBirth(LocalDate.now());
        user.setMail("test@test.de");
        user = userService.save(user);
    }


    @Test
    public void addUser(@ArquillianResteasyResource MovementController movementController) {
        RawMovementPojo rawMovementPojo = new RawMovementPojo(user.getId(), 20.2d, 20.4d, LocalDateTime.now());
        Response response = movementController.createRawMovement(rawMovementPojo);
        Assert.assertEquals(response.getStatus(), 200);
    }
}

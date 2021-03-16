package de.ami.team1;


import de.ami.team1.crud.CrudEntity;
import de.ami.team1.dataacceptance.controller.UserController;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.InfectionReportPojo;
import de.ami.team1.dataacceptance.pojos.UserPojo;
import de.ami.team1.dataacceptance.scheduling.MovementWorker;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.dataacceptance.util.UserHelper;
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

@RunWith(Arquillian.class)
public class UserRestTest {
    @Inject
    private UserService userService;
    private User user;
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(UserPojo.class.getPackage())
                .addPackage(User.class.getPackage())
                .addPackage(UserService.class.getPackage())
                .addPackage(UserController.class.getPackage())
                .addPackage(UserHelper.class.getPackage())
                .addPackage(MovementWorker.class.getPackage())
                .addPackage(CrudEntity.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");

    }


    @Before
    public void checkUserExistence() {
        user = new User();
        user.setDateOfInfection(null);
        user.setGender('m');
        user.setDateOfBirth(LocalDate.now());
        user.setMail("test@test.de");
        user = userService.save(user);
    }

    @Test
    public void addUser(@ArquillianResteasyResource UserController userController) {
        UserPojo userPojo = new UserPojo("test@test.de", LocalDate.now(), 'm');
        Response response = userController.createUser(userPojo);
        Assert.assertEquals(response.getStatus(), 200);
    }

    @Test
    public void testReportUserInfected(@ArquillianResteasyResource UserController userController) {
        InfectionReportPojo infectionReportPojo = new InfectionReportPojo(user.getId(), LocalDate.now());
        Response response = userController.reportInfectedUser(infectionReportPojo);
        User user = userService.read(infectionReportPojo.getUserId());
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertEquals(user.getDateOfInfection(), infectionReportPojo.getDateOfInfection());
    }


}

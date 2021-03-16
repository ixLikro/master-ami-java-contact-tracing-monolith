package de.ami.team1;

import de.ami.team1.contacttracking.ContactTrackingWorker;
import de.ami.team1.contacttracking.util.Line;
import de.ami.team1.contacttracking.util.Point;
import de.ami.team1.crud.CrudEntity;
import de.ami.team1.dataacceptance.controller.MovementController;
import de.ami.team1.dataacceptance.controller.UserController;
import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
import de.ami.team1.dataacceptance.pojos.UserPojo;
import de.ami.team1.dataacceptance.scheduling.MovementWorker;
import de.ami.team1.dataacceptance.services.RawMovementService;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.dataacceptance.util.MovementHelper;
import de.ami.team1.dataacceptance.util.UserHelper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(Arquillian.class)
public class TrackingTest {

    @Inject
    private ContactTrackingWorker contactTrackingWorker;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(Movement.class.getPackage())
                .addPackage(CrudEntity.class.getPackage())
                .addPackage(ContactTrackingWorker.class.getPackage())
                .addPackage(Line.class.getPackage())
                .addPackage(UserService.class.getPackage())
                .addPackage(UserController.class.getPackage())
                .addPackage(UserHelper.class.getPackage())
                .addPackage(MovementWorker.class.getPackage())
                .addPackage(UserPojo.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml");

    }

    @Test
    public void lineIntersect() {
        Line one = new Line(0, -1, 2, 1, 1, -1);
        Line two = new Line(0, 2, 2, 0, -1, 2);
        Optional<Point> intersect = contactTrackingWorker.isIntersecting(one, two);
        Assert.assertTrue(intersect.isPresent());
    }

    @Test
    public void lineIntersectButNotInRange() {
        Line one = new Line(0, -1, 2, 1, 1, -3);
        Line two = new Line(0, 2, 2, 0, -1, 2);
        Optional<Point> intersect = contactTrackingWorker.isIntersecting(one, two);
        Assert.assertFalse(intersect.isPresent());
    }

    @Test
    public void linesParallel() {
        Line one = new Line(0, -1, 2, 1, 1, -1);
        Line two = new Line(0, -1, 2, 1, 1, -3);
        Optional<Point> intersect = contactTrackingWorker.isIntersecting(one, two);
        Assert.assertFalse(intersect.isPresent());
    }
}

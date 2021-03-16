package de.ami.team1.contacttracking;

import de.ami.team1.analyse.services.AnalyseMovementService;
import de.ami.team1.contacttracking.util.Line;
import de.ami.team1.contacttracking.util.Point;
import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.util.ConfigService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Stateless
public class ContactTrackingWorker {
    @Inject
    private AnalyseMovementService trackingDataProvider;
    @Inject
    private ConfigService configService;

    private ContactNotifierService contactNotifierService = new ContactNotifierService();

    public void findContacts(User user) {
        List<Movement> movementList = trackingDataProvider.getListOfUserMovemntsSinceDate(user, user.getDateOfInfection().minusDays(14));
        if(movementList.isEmpty()){
            return;
        }
        List<Movement> sameGridMovement = new LinkedList<>();
        sameGridMovement.add(movementList.get(0));
        Movement compMovement = movementList.remove(0);
        while (!movementList.isEmpty()) {
            Movement nextMovement = movementList.remove(0);
            if (isInSameGrid(compMovement, nextMovement)) {
                sameGridMovement.add(nextMovement);
            } else {
                gridTracking(sameGridMovement, user);
                sameGridMovement.clear();
                sameGridMovement.add(nextMovement);
            }
            compMovement = nextMovement;
        }
    }

    /**
     * Find contacts in grids and starts notification
     *
     * @param gridMovement
     */
    private void gridTracking(List<Movement> gridMovement, User user) {
        int gridX = gridMovement.get(0).getGridX();
        int gridY = gridMovement.get(0).getGridY();
        LocalDateTime start = gridMovement.get(0).getTimestamp();
        LocalDateTime end = gridMovement.get(gridMovement.size() - 1).getTimestamp();
        List<Movement> trackingMovement = trackingDataProvider.getListOfMovementsInGridAndTimeIntervallWithoutUser(gridX, gridY, start, end, user);
        List<Line> trackingLines = new LinkedList<>();
        for (Movement t : trackingMovement) {
            if (t.getNextMovment() == null) {
                trackingLines.add(calculateLineFromMovement(t, t));
            } else {
                trackingLines.add(calculateLineFromMovement(t, t.getNextMovment()));
            }
        }

        for (Movement v : gridMovement) {
            Line line;
            if (v.getNextMovment() != null) {
                Movement nextV = v.getNextMovment();
                line = calculateLineFromMovement(v, nextV);

                for (int i = 0; i < trackingLines.size(); i++) {
                    //check if time is in between movements
                    Movement otherMovement = trackingMovement.get(i);
                    boolean between = v.getTimestamp().isBefore(otherMovement.getTimestamp()) && nextV.getTimestamp().isAfter(otherMovement.getTimestamp());
                    if (between) {
                        //check if lines intersect
                        Optional<Point> intersect = isIntersecting(line, trackingLines.get(i));
                        intersect.ifPresent(point -> contactNotifierService.notifyViaRESTCall(user, point, v.getTimestamp(),configService.getDATA_GENERATOR_URL()));
                    }
                }
            }


        }


    }

    /**
     * Calulates a line formular out of the points of two movements
     *
     * @param one Movement one
     * @param two Movement two
     * @return formular in style of y = m*x+b
     */
    private Line calculateLineFromMovement(Movement one, Movement two) {
        double m = (one.getLatitude() - two.getLatitude()) / (one.getLongitude() - two.getLongitude());
        double b = one.getLatitude() - (m) * one.getLongitude();
        return new Line(one.getLongitude(), one.getLatitude(), two.getLongitude(), two.getLatitude(), m, b);
    }


    /**
     * Checks if two lines intersect and if yes, if the intersection is on the segment given on line one
     *
     * @param one Line
     * @param two Line
     * @return boolean
     */
    public Optional<Point> isIntersecting(Line one, Line two) {
        Optional<Point> optionalPoint;
        if (one.getM() == two.getM()) {
            return Optional.empty();
        }
        double x = (two.getB() - one.getB()) / (one.getM() - two.getM());
        double y = one.getM() * x + one.getB();

        //check if point is on the line segement
        if (one.getX1() <= x && x <= one.getX2() && one.getY1() <= y && y <= one.getY2()) {
            Point point = new Point(x, y);
            return Optional.of(point);
        } else return Optional.empty();

    }

    /**
     * Check if two Movements are in the same grid
     *
     * @param movementOne Movement
     * @param movementTwo Movement
     * @return boolean
     */
    private boolean isInSameGrid(Movement movementOne, Movement movementTwo) {
        return (movementOne.getGridX() == movementTwo.getGridX()) && (movementOne.getGridY() == movementTwo.getGridY());
    }

}

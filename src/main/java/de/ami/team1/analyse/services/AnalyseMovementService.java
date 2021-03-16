package de.ami.team1.analyse.services;

import de.ami.team1.crud.CrudService;
import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.visualization.model.HeatMapValue;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AnalyseMovementService extends CrudService<Movement> {
    @Override
    protected Class<Movement> getEntityClass() {
        return Movement.class;
    }
    /**
     * Reads the lastest Movement with a given user and returns it
     *
     * @param user user entity
     * @return Movement
     */
    public List<Movement> readMovementByUserID(User user, int amount) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readLatestByUser", Movement.class);
        q.setParameter("uid", user);
        q.setMaxResults(amount);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Reads the lastest Movement with a given user and returns it
     *
     * @param user user entity
     * @return last 10 movements
     */
    public Movement readLatestMovementByUserID(User user) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readLatestByUser", Movement.class);
        q.setParameter("uid", user);
        q.setMaxResults(10);
        try {
            return q.getResultList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Counts all movement entries of a user
     *
     * @param user given user
     * @return amount of stored movement data
     */
    public Long readAmountOfEntries(User user) {
        TypedQuery<Long> q = em.createNamedQuery("Movement.readCountEntriesByUser", Long.class);
        q.setParameter("uid", user);
        q.setMaxResults(1);
        try {
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Count all users with matching mail addresses
     *
     * @param mail mail filter
     * @return amount of users with matching mail addresses
     */
    public int countUsersWithEmailFilter(String mail) {

        TypedQuery<Long> q = em.createNamedQuery("User.countWithEmailFilter", Long.class);
        q.setParameter("mailfilter", "%" + (mail == null ? "" : mail) + "%"); //search with before and after wildcard
        try {
            return q.getSingleResult().intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Searches all users with matching mail addresses
     *
     * @param mail mail filter
     * @return users with matching mail addresses
     */
    public List<User> getUsersWithMailFilter(String mail,int page, int pageSize) {

        TypedQuery<User> q = em.createNamedQuery("User.emailFilter", User.class);
        q.setParameter("mailfilter", "%" + (mail == null ? "" : mail) + "%"); //search with before and after wildcard
        q.setFirstResult(pageSize * page);
        q.setMaxResults(pageSize);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Reads all movement points inside given boundaries
     *
     * @param minLongitude min longitude value
     * @param maxLongitude max longitude value
     * @param minLatitude min latitude value
     * @param maxLatitude max latitude value
     * @return List<HeatMapValue>
     */
    public List<HeatMapValue> readHeatMap(double minLongitude, double maxLongitude, double minLatitude, double maxLatitude) {
        TypedQuery<HeatMapValue> q = em.createNamedQuery("Movement.getHeatMapTotal", HeatMapValue.class);
        q.setParameter("latMax", maxLatitude);
        q.setParameter("latMin", minLatitude);
        q.setParameter("longMax", maxLongitude);
        q.setParameter("longMin", minLongitude);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Reads the latest movement of infected users
     *
     * @return list of user movements
     */
    public List<Movement> readLatestMovementByInfectedUsers() {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readLatestMovementByInfectedUsers", Movement.class);
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = LocalDate.now().minusDays(14);
        q.setParameter("fromDate", fromDate);
        q.setParameter("toDate", toDate);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads the first movement of infected users after date of infection
     *
     * @return list of user movements
     */
    public List<Movement> readFirstPositionOfInfectedUsers() {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readPositionOfInfectedUsers", Movement.class);
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = LocalDate.now().minusDays(7);
        q.setParameter("fromDate", fromDate);
        q.setParameter("toDate", toDate);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads the first movement of infected users after date of infection in specified month
     * @param minusMonth
     * @return list of user movements
     */
    public List<Movement> readInfectedUsersByMonth(int minusMonth) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readPositionOfInfectedUsers", Movement.class);
        LocalDate initial = LocalDate.now();
        if (minusMonth != 0) {
            initial = initial.minusMonths(minusMonth);
        }
        LocalDate start = initial.withDayOfMonth(1);
        LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        q.setParameter("fromDate", start);
        q.setParameter("toDate", end);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Movement> getListOfUserMovemntsSinceDate(User user, LocalDate minusDays) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readMovementSinceDate", Movement.class);
        q.setParameter("since", minusDays.atStartOfDay());
        q.setParameter("uid", user);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Movement> getListOfMovementsInGridAndTimeIntervallWithoutUser(int gridX, int gridY, LocalDateTime start, LocalDateTime end, User user) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readInGridAndTimeIntervallWithoutUser", Movement.class);
        q.setParameter("uid", user);
        q.setParameter("gx", gridX);
        q.setParameter("gy", gridY);
        q.setParameter("t1", start);
        q.setParameter("t2", end);

        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads current infected users
     * last 14 days
     *
     * @return List of infected users
     */
    public List<User> readInfectedUsers() {
        TypedQuery<User> q = em.createNamedQuery("User.readInfectedUsers", User.class);
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = LocalDate.now().minusDays(14);
        q.setParameter("fromDate", fromDate);
        q.setParameter("toDate", toDate);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads current infected users
     *
     * @return List of infected users
     */
    public List<User> readInfectedUsers(LocalDate fromDate, LocalDate toDate) {
        TypedQuery<User> q = em.createNamedQuery("User.readInfectedUsers", User.class);
        q.setParameter("fromDate", fromDate);
        q.setParameter("toDate", toDate);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Reads latest Movement with a given user
     *
     * @param user user entity
     * @return last 10 movements
     */
    public Movement readFirstMovementByInfectedUser(User user) {
        TypedQuery<Movement> q = em.createNamedQuery("Movement.readPositionOfInfectedUser", Movement.class);
        q.setParameter("uid", user);
        q.setParameter("dateOfInfection", user.getDateOfInfection().atStartOfDay());
        q.setMaxResults(1);
        try {
            return q.getResultList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}

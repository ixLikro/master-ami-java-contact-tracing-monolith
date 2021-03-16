package de.ami.team1.dataacceptance.services;

import de.ami.team1.crud.CrudService;
import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.User;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


/**
 * Services for executing all needed CRUD methods
 */
@Stateless
public class MovementService extends CrudService<Movement> {
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
     * Reads the lastest movement of infected users
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
}

package de.ami.team1.dataacceptance.services;

import de.ami.team1.crud.CrudService;
import de.ami.team1.dataacceptance.entities.User;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

/**
 * All needed CRUD-Methods for the User
 */
@Stateless
public class UserService extends CrudService<User> {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    /**
     * Returns a list of all UserIds
     *
     * @return List of all UserIds
     */
    public List<Long> readAllUserIds() {
        TypedQuery<Long> q = em.createNamedQuery("User.readlAllUserIds", Long.class);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Reads current infected users
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
}



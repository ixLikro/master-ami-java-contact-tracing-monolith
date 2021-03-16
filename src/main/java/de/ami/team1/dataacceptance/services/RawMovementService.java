package de.ami.team1.dataacceptance.services;

import de.ami.team1.crud.CrudServiceBuffer;
import de.ami.team1.dataacceptance.entities.RawMovement;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * All needed CRUD Operations for the RawMovement
 */
@Stateless
public class RawMovementService extends CrudServiceBuffer<RawMovement> {

    /**
     * Counting all RawMovements in the buffer database
     *
     * @return Long amount of RawMovements
     */
    public Long countRawMovements() {
        TypedQuery<Long> q = em.createNamedQuery("RawMovement.countAll", Long.class);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * List of all RawMovements ordered by Timestamp
     *
     * @return List of all RawMovements
     */
    public List<RawMovement> readAllOrderByTimestamp() {
        TypedQuery<RawMovement> q = em.createNamedQuery("RawMovement.readAllOrderByTimestamp", RawMovement.class);
        q.setMaxResults(200);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    protected Class<RawMovement> getEntityClass() {
        return RawMovement.class;
    }
}

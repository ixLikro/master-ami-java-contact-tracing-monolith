package de.ami.team1.crud;

import java.sql.Timestamp;

/**
 * A marker interface for all entities.
 *
 * @see CrudService
 */
public interface CrudEntity {

    /**
     * Returns, whether the entity has already been persisted by the JPA provider.
     * If so, another call to em.persist() would cause an exception.
     *
     * @return whether the entity is persistent
     */
    boolean isPersistent();

    /**
     * Extends the parent interface with a timestamp for the last access of the entity.
     */
    interface WithLastUpdate extends CrudEntity {

        /**
         * Returns, when the entity has been updated the last time.
         *
         * @return the last update timestamp
         */
        Timestamp getLastUpdate();

        /**
         * Sets the entity's timestamp, when it has been last updated.
         *
         * @param lastUpdate the last update timestamp
         */
        void setLastUpdate(Timestamp lastUpdate);
    }
}

package de.ami.team1.crud;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A base service class to be used by all database services.
 * Bundles common CRUD operations, that are used by all entities.
 * This has been done to remove the amount of duplicated code in all the services.
 *
 * @param <T> the entity type
 */
public abstract class CrudService<T extends CrudEntity> {

    @PersistenceContext(unitName = "nachverfolgung")
    protected EntityManager em;

    /**
     * Loads the entity of this type with the given id from the database.
     *
     * @param id ID of said entity
     * @return entity or null, if not found
     */
    public T read(long id) {
        return em.find(getEntityClass(), id);
    }

    /**
     * Loads all entities of this type from the the database.
     *
     * @return entity list
     */
    public List<T> readAll() {
        CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(getEntityClass());
        TypedQuery<T> allQuery = em.createQuery(cq.select(cq.from(getEntityClass())));
        return allQuery.getResultList();
    }

    /**
     * Flushing the entitiy Manager
     */
    public void flush(){
        em.flush();
    }

    /**
     * Merges the entity back to the persistence context.
     * Relevant to detached entities.
     *
     * @param entity said entity
     * @return the merged entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public T merge(T entity) {
        return em.merge(entity);
    }

    /**
     * Saves the entity.
     * If is is already persisted, an updated is executed.
     * If it has not been persisted yet, it will be created.
     *
     * @param entity said entity
     * @return the saved entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public T save(T entity) {
        return entity.isPersistent()
                ? update(entity)
                : create(entity);
    }

    /**
     * Specifies the type of the entities to be relevant to this service.
     * Needs to match the type parameter {@link #<T>}.
     *
     * @return entity class
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Persists an entity to the database.
     *
     * @param entity said entity
     * @return the persisted entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected T create(T entity) {
        em.persist(entity);
        return entity;
    }

    /**
     * Writes an entity's changes to the database.
     *
     * @param entity said entity
     * @return the updated entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected T update(T entity) {
        return em.merge(entity);
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity said entity
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(T entity) {
        entity = em.merge(entity);
        em.remove(entity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Extends the parent class with functionality to handle entities of the type
     * {@link CrudEntity.WithLastUpdate}.
     *
     * @param <T> the entity type
     */
    public static abstract class WithLastUpdate<T extends CrudEntity.WithLastUpdate> extends CrudService<T> {

        /**
         * {@inheritDoc}
         * Sets the last update timestamp before persisting.
         */
        @Override
        protected T create(T entity) {
            setLastUpdate(entity);
            return super.create(entity);
        }

        /**
         * {@inheritDoc}
         * Also sets the last update timestamp.
         */
        @Override
        public T update(T entity) {
            entity = super.update(entity);
            setLastUpdate(entity);
            return entity;
        }

        private void setLastUpdate(T entity) {
            entity.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
        }
    }
}
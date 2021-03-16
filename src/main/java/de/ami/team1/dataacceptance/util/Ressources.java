package de.ami.team1.dataacceptance.util;


import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Configuration class for multiple datasources
 */
public class Ressources {

    @Produces
    @PersistenceContext(unitName = "nachverfolgung")
    private EntityManager nachverfolgung;

    @Produces
    @PersistenceContext(unitName = "movement_data")
    private EntityManager movementData;


    @Produces
    @Resource(lookup = "java:jboss/datasources/nachverfolgung")
    private DataSource dsPrimary;

    @Produces
    @Resource(lookup = "java:jboss/datasources/movement_data")
    private DataSource dsSecodnary;

}

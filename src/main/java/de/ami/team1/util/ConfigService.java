package de.ami.team1.util;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Field;

/**
 * a service that handles application properties.
 * all properties can be overwritten by an environment variable with the same name.
 */
@Startup
@Singleton
public class ConfigService {

    // filed name -> env variable name, field value -> the default value that ist used if no env variable can be found
    private String SELF_URL = "http://localhost:8080/nachvervolgung";
    private String DATA_GENERATOR_URL = "http://localhost:8081";


    @PostConstruct
    public void init() {
        for (Field f : getClass().getDeclaredFields()) {
            String env = System.getenv(f.getName());
            f.setAccessible(true);
            try {
                if (env == null) {
                    System.out.println("No Env-Variable " + f.getName() + " found, using default value: " + f.get(this));
                } else {
                    f.set(this, env);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the url of the monolith, default value: http://loalhost:8080/nachvervolgung
     */
    public String getSELF_URL() {
        return SELF_URL;
    }

    /**
     * @return the url of the data generator, default value: http://localhost:8081
     */
    public String getDATA_GENERATOR_URL() {
        return DATA_GENERATOR_URL;
    }
}

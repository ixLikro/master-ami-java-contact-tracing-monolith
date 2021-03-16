package de.ami.team1.visualization.view;

import de.ami.team1.util.ConfigService;
import de.ami.team1.visualization.model.api.GenerationTaskPojo;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Set;

/**
 * this controller handles the data generator part of the landingpage.
 */
@Named
@ViewScoped
public class LandingpageDataGeneratorController implements Serializable {

    @Inject
    private FacesContext context;

    @Inject
    private ConfigService config;

    private boolean isAvailable = false;
    private int amountOfPoints = 50;
    private int amountUser = 10;
    private int amountNewUser = 0;

    private Client client;
    private String URL;
    Validator validator;
    private boolean showError;
    private final String defaultError = "Fehler bei dem generieren der Daten. Bitte überprüfe deine Eingaben und versuche es erneut.";
    private String errorString = defaultError;

    public LandingpageDataGeneratorController() {

    }

    @PostConstruct
    public void init() {
        // create client and check if the data generator is available
        client = ClientBuilder.newClient();
        URL = config.getDATA_GENERATOR_URL() + "/generator/";

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        try {
            Response response = client
                    .target(URL)
                    .path("movement")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(new GenerationTaskPojo(0, 0, 0), MediaType.APPLICATION_JSON_TYPE));
            if (response.getStatus() >=  200 && response.getStatus() < 300){
                isAvailable = true;
            }
        }catch (Exception e){
            isAvailable = false;
        }
    }

    /**
     * calls the data generator on path /generator/movement with the parameters that the user typed in.
     */
    public void startGenerator(){
        showError = false;
        errorString = defaultError;
        GenerationTaskPojo pojo = new GenerationTaskPojo(amountOfPoints, amountUser, amountNewUser);

        Set<ConstraintViolation<GenerationTaskPojo>> violations = validator.validate(pojo);
        for (ConstraintViolation<GenerationTaskPojo> violation : violations) {
            showError = true;
            errorString += "\nDeiner Werte sind zu groß, wähle sie bitte etwas kleiner!";
            amountOfPoints = 50;
            amountUser = 10;
            amountNewUser = 0;
            return;
        }

        Response response = client
                .target(URL)
                .path("movement")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(pojo, MediaType.APPLICATION_JSON_TYPE));
        if (!(response.getStatus() >=  200 && response.getStatus() < 300)){
            showError = true;
        }
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public int getAmountOfPoints() {
        return amountOfPoints;
    }

    public void setAmountOfPoints(int amountOfPoints) {
        this.amountOfPoints = amountOfPoints;
    }

    public int getAmountUser() {
        return amountUser;
    }

    public void setAmountUser(int amountUser) {
        this.amountUser = amountUser;
    }

    public int getAmountNewUser() {
        return amountNewUser;
    }

    public void setAmountNewUser(int amountNewUser) {
        this.amountNewUser = amountNewUser;
    }

    public String getURL() {
        return URL;
    }

    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}

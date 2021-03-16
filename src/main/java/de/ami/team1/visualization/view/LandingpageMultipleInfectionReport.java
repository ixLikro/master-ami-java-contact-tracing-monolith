package de.ami.team1.visualization.view;

import de.ami.team1.dataacceptance.controller.MovementController;
import de.ami.team1.dataacceptance.controller.UserController;
import de.ami.team1.util.ConfigService;
import de.ami.team1.util.RandomHelper;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
import de.ami.team1.dataacceptance.pojos.InfectionReportPojo;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * this controller handles the report multiple users of the landingpage.
 */
@Named
@ViewScoped
public class LandingpageMultipleInfectionReport implements Serializable {

    //this is roughly lower saxony and surroundings
    private final static double topLeftLat = 53.6612;
    private final static double topLeftLong = 7.30505;
    private final static double botRightLat = 51.58678;
    private final static double botRightLong = 11.19027;

    private final static double maxDistance = 0.2;

    @Inject
    private ConfigService config;

    @Inject
    private UserController userController;

    @Inject
    private MovementController movementController;

    Client client;
    private String URL;
    private boolean showError;
    private String errorString = "Infektiös melden konnte nicht abgelschossen werden, da ein Fehler aufgetreten ist. Bitte versuche es erneut.";

    private List<Long> userIds;
    private String buttonLabel = "0% der Nutzer Infektiös melden!";

    //user input
    private double amount = 0;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        URL = config.getSELF_URL() + "/resources";
    }

    private void updateUserIds(){
/*       userIds = client
            .target(URL)
            .path("user/listUserIds")
            .request()
            .get(new GenericType<List<Long>>() {});*/
        userIds = userController.getAllUserIds();
    }

    /**
     * report the % of users infected that was inputted on the landingpage.
     * the users are chosen randomly. After the infection report one movement point is generated per user.
     * This movement is imported bc the trend analyse calculate only users that have moved after the report
     */
    public void reportInfected(){

        updateUserIds();
        int amountOfUsersToReport = (int) Math.round(userIds.size() * (amount / 100d));
        if(amountOfUsersToReport > 0){
            // get random users
            Collections.shuffle(userIds);
            List<Long> selectedUser = userIds.subList(0, amountOfUsersToReport);

            //iterate over all selected users
            for (Long id: selectedUser) {

                //report infected
/*                Response infectedResponse = client
                    .target(URL)
                    .path("user/infected")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(Entity.entity(new ReportInfectionPojo(id, LocalDate.now()), MediaType.APPLICATION_JSON_TYPE));
                if (!(infectedResponse.getStatus() >=  200 && infectedResponse.getStatus() < 300)){
                    showError = true;
                    amount = 0d;
                    return;
                }*/
                try {
                    userController.reportInfectedUser(new InfectionReportPojo(id, LocalDate.now()));
                }catch (Exception e){
                    e.printStackTrace();
                    showError = true;
                    amount = 0d;
                    return;
                }

                //get last movement
/*                Response re = client.target(URL).path("movement/lastMovement/" + id).request().get();
                MovementPojo lastMovement = null;
                if (re.getStatus() >=  200 && re.getStatus() < 300) {
                    lastMovement = re.readEntity(MovementPojo.class);
                }else {
                    // allow 404 exceptions -> the user just don't have a movement yet
                    if(re.getStatus() != 404){
                        showError = true;
                        amount = 0d;
                        return;
                    }
                }*/
                RawMovementPojo lastMovement = null;
                try {
                   lastMovement = movementController.getLastMovementOfUserIfExisting(id);
                }catch (Exception e){
                    // allow nested 404 exceptions -> the user just don't have a movement yet
                    if (e.getCause() != null && !(e.getCause() instanceof NotFoundException)){
                        e.printStackTrace();
                        showError = true;
                        amount = 0d;
                        return;
                    }
                }

                //create a next movement, its important bc the trend analyse calculate only users that have moved after the report
                RawMovementPojo nextMovement = new RawMovementPojo(id, 0d,0d, LocalDateTime.now());
                if(lastMovement != null){
                    nextMovement.setLatitude(RandomHelper.getDouble(lastMovement.getLatitude() - maxDistance, lastMovement.getLatitude() + maxDistance));
                    nextMovement.setLongitude(RandomHelper.getDouble(lastMovement.getLongitude() - maxDistance, lastMovement.getLongitude() + maxDistance));
                }else {
                    nextMovement.setLatitude(RandomHelper.getDouble(topLeftLat, botRightLat));
                    nextMovement.setLongitude(RandomHelper.getDouble(topLeftLong, botRightLong));
                }

                //send movement
/*                Response sendResponse = client
                    .target(URL)
                    .path("movement/")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(nextMovement, MediaType.APPLICATION_JSON_TYPE));
                if (!(sendResponse.getStatus() >=  200 && sendResponse.getStatus() < 300)){
                    showError = true;
                    amount = 0d;
                    return;
                }*/
                try {
                    movementController.createRawMovement(nextMovement);
                }catch (Exception e){
                    e.printStackTrace();
                    showError = true;
                    amount = 0d;
                    return;
                }
            }
        }
        amount = 0d;
    }

    public void setAmount(double amount) {
        this.amount = Math.min(amount, 100d);
        buttonLabel = this.amount + "% der Nutzer Infektiös melden!";
    }

    public double getAmount() {
        return amount;
    }


    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }
}

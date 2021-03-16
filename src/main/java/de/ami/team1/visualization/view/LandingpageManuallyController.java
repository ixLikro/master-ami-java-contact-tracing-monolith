package de.ami.team1.visualization.view;

import de.ami.team1.analyse.VisualizationDataAnalyzer;
import de.ami.team1.dataacceptance.controller.MovementController;
import de.ami.team1.dataacceptance.controller.UserController;
import de.ami.team1.util.ConfigService;
import de.ami.team1.visualization.model.Movement;
import de.ami.team1.visualization.model.User;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
import de.ami.team1.dataacceptance.pojos.InfectionReportPojo;

import javax.annotation.PostConstruct;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this controller handles the user info part (Nutzerdaten einsehen und bearbeiten) part of the landingpage.
 */
@ViewScoped
@Named
public class LandingpageManuallyController implements Serializable {


    @Inject
    private VisualizationDataAnalyzer dataProducer;

    @Inject
    private ConfigService config;

    @Inject
    private UserController userController;

    @Inject
    private MovementController movementController;


    //the search string
    private String filterUser;
    // tells the user-cache to reload the users, get set to true by a new search or pagination change
    private boolean reloadUserFlag;
    //pagination user
    private int userPage = 0;
    private int userPageSize = 13;
    private List<User> filteredUsers;

    //add data manually section
    private User selectedUser;
    private List<AbstractMap.SimpleEntry<String, String>> userInfos;
    private double latitude, longitude;

    private Client client;
    private String URL;
    private Validator validator;
    private boolean showErrorReport;
    private String errorReport = "Nutzer konnte nicht infektiös gemeldet werden, da ein unerwarteter Fehler aufgetreten ist.";
    private boolean showErrorMovement;
    private final String errorDefaultMovement = "Bewegungspunkt konnte nicht angelegt werden, da ein unerwarteter Fehler aufgetreten ist.";
    private String errorMovement = errorDefaultMovement;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        URL = config.getSELF_URL() + "/resources";
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * select one user and builds the userInfo map that will be displayed on the right side.
     * @param user the user that was selected
     */
    public void selectOneUser(User user) {
        this.selectedUser = user;
        List<Movement> movementData = dataProducer.getMovementFromUser(user);

        userInfos = new ArrayList<>();
        userInfos.add(new AbstractMap.SimpleEntry<>("id:", user.getId()+""));
        userInfos.add(new AbstractMap.SimpleEntry<>("Email:", user.getMail()));
        if (user.getDateOfBirth() != null) {
            userInfos.add(new AbstractMap.SimpleEntry<>("Geburtstag:", user.getDateOfBirth().toString()));
        }
        userInfos.add(new AbstractMap.SimpleEntry<>("Geschlecht:", user.getGender() + ""));
        userInfos.add(new AbstractMap.SimpleEntry<>("Bewegungspunkte:", movementData.size() + ""));
        if (!movementData.isEmpty() && movementData.get(0).getMapPoint() != null) {
            userInfos.add(new AbstractMap.SimpleEntry<>("Letzte Position:",
                    movementData.get(0).getMapPoint().getLatitude() + "/" +
                            movementData.get(0).getMapPoint().getLongitude()));
            userInfos.add(new AbstractMap.SimpleEntry<>("Letztes Pos. Update:", movementData.get(0).getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
        }
        userInfos.add(new AbstractMap.SimpleEntry<>("Infektiös gemeldet:", user.getLastInfectionReport() != null ? user.getLastInfectionReport().toString() : "Noch nie"));
    }

    /**
     * reports the current selected user infected
     */
    public void reportInfected(){
        showErrorReport = false;
        long currentSelected = selectedUser.getId();
/*        Response response = client
                .target(URL)
                .path("user/infected")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(new ReportInfectionPojo(selectedUser.getId(), LocalDate.now()), MediaType.APPLICATION_JSON_TYPE));
        if (!(response.getStatus() >=  200 && response.getStatus() < 300)){
            showErrorReport = true;
            return;
        }*/
        try {
            userController.reportInfectedUser(new InfectionReportPojo(selectedUser.getId(), LocalDate.now()));
        }catch (Exception e){
            showErrorReport = true;
            e.printStackTrace();
            return;
        }

        //update the users
        reloadUserFlag = true;
        loadUsers();

        //is the current selected in the new data? (the user don't change the page after the select)
        AtomicBoolean stillHere = new AtomicBoolean(false);
        filteredUsers.forEach(user -> {
            if(user.getId() == currentSelected){
                selectOneUser(user);
                stillHere.set(true);
            }
        });
        //nope so just unselect the user
        if(!stillHere.get()){
            selectedUser = null;
        }
    }

    /**
     * add a nwe movement to the selected user
     */
    public void addMovement(){
        showErrorMovement = false;
        errorMovement = errorDefaultMovement;
        long currentSelected = selectedUser.getId();
        RawMovementPojo movementPojo = new RawMovementPojo(selectedUser.getId(), latitude, longitude, LocalDateTime.now());

        //validate pojo
        Set<ConstraintViolation<RawMovementPojo>> violations = validator.validate(movementPojo);
        for (ConstraintViolation<RawMovementPojo> violation : violations) {
            showErrorMovement = true;
            errorMovement = "Ungültige Eingabe!";
            return;
        }

/*        Response response = client
                .target(URL)
                .path("movement/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(movementPojo, MediaType.APPLICATION_JSON_TYPE));
        if (!(response.getStatus() >=  200 && response.getStatus() < 300)){
            showErrorMovement = true;
            return;
        }*/
        try {
            movementController.createRawMovement(movementPojo);
        }catch (Exception e){
            showErrorMovement = true;
            e.printStackTrace();
            return;
        }

        latitude = 0d;
        longitude = 0d;
        //update the users
        reloadUserFlag = true;
        loadUsers();

        //is the current selected in the new data? (the user don't change the page after the select)
        AtomicBoolean stillHere = new AtomicBoolean(false);
        filteredUsers.forEach(user -> {
            if(user.getId() == currentSelected){
                selectOneUser(user);
                stillHere.set(true);
            }
        });
        //nope so just unselect the user
        if(!stillHere.get()){
            selectedUser = null;
        }
    }

    //******************* user pagination and search logic
    public void loadUsers() {
        filteredUsers = dataProducer.getUsersWithFilter(filterUser, userPage, userPageSize);
    }

    public List<User> getFilteredUsers() {
        if (reloadUserFlag || filteredUsers == null) {
            loadUsers();
            reloadUserFlag = false;
        }
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public int getUserPageCount() {
        return (int) Math.ceil(dataProducer.countUserWithFilter(filterUser) / (double) userPageSize);
    }

    public Integer userPrevPage() {
        reloadUserFlag = true;
        return userPage--;
    }

    public Integer userNextPage() {
        reloadUserFlag = true;
        return userPage++;
    }

    public String getFilterUser() {
        return filterUser;
    }

    public void setFilterUser(String filterUser) {
        this.filterUser = filterUser;
        userPage = 0;
        reloadUserFlag = true;
    }

    public boolean isReloadUserFlag() {
        return reloadUserFlag;
    }


    //*************** getter and setter without logic

    public void setReloadUserFlag(boolean reloadUserFlag) {
        this.reloadUserFlag = reloadUserFlag;
    }

    public int getUserPage() {
        return userPage;
    }

    public void setUserPage(int userPage) {
        reloadUserFlag = true;
        this.userPage = userPage;
    }

    public int getUserPageSize() {
        return userPageSize;
    }

    public void setUserPageSize(int userPageSize) {
        reloadUserFlag = true;
        this.userPageSize = userPageSize;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getUserInfos() {
        return userInfos;
    }

    public String getURL() {
        return URL;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isShowErrorReport() {
        return showErrorReport;
    }

    public void setShowErrorReport(boolean showErrorReport) {
        this.showErrorReport = showErrorReport;
    }

    public String getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(String errorReport) {
        this.errorReport = errorReport;
    }

    public boolean isShowErrorMovement() {
        return showErrorMovement;
    }

    public void setShowErrorMovement(boolean showErrorMovement) {
        this.showErrorMovement = showErrorMovement;
    }

    public String getErrorMovement() {
        return errorMovement;
    }

    public void setErrorMovement(String errorMovement) {
        this.errorMovement = errorMovement;
    }
}

package de.ami.team1.visualization.view;

import de.ami.team1.analyse.VisualizationDataAnalyzer;
import de.ami.team1.visualization.control.RandomDataGenerator;
import de.ami.team1.visualization.control.VisualizationDataProducer;
import de.ami.team1.visualization.model.*;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class VisualizationController implements Serializable {

    @Inject
    private FacesContext context;
    @Inject
    private VisualizationDataProducer dataProducer;

    //the selected users with all there movement points
    private Map<User, List<Movement>> selectedUser;
    private List<MovementOverview> movementOverviews;

    //the search string
    private String filterUser;

    // tells the user-cache to reload the users, got set to true by a new search or pagination change
    private boolean reloadUserFlag;

    //pagination user
    private int userPage = 0;
    private int userPageSize = 15;
    private List<User> filteredUsers;

    @PostConstruct
    public void init() {
        selectedUser = new HashMap<>();
        movementOverviews = new ArrayList<>();
    }


    /**
     * returns a string with a valid javaScript array with the following form
     * [[latitude, longitude, intensity],[latitude, longitude, intensity], ...]
     * @param list the heatmap data
     * @return the js array that contains all given heatmapValues
     */
    private String serializeHeatmapDataToJsArray(List<HeatMapValue> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (HeatMapValue value : list) {
            builder.append("[");
            builder.append(value.getLatitude());
            builder.append(",");
            builder.append(value.getLongitude());
            builder.append(",");
            builder.append(value.getIntensity());
            builder.append("],");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * returns a string with a valid javaScript object with the following form
     * {email: "usermail", id: userId,color: "#hexColor", movement: [[latitude, longitude, timestamp],[latitude, longitude, timestamp], ...]}
     * @param overview the movement overview
     * @param movements the list of the movements of the user
     * @return the js object that contains the user id, email and all movement points
     */
    private String serializeMovement(MovementOverview overview, List<Movement> movements){
        StringBuilder builder = new StringBuilder();
        builder.append("{email: \"");
        builder.append(overview.getUser().getMail());
        builder.append("\",id: ");
        builder.append(overview.getUser().getId());
        builder.append(",color:\"");
        builder.append(overview.getColor());
        builder.append("\",movement: [");
        for (Movement value : movements) {
            builder.append("[");
            builder.append(value.getMapPoint().getLatitude());
            builder.append(",");
            builder.append(value.getMapPoint().getLongitude());
            builder.append(",");
            builder.append(value.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli());
            builder.append("],");
        }
        builder.append("]}");
        return builder.toString();
    }

    /**
     * loads the heatmap data between two given points.
     * <p>
     * requires the following four request parameter:
     * p1Latitude, p1Longitude,p2Latitude and p2Longitude.
     * All parameter will be interpreted as double.
     * <p>
     * adds javaScript code to the ajax response as evalScript.
     * The script will render all loaded points as a head map as leafLet layer.
     */
    public void getHeatmapData() {
        // get 2 two outer points from request params, the data should be loaded in between this points
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        MapPoint p1 = new MapPoint(Double.parseDouble(params.get("p1Latitude")), Double.parseDouble(params.get("p1Longitude")));
        MapPoint p2 = new MapPoint(Double.parseDouble(params.get("p2Latitude")), Double.parseDouble(params.get("p2Longitude")));

        //get heatmapData and serialize ist as javaScript array
        String pointList = serializeHeatmapDataToJsArray(dataProducer.getHeatmapData(p1, p2));

        //send this js code to the client, it will be performed after the response of the ajax request is revived
        context.getPartialViewContext()
                .getEvalScripts()
                .add("serverCallback_HeatmapData(" + pointList + ");");
    }

    /**
     * selects a user and loads its movement.
     * Triggers a movement-redraw function on the client
     *
     * @param user the user that should be selected
     */
    public void selectOneUser(User user) {
        if (!selectedUser.containsKey(user)) {
            //add user and load user movement
            List<Movement> movement = dataProducer.getMovementFromUser(user);
            MovementOverview overview = new MovementOverview(user, movement);
            selectedUser.put(user, movement);

            //add to overview
            movementOverviews.add(overview);

            //build js object and send it to the client as evalScript
            context.getPartialViewContext()
                    .getEvalScripts()
                    .add("serverCallback_newUserSelected(" + serializeMovement(overview, movement) + ");");
        }
    }

    /**
     * unselects a user and loads its movement.
     * Triggers a movement-redraw function on the client
     *
     * @param user the user that should be unselected
     */
    public void unselectOneUser(User user) {
        selectedUser.remove(user);
        movementOverviews.remove(movementOverviews
                .stream()
                .filter(overview -> overview.getUser().getId() == user.getId())
                .findAny()
                .orElse(null));

        //build js object and send it to the client as evalScript
        context.getPartialViewContext()
                .getEvalScripts()
                .add("serverCallback_userUnselected(" + user.getId() + ");");
    }


    //******************* user pagination and search logic
    private void loadUsers() {
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

    public void setUserPageSize(int userPageSize) {
        reloadUserFlag = true;
        this.userPageSize = userPageSize;
    }

    public void setUserPage(int userPage) {
        reloadUserFlag = true;
        this.userPage = userPage;
    }

    public void setFilterUser(String filterUser) {
        this.filterUser = filterUser;
        userPage = 0;
        reloadUserFlag = true;
    }


    //******************* getter and setter without logic

    /**
     * to prevent to send all movement points twice, the jsf part will only receive the overview.
     * the actual movement Data will be send by eval-script directly in js
     *
     * @return a list of movement overviews from all selected users
     */
    public List<MovementOverview> getSelectedMovementOverviews() {
        return movementOverviews;
    }

    public void setSelectedUser(Map<User, List<Movement>> selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Map<User, List<Movement>> getSelectedUser() {
        return selectedUser;
    }

    public String getFilterUser() {
        return filterUser;
    }

    public int getUserPage() {
        return userPage;
    }

    public int getUserPageSize() {
        return userPageSize;
    }

}

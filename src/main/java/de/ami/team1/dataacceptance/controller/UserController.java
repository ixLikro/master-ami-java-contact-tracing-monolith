package de.ami.team1.dataacceptance.controller;

import de.ami.team1.contacttracking.ContactTrackingWorker;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.InfectionReportPojo;
import de.ami.team1.dataacceptance.pojos.UserPojo;
import de.ami.team1.dataacceptance.services.UserService;
import de.ami.team1.dataacceptance.util.UserHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class UserController {

    @Inject
    private UserService userService;
    @Inject
    private UserHelper userHelper;
    @Inject
    private ContactTrackingWorker contactTrackingWorker;
    // TODO: 06.01.21 enable worker when data provided 

    /**
     * Creates a new User from a given Pojo and returns pojo with id of the new created user.
     *
     * @param userPojo Pojo of User
     * @return UserPojo with added id
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(UserPojo userPojo) {
        User user = userHelper.upcycle(userPojo);
        user = userService.save(user);
        userPojo.setId(user.getId());
        return Response.status(200).entity(userPojo).build();
    }


    /**
     * Reports an user as infected on a certain date
     *
     * @param infectionReportPojo information about the infection
     * @return Status 200
     */
    @PUT
    @Path("/infected")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reportInfectedUser(InfectionReportPojo infectionReportPojo) {
        User user = userService.read(infectionReportPojo.getUserId());
        user.setDateOfInfection(infectionReportPojo.getDateOfInfection());
        contactTrackingWorker.findContacts(user);
        userService.save(user);
        return Response.status(200).build();
    }

    /**
     * Supplies a list of all usersid
     *
     * @return list of all userids
     */
    @GET
    @Path("/listUserIds")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Long> getAllUserIds() {
        return userService.readAllUserIds();
    }
}

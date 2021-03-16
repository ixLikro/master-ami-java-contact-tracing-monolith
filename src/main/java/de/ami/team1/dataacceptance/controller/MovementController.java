package de.ami.team1.dataacceptance.controller;

import de.ami.team1.dataacceptance.entities.Movement;
import de.ami.team1.dataacceptance.entities.RawMovement;
import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.RawMovementPojo;
import de.ami.team1.dataacceptance.services.MovementService;
import de.ami.team1.dataacceptance.services.RawMovementService;
import de.ami.team1.dataacceptance.services.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Controller for Movementdata
 */
@Path("/movement")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class MovementController {

    @Inject
    private RawMovementService rawMovementService;
    @Inject
    private UserService userService;
    @Inject
    private MovementService movementService;

    /**
     * Accecpts a RawMovementPojo and persists it in the buffer database.
     *
     * @param rawMovementPojo Pojo
     * @return http response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRawMovement(RawMovementPojo rawMovementPojo) {
        RawMovement rawMovement = rawMovementPojo.createPersitableObject();
        rawMovement = rawMovementService.save(rawMovement);
        return Response.status(200).build();

    }

    /**
     * Returns the latest Movement of a given user as rawmovementpojo
     *
     * @param id id of user;
     * @return Rawmovementpojo of user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lastMovement/{id}")
    public RawMovementPojo getLastMovementOfUserIfExisting(@PathParam("id") Long id) {
            User user = userService.read(id);
            Movement movement = movementService.readLatestMovementByUserID(user);
            if (user == null || movement == null){
                throw new NotFoundException();
            }
            RawMovementPojo rawMovementPojo = new RawMovementPojo(user.getId(), movement.getLatitude(), movement.getLongitude(), movement.getTimestamp());
            return rawMovementPojo;

    }

}

package de.ami.team1.contacttracking;

import de.ami.team1.contacttracking.util.Point;
import de.ami.team1.contacttracking.util.RestClientInterceptor;
import de.ami.team1.dataacceptance.entities.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;


public class ContactNotifierService {

    // TODO: 06.01.21 change target adress to real one

    public void notifyViaRESTCall(User user, Point point, LocalDateTime time,String datageneratorUrl) {
        NotificationPojo notificationPojo = new NotificationPojo(user.getMail(), time, point,user.getId());
        Client client = ClientBuilder.newBuilder()
                .register(new RestClientInterceptor())
                .build();
        Response re = client.target(datageneratorUrl).path("/notify").request().post(Entity.entity(notificationPojo, MediaType.APPLICATION_JSON));
    }

}

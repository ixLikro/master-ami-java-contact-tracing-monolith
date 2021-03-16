package de.ami.team1.dataacceptance.util;

import de.ami.team1.dataacceptance.entities.User;
import de.ami.team1.dataacceptance.pojos.UserPojo;

import javax.ejb.Stateless;

/**
 * A Helper Class for Converting User Entities
 */

@Stateless
public class UserHelper {

    public User upcycle(UserPojo userPojo) {
        User user = new User();
        user.setMail(userPojo.getMail());
        user.setDateOfBirth(userPojo.getDateOfBirth());
        user.setGender(userPojo.getGender());
        user.setDateOfInfection(null);
        return user;
    }
}

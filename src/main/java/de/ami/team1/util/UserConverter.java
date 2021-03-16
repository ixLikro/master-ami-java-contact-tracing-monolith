package de.ami.team1.util;

import de.ami.team1.dataacceptance.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserConverter {

    public de.ami.team1.dataacceptance.entities.User toDA(de.ami.team1.visualization.model.User user){
        de.ami.team1.dataacceptance.entities.User retUser = new de.ami.team1.dataacceptance.entities.User();
        retUser.setId(user.getId());
        retUser.setGender(user.getGender());
        retUser.setMail(user.getMail());
        retUser.setDateOfBirth(user.getDateOfBirth());
        retUser.setDateOfInfection(user.getLastInfectionReport());
        return retUser;
    }

    public de.ami.team1.visualization.model.User toV(de.ami.team1.dataacceptance.entities.User user){
        de.ami.team1.visualization.model.User retUser = new de.ami.team1.visualization.model.User();
        retUser.setId(user.getId());
        retUser.setGender(user.getGender());
        retUser.setMail(user.getMail());
        retUser.setDateOfBirth(user.getDateOfBirth());
        retUser.setLastInfectionReport(user.getDateOfInfection());
        return retUser;
    }

    public List<User> toDA(List<de.ami.team1.visualization.model.User> users){
        List<de.ami.team1.dataacceptance.entities.User> retUsers = new ArrayList<>();
        for (de.ami.team1.visualization.model.User user: users){
            retUsers.add(toDA(user));
        }
        return retUsers;
    }

    public List<de.ami.team1.visualization.model.User> toV(List<de.ami.team1.dataacceptance.entities.User> users){
        List<de.ami.team1.visualization.model.User> retUsers = new ArrayList<>();
        for (de.ami.team1.dataacceptance.entities.User user: users){
            retUsers.add(toV(user));
        }
        return retUsers;
    }
}

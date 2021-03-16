package de.ami.team1.contacttracking;

import de.ami.team1.contacttracking.util.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NotificationPojo {

    public String email;
    public LocalDateTime timeOfContact;
    public Point contactPoint;
    public long userId;

    public NotificationPojo(String email, LocalDateTime timeOfContact, Point contactPoint, long userId) {
        this.email = email;
        this.timeOfContact = timeOfContact;
        this.contactPoint = contactPoint;
        this.userId = userId;
    }

    public NotificationPojo(){

    }

    //getter and setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getTimeOfContact() {
        return timeOfContact;
    }

    public void setTimeOfContact(LocalDateTime timeOfContact) {
        this.timeOfContact = timeOfContact;
    }

    public Point getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Point contactPoint) {
        this.contactPoint = contactPoint;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

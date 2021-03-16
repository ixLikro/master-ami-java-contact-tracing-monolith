package de.ami.team1.visualization.model;

import java.time.LocalDate;

public class User {

    private long id;
    private String mail;
    private LocalDate dateOfBirth;
    private char gender;
    private LocalDate lastInfectionReport;

    public User() {
    }

    public User(long id, String mail, LocalDate dateOfBirth, char gender, LocalDate lastInfectionReport) {
        this.id = id;
        this.mail = mail;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastInfectionReport = lastInfectionReport;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getLastInfectionReport() {
        return lastInfectionReport;
    }

    public void setLastInfectionReport(LocalDate lastInfectionReport) {
        this.lastInfectionReport = lastInfectionReport;
    }
}

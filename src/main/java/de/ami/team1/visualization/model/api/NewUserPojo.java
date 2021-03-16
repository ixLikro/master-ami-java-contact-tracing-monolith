package de.ami.team1.visualization.model.api;

import java.time.LocalDate;

public class NewUserPojo {
    private String mail;
    private LocalDate dateOfBirth;
    private char gender;

    /**
     * Constructor for the User pojo with the id
     *
     * @param mail
     * @param dateOfBirth
     * @param gender
     */
    public NewUserPojo(String mail, LocalDate dateOfBirth, char gender) {
        this.mail = mail;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }


    /**
     * Default Constructor for UserPojo
     */
    public NewUserPojo() {
    }

    //getter and setter
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
}

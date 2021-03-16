package de.ami.team1.dataacceptance.entities;

import de.ami.team1.crud.CrudEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Entity representation for a user
 */

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.countWithEmailFilter", query = "SELECT count(m) FROM User m where m.mail LIKE :mailfilter"),
        @NamedQuery(name = "User.emailFilter", query = "SELECT m FROM User m where m.mail LIKE :mailfilter ORDER BY m.id"),
        @NamedQuery(name = "User.readlAllUserIds", query = "select m.id from User m"),
        @NamedQuery(name = "User.readInfectedUsers", query = "SELECT u from User u where u.dateOfInfection BETWEEN :fromDate AND :toDate")
})

public class User implements CrudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;

    @Column(name = "mail")
    @Email
    @NotBlank
    private String mail;

    @Column(name = "date_of_birth")
    @Past
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private char gender;

    @Column(name = "date_of_infection")
    private LocalDate dateOfInfection;

    /**
     * Constructor for creating a new User
     *
     * @param mail
     * @param dateOfBirth
     * @param gender
     */
    public User(String mail, LocalDate dateOfBirth, char gender, LocalDate dateOfInfection) {
        this.mail = mail;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.dateOfInfection = dateOfInfection;
    }

    /**
     * Default Constructor
     */
    public User() {
    }

    @Override
    public boolean isPersistent() {
        return false;
    }


    //getter and setter

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LocalDate getDateOfInfection() {
        return dateOfInfection;
    }

    public void setDateOfInfection(LocalDate dateOfInfection) {
        this.dateOfInfection = dateOfInfection;
    }
}

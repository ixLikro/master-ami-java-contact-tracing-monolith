package de.ami.team1.dataacceptance.pojos;

import java.time.LocalDate;

/**
 * Pojo with the information about an infection
 */

public class InfectionReportPojo {
    private long userId;
    private LocalDate dateOfInfection;

    /**
     * Constructor for Pojo
     *
     * @param userId          id of the infected user
     * @param dateOfInfection date of the infection
     */
    public InfectionReportPojo(long userId, LocalDate dateOfInfection) {
        this.userId = userId;
        this.dateOfInfection = dateOfInfection;
    }

    /**
     * Default Constructor
     */
    public InfectionReportPojo() {

    }

    //getter and setter

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDate getDateOfInfection() {
        return dateOfInfection;
    }

    public void setDateOfInfection(LocalDate dateOfInfection) {
        this.dateOfInfection = dateOfInfection;
    }
}

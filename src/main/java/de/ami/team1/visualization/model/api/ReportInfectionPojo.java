package de.ami.team1.visualization.model.api;

import java.time.LocalDate;

public class ReportInfectionPojo {
    private long userId;
    private LocalDate dateOfInfection;

    public ReportInfectionPojo(long userId, LocalDate dateOfInfection) {
        this.userId = userId;
        this.dateOfInfection = dateOfInfection;
    }

    public ReportInfectionPojo(){

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

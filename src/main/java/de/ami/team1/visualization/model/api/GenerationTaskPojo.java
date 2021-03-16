package de.ami.team1.visualization.model.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Pojoclass for sending a generator task to the data generator
 */
public class GenerationTaskPojo {
    @PositiveOrZero
    @Max(10000)
    private int amountDataPointPerUsers;
    @PositiveOrZero
    @Max(500)
    private int users;
    @PositiveOrZero
    @Max(500)
    private int newUsers;

    public GenerationTaskPojo() {

    }

    /**
     * Constructor for GenTaskPojo
     *
     * @param amountDataPointPerUsers amount of datapoints, which shall be created by each user
     * @param users                   amount of users to be tested
     * @param newUsers       amount of users, which shall be newly created
     */
    public GenerationTaskPojo(int amountDataPointPerUsers, int users, int newUsers) {
        this.amountDataPointPerUsers = amountDataPointPerUsers;
        this.users = users;
        this.newUsers = newUsers;
    }

    //getter and setter
    public int getAmountDataPointPerUsers() {
        return amountDataPointPerUsers;
    }

    public void setAmountDataPointPerUsers(int amountDataPointPerUsers) {
        this.amountDataPointPerUsers = amountDataPointPerUsers;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(int newUsers) {
        this.newUsers = newUsers;
    }
}

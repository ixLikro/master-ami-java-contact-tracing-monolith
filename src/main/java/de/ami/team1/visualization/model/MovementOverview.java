package de.ami.team1.visualization.model;

import de.ami.team1.util.RandomHelper;

import java.util.List;

public class MovementOverview {

    private User user;
    private int moveMovementCount;
    private String color;

    public MovementOverview() {
    }

    public MovementOverview(User user, List<Movement> movements) {
        this.user = user;
        this.moveMovementCount = movements.size();
        this.color = RandomHelper.getHexColor(40, 255);
    }

    public MovementOverview(User user, int moveMovementCount, String color) {
        this.user = user;
        this.moveMovementCount = moveMovementCount;
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMoveMovementCount() {
        return moveMovementCount;
    }

    public void setMoveMovementCount(int moveMovementCount) {
        this.moveMovementCount = moveMovementCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

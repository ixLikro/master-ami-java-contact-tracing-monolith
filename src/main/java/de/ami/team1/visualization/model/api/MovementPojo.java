package de.ami.team1.visualization.model.api;

import de.ami.team1.dataacceptance.entities.RawMovement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public class MovementPojo {
    @PositiveOrZero
    private long userId;
    @Max(value = 85)
    @Min(value = -85)
    private double latitude;
    @Max(value = 180)
    @Min(value = -180)
    private double longitude;
    @PastOrPresent
    private LocalDateTime timestamp;

    /**
     * Default Constructor
     */
    public MovementPojo(){

    }

    /**
     * Constructor for RawMovementPojo
     * @param userId id of user
     * @param latitude decimal latitude
     * @param longitude decimal longitude
     * @param timestamp client side timestamp
     */
    public MovementPojo(long userId, double latitude, double longitude, LocalDateTime timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    /**
     * Creates a persistable RawMovement Object out of the given pojo.
     * @return RawMovement Object
     */
    public RawMovement createPersitableObject(){
        return new RawMovement(userId,latitude,longitude,timestamp);
    }

    //getter and setter

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

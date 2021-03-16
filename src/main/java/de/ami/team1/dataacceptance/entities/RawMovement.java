package de.ami.team1.dataacceptance.entities;

import de.ami.team1.crud.CrudEntity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Representation for Movementdata as send by the User.
 */
@NamedQueries({
        @NamedQuery(name = "RawMovement.countAll", query = "SELECT count(m) from RawMovement m"),
        @NamedQuery(name = "RawMovement.readAllOrderByTimestamp", query = "select m from RawMovement m order by m.timestamp asc")
})

@Table(name = "rawmovement")
@Entity
public class RawMovement implements CrudEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    @NotNull
    private long userId;

    //using Decimal degrees (DD): 41.40338, 2.17403
    @Column(name = "latitude")
    @NotNull
    @Max(value = 85)
    @Min(value = -85)
    private double latitude;

    @Column(name = "longitude")
    @NotNull
    @Max(value = 180)
    @Min(value = -180)
    private double longitude;

    @Column(name = "time_stamp")
    @NotNull
    private LocalDateTime timestamp;

    /**
     * Constructor for creating a new Rawmovement Object
     *
     * @param userId    id of the User who is transmitting the data
     * @param latitude  decimal latitude of his current location
     * @param longitude decimal longitude of his current location
     * @param timestamp timestamp of the time transmitted the data. must be taken on client side.
     */
    public RawMovement(@NotNull long userId, @NotNull double latitude, @NotNull double longitude, @NotNull LocalDateTime timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    /**
     * Default Constructor
     */
    public RawMovement() {
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

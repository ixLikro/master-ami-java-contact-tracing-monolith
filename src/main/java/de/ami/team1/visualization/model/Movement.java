package de.ami.team1.visualization.model;

import java.time.LocalDateTime;

public class Movement {

    private long userId;
    private MapPoint mapPoint;
    private LocalDateTime timestamp;

    public Movement() {
    }

    public Movement(long userId, MapPoint mapPoint, LocalDateTime timestamp) {
        this.userId = userId;
        this.mapPoint = mapPoint;
        this.timestamp = timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public MapPoint getMapPoint() {
        return mapPoint;
    }

    public void setMapPoint(MapPoint mapPoint) {
        this.mapPoint = mapPoint;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

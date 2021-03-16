package de.ami.team1.util;

import de.ami.team1.visualization.model.MapPoint;

public class MovementConverter {
    public de.ami.team1.dataacceptance.entities.Movement toDA(de.ami.team1.visualization.model.Movement movement){
        de.ami.team1.dataacceptance.entities.Movement retMovement = new de.ami.team1.dataacceptance.entities.Movement();
        retMovement.setLatitude(movement.getMapPoint().getLatitude());
        retMovement.setLongitude(movement.getMapPoint().getLongitude());
        retMovement.setTimestamp(movement.getTimestamp());

        return retMovement;
    }

    public de.ami.team1.visualization.model.Movement toV(de.ami.team1.dataacceptance.entities.Movement movement){
        de.ami.team1.visualization.model.Movement retMovement = new de.ami.team1.visualization.model.Movement();
        retMovement.setMapPoint(new MapPoint(movement.getLatitude(), movement.getLongitude()));
        retMovement.setUserId(movement.getUser().getId());
        retMovement.setTimestamp(movement.getTimestamp());
        return retMovement;
    }
}

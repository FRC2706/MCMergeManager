package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-08.
 */

public class AutoCubePickupEvent extends Event{
    public static final String ID = "auto_cube_pickup";

    public enum pickupType{
        PORTAL, GROUND, EXCHANGE
    }

    public pickupType pickupType;

    public AutoCubePickupEvent() {
        // Empty Constructor
    }

    public AutoCubePickupEvent(double timestamp, pickupType pickupType){
        super(timestamp);
        this.pickupType = pickupType;

    }
}

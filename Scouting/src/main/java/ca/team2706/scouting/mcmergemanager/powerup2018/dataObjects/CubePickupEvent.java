package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by Merge on 2018-01-20.
 */

public class CubePickupEvent extends Event {

    public static final String ID = "pickup_cube";

    public enum PickupType {
    powerup2018_integration
        PORTAL, GROUND, EXCHANGE, PYRAMID
    }

    public PickupType pickupType;

    public CubePickupEvent() {
        // Empty Constructor
    }

    public CubePickupEvent(double timestamp, PickupType PickupType){
        super(timestamp);
        this.pickupType = PickupType;

    }

}

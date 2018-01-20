package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class GearPickupEvent extends Event {

    public static final int objectiveId = 23;

    public GearPickupEvent(){

    }

    public enum GearPickupType {
        GROUND, WALL;
    }

    public GearPickupType pickupType; // true is from wall, false is from ground

    public GearPickupEvent(double timestamp, GearPickupType pickupType) {
        super(timestamp);

        this.pickupType = pickupType;
    }
}

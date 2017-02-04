package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class GearPickupEvent extends Event {

    public enum GearPickupType {
        GROUND, WALL;
    }

    public GearPickupType pickupType; // true is from wall, false is from ground
    public boolean successful; // true if no difficulty, false if had some difficulty

    public GearPickupEvent(double timestamp, GearPickupType pickupType, boolean successful) {
        super(timestamp);

        this.pickupType = pickupType;
        this.successful = successful;
    }
}

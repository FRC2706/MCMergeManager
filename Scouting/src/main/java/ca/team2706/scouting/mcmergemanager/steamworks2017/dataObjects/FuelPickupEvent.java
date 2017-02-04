package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class FuelPickupEvent extends Event {

    public enum FuelPickupType {
        HOPPER, WALL, GROUND;
    }

    public int amount;

    public FuelPickupType pickupType;

    public FuelPickupEvent(double timestamp, FuelPickupType pickupType, int amount) {
        super(timestamp);

        this.pickupType = pickupType;
        this.amount = amount;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class FuelPickupEvent extends Event {

    public static final int objectiveId = 21;

    public enum FuelPickupType {
        HOPPER, WALL, GROUND;
    }

    public int amount;

    public FuelPickupType pickupType;

    public FuelPickupEvent (){

    }

    public FuelPickupEvent(double timestamp, FuelPickupType pickupType, int amount) {
        super(timestamp);
//        PickupType = FuelPickupType.GROUND;
        this.pickupType = pickupType;
        this.amount = amount;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class FuelPickup {

    public double startTime;
    public double endTime;
    public int amount; // TODO: do we want this here, or in fuel shot?

    public int pickupLocation;
    public static final int HOPPER = 0;
    public static final int PLAYER_STATION = 1;
    public static final int GROUND = 2;

    public FuelPickup(double startTime, double endTime, int pickupLocation, int amount) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickupLocation = pickupLocation;
        this.amount = amount;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class GearPickup {

    public double startTime;
    public double endTime;

    public boolean pickupLocation; // true is from wall, false is from ground
    public boolean successful; // true if no difficulty, false if had some difficulty

    public GearPickup(double startTime, double endTime, boolean pickupLocation, boolean successful) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickupLocation = pickupLocation;
        this.successful = successful;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class GearDelivevry {

    public double startTime;
    public double endTime;

    public int lift;
    public int delivered;

    public static final int NO_LIFT = 0;
    public static final int LEFT = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 3;

    public static final int DELIVERED = 0;
    public static final int DROPPED_MOVING = 1;
    public static final int DROPPED_DELIVERING = 2;

    public GearDelivevry(double startTime, double endTime, int delivered, int lift) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.delivered = delivered;
        this.lift = lift;
    }
}

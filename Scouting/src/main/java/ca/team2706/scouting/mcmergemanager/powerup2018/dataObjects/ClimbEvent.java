package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by Merge on 2018-02-10.
 */

public class ClimbEvent extends Event{
    public static final String ID = "climb";

    public enum ClimbType {
        // Success Assisted means they were assisted climbing
        // Success Assisted Others means they assisted others while climbing
        NO_CLIMB, FAIL, SUCCESS_INDEPENDENT, SUCCESS_ASSISTED, SUCCESS_ASSISTED_OTHERS;
    }

    public ClimbType climbType;
    public double climb_time;

    public ClimbEvent(ClimbType climbType, double climb_time) {
        this.climb_time = climb_time;
        this.climbType = climbType;
    }


}
package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class PostGameObject {
    public String notes = "";

    public enum ClimbType {
        NO_CLIMB, FAIL, SUCCESS;
    }

    public ClimbType climbType;
    public double climb_time;
    public double time_dead;


    // empty constructor
    public PostGameObject() {}

    public PostGameObject(String notes, ClimbType climbType, double climb_time, double time_dead) {
        this.climbType = climbType;
        this.notes = notes;
        this.climb_time = climb_time;
        this.time_dead = time_dead;
    }
}

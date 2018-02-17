package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.io.Serializable;

/**
 * Created by dwall on 16/01/17.
 */

public class PostGameObject extends Event implements Serializable {
    public static String ID = "post_game";

    public enum ClimbType {
        NO_CLIMB, FAIL, SUCCESS;
    }

    public ClimbType climbType;
    public double climb_time;
    public double time_dead;
    public double time_defending;


    // empty constructor
    public PostGameObject() {}

    public PostGameObject(String notes, ClimbType climbType, double climb_time, double time_dead, double time_defending) {
        this.climbType = climbType;
        this.climb_time = climb_time;
        this.time_dead = time_dead;
        this.time_defending = time_defending;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class PostGameObject {
    public String notes = "";
    public int climb;
    public double climb_time;
    public double time_dead;

    public static final int NO_CLIMB = 0;
    public static final int FAIL_CLIMB = 1;
    public static final int SUCCESS_CLIMB = 2;

    // empty constructor
    public PostGameObject() {}

    public PostGameObject(String notes, int climb, double climb_time, double time_dead) {
        this.climb = climb;
        this.notes = notes;
        this.climb_time = climb_time;
        this.time_dead = time_dead;
    }
}

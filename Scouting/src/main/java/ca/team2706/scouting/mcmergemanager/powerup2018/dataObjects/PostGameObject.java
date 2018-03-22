package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import java.io.Serializable;

/**
 * Created by dwall on 16/01/17.
 */

public class PostGameObject extends Event implements Serializable {

    public static final String ID = "post_game";

    public double time_dead;
    public double time_defending;


    // empty constructor
    public PostGameObject() {}

    public PostGameObject(double time_dead, double time_defending, double time_stamp) {
        super(time_stamp);
        this.time_dead = time_dead;
        this.time_defending = time_defending;

    }
}

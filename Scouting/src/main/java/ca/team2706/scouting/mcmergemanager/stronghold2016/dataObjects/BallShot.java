package ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects;

import java.io.Serializable;

/**
 * Created by MCSoftware on 2016-01-22.
 */
public class BallShot implements Serializable {

    public static final int HIGH_GOAL = 0;
    public static final int LOW_GOAL  = 1;
    public static final int MISS      = 2;

    public int x;
    public int y;
    public double shootTime;
    public int whichGoal; // 0 = failed, 1 = low goal, 2 = high goal

    public BallShot() {

    }

    public BallShot(int x, int y, double shootTime, int whichGoal) {
        this.x = x;
        this.y = y;
        this.shootTime = shootTime;
        this.whichGoal = whichGoal;
    }
}
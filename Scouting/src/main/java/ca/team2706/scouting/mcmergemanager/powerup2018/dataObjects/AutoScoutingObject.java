package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import java.io.Serializable;

/**
 * Created by dwall on 16/01/17.
 */

public class AutoScoutingObject implements Serializable {

    public boolean start_gear;
    public boolean start_fuel;

    public int boiler_attempted;
    public static final int BOILER_NOT_ATTEPMTED  = 0;
    public static final int LOW_BOILER_ATTEPMTED  = 1;
    public static final int HIGH_BOILER_ATTEMPTED = 2;
    public int numFuelScored = 0;

    public int open_hopper; // 0 if none, 1+ store how many opened

    public int gear_delivered;
    public static final int NOT_DELIVERED    = 0;
    public static final int FAIL_DELIVERY    = 1;
    public static final int SUCCESS_DELIVERY = 2;

    public boolean crossedBaseline;

    // empty constructor
    public AutoScoutingObject() {}
}

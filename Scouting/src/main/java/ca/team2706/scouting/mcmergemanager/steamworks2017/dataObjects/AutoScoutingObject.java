package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class AutoScoutingObject {

    public boolean start_gear;
    public boolean start_fuel;

    public int boiler_attempted;
    public static final int BOILER_NOT_ATTEPMTED  = 0;
    public static final int LOW_BOILER_ATTEPMTED  = 1;
    public static final int HIGH_BOILER_ATTEMPTED = 2;
    public int numFuelScored; // slider for numFuelScored of fuel delivery

    public int open_hopper; // 0 if none, 1+ store how many opened

    public int gear_delivered;
    public static final int NOT_DELIVERED    = 0;
    public static final int FAIL_DELIVERY    = 1;
    public static final int SUCCESS_DELIVERY = 2;

    public boolean crossedBaseline;

    // empty constructor
    public AutoScoutingObject() {}


    // TODO out of date, and not being used anywhere. Fix if we need it.
    // public AutoScoutingObject(boolean start_gear, boolean start_fuel, int open_hopper, int gear_delivered, boolean boiler,
    //                           int accuracy) {
    //     this.start_fuel = start_fuel;
    //     this.start_gear = start_gear;
    //     this.open_hopper = open_hopper;
    //     this.gear_delivered = gear_delivered;
    //     this.boiler = boiler;
    //     this.numFuelScored = accuracy;
    // }
}

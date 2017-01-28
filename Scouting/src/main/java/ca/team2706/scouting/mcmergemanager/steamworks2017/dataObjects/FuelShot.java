package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class FuelShot {

    public double startTime;
    public double endTime;

    public int accuracy;
    public boolean boiler; // true is high, false is low

    public int x;
    public int y;

    public FuelShot(double startTime, double endTime, boolean boiler, int accuracy, int x, int y){
        this.startTime = startTime;
        this.endTime = endTime;
        this.boiler = boiler;
        this.accuracy = accuracy;
        this.x = x;
        this.y = y;
    }
}

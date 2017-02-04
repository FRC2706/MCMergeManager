package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class FuelShotEvent extends Event {

    public int numScored;
    public int numMissed;
    public boolean boiler; // true is high, false is low

    public int x;
    public int y;

    public FuelShotEvent(double timestamp, boolean boiler, int numScored, int numMissed, int x, int y){
        super(timestamp);

        this.boiler = boiler;
        this.numScored = numScored;
        this.numMissed = numMissed;
        this.x = x;
        this.y = y;
    }
}

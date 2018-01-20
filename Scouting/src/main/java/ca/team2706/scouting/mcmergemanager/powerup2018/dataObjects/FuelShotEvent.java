package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class FuelShotEvent extends Event {

    public static final int objectiveId = 20;

    public int numScored;
    public int numMissed;
    public boolean boiler; // true is high, false is low


    public FuelShotEvent() {

    }

    public FuelShotEvent(double timestamp, boolean boiler, int numScored, int numMissed){
        super(timestamp);

        this.boiler = boiler;
        this.numScored = numScored;
        this.numMissed = numMissed;
    }
}

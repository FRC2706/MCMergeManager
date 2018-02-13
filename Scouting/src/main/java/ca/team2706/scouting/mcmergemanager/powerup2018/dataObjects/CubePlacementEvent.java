package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by Merge on 2018-01-20.
 */

public class CubePlacementEvent extends Event{

    public static final String ID = "cube_pickup";

    public enum placementType {
        ALLIANCE_SWITCH, OPPOSING_SWITCH, SCALE, EXCHANGE, DROPPED
    }

    public placementType placementType;

    public CubePlacementEvent(){
        // Empty Constructor
    }

    public CubePlacementEvent(double timestamp, placementType placementType){
        super(timestamp);
        this.placementType = placementType;

    }

}

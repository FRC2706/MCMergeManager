package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-08.
 */

public class AutoCubePlacementEvent extends Event {
    public static final String ID = "auto_cube_placement";

    public enum PlacementType {
        ALLIANCE_SWITCH, SCALE, EXCHANGE, DROPPED
    }

    public PlacementType placementType;

    public AutoCubePlacementEvent(){
        // Empty Constructor
    }

    public AutoCubePlacementEvent(double timestamp, PlacementType placementType){
        super(timestamp);
        this.placementType = placementType;
    }

}

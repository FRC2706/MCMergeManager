package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by Merge on 2018-01-20.
 */

public class CubePlacementEvent extends Event {

    public static final String ID = "cube_placement";

    public enum PlacementType {
        ALLIANCE_SWITCH, OPPOSING_SWITCH, SCALE, EXCHANGE
    }

    public PlacementType placementType;


    public CubePlacementEvent(double timestamp, PlacementType placementType){
        super(timestamp);
        this.placementType = placementType;

    }

}

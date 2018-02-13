package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-08.
 */

public class AutoLineCrossEvent extends Event {
    public static final String ID = "auto_line_cross";

    public Boolean crossedAutoLine = false;

    public AutoLineCrossEvent() {
        // Empty Constructor
    }

    public AutoLineCrossEvent (double timestamp, Boolean crossedAutoLine) {
        super(timestamp);
        this.crossedAutoLine = crossedAutoLine;
    }


}

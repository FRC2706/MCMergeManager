package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-08.
 */

public class AutoMalfunctionEvent extends Event {
    public static final String ID = "auto_malfunction_event";

    public Boolean autoMalfunction = false;

    public AutoMalfunctionEvent() {
        // Empty Constructor
    }

    public AutoMalfunctionEvent (double timestamp, Boolean autoMalfunction) {
        super(timestamp);
        this.autoMalfunction = autoMalfunction;
    }
}

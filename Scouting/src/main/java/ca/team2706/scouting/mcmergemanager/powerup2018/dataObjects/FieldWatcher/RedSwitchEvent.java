package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class RedSwitchEvent extends Event {

    public static final String ID = "field_watcher_red_switch_event";

    public enum AllianceColour{
        BLUE, RED, NEUTRAL
    }

    public AllianceColour allianceColour;

    public RedSwitchEvent(double timestamp, AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }

    public RedSwitchEvent() {
        //Empty constructor
    }
}

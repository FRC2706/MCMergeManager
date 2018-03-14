package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class BlueSwitchEvent extends Event {

    public static final String ID = "field_watcher_blue_switch_event";

    private FieldWatcherObject.AllianceColour allianceColour;

    public BlueSwitchEvent(double timestamp, FieldWatcherObject.AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }

    public BlueSwitchEvent() {
        //Empty constructor
    }

    public FieldWatcherObject.AllianceColour getAllianceColour() {
        return allianceColour;
    }
    public void setAllianceColour(FieldWatcherObject.AllianceColour c) {
        allianceColour = c;
    }
}

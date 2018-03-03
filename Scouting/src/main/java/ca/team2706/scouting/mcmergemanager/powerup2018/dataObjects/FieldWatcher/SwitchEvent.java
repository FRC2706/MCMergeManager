package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class SwitchEvent extends Event {

    public static String ID = "field_watcher_boost";

    public enum AllianceColour{
        BLUE, RED
    }

    public AllianceColour allianceColour;

    public SwitchEvent(double timestamp, AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }
}

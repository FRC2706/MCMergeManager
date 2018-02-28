package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class LevitateEvent extends Event{
    public enum AllianceColour{
        BLUE, RED
    }

    public static String ID = "field_watcher_levitate";

    public AllianceColour allianceColour;

    public LevitateEvent(double timestamp, AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }
}

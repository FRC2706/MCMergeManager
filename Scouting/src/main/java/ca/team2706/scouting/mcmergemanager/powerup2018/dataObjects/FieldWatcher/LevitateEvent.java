package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class LevitateEvent extends Event{

    public static String ID = "field_watcher_levitate";

    private FieldWatcherObject.AllianceColour allianceColour;

    public LevitateEvent(double timestamp, FieldWatcherObject.AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }

    public LevitateEvent(){

    }

    public FieldWatcherObject.AllianceColour getAllianceColour() {
        return allianceColour;
    }
    public void setAllianceColour(FieldWatcherObject.AllianceColour c) {
        if(c == FieldWatcherObject.AllianceColour.NEUTRAL)
            throw new IllegalStateException();

        allianceColour = c;
    }
}

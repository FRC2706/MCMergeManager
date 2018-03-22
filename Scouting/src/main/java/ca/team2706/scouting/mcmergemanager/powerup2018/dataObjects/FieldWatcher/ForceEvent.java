package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class ForceEvent extends Event {

    public static String ID = "field_watcher_force";



    private FieldWatcherObject.AllianceColour allianceColour;

    public ForceEvent(double timestamp, FieldWatcherObject.AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }

    public ForceEvent() {

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

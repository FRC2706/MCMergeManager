package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class ScaleEvent extends Event {

    public static String ID = "field_watcher_scale";


    private FieldWatcherObject.AllianceColour allianceColour;

    public ScaleEvent(double timestamp, FieldWatcherObject.AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }
    public ScaleEvent(){

    }

    public FieldWatcherObject.AllianceColour getAllianceColour() {
        return allianceColour;
    }
    public void setAllianceColour(FieldWatcherObject.AllianceColour c) {
        allianceColour = c;
    }
}

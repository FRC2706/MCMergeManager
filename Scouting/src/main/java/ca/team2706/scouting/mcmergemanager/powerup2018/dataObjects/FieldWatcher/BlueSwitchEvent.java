package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-02-24.
 */

public class BlueSwitchEvent extends Event {
    public enum AllianceColour{
        BLUE, RED, NEUTRAL
    }

    public AllianceColour allianceColour;

    public BlueSwitchEvent(double timestamp, AllianceColour allianceColour) {
        super(timestamp);
        this.allianceColour = allianceColour;
    }

    public BlueSwitchEvent() {
        //Empty constructor
    }
}

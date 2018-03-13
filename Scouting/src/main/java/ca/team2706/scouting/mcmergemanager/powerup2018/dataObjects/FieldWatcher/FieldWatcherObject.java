package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;


public class FieldWatcherObject implements Serializable {

    public enum AllianceColour{
        BLUE, RED, NEUTRAL
    }

    private ArrayList<Event> events = new ArrayList<>();


    public void add(Event e) {
        events.add(e);
    }

    /**
     * Puts all events during teleop mode into a list, sorted by timestamp.
     * Useful for doing cycle analysis in StatsEngine.
     */
    public ArrayList<Event> getEvents() {
        Collections.sort(events);

        return events;
    }


    public FieldWatcherObject() {

    }

}

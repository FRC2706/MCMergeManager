package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by Merge on 2018-01-20.
 */

public class AutoScoutingObject implements Serializable {

    private ArrayList<Event> events = new ArrayList<>();

    public void add(Event e) {
        events.add(e);
    }

    /**
     * Puts all events during auto mode into a list, sorted by timestamp.
     * Useful for doing cycle analysis in StatsEngine2018.
     */
    public ArrayList<Event> getEvents() {
        Collections.sort(events);
        return events;
    }

    public AutoScoutingObject() {

    }


    

}

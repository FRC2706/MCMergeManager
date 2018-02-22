package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class TeleopScoutingObject implements Serializable {

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


    public TeleopScoutingObject() {

    }

}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * This is an abstract class to represent any timestamped event during an FRC match.
 *
 * It implements Comparator to allow them to be sorted by timestamp.
 */
public abstract class Event implements Comparable<Event> {
    public double timestamp;

    public Event(double timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Compare this event to another event and return -1,0,1 if this event started before, at the same time as, or after the other event.
     */
    @Override
    public int compareTo(Event e) {
        if (this.timestamp < e.timestamp)
            return -1;
        else if (this.timestamp > e.timestamp)
            return 1;
        else
            return 0;
    }
}

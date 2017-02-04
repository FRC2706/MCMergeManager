package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class DefenseEvent extends Event {

    public int skill;

    public DefenseEvent(double timestamp,  int skill) {
        super(timestamp);
        this.skill = skill;
    }
}

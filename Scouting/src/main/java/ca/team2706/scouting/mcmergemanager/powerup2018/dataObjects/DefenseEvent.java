package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;

/**
 * Created by dwall on 16/01/17.
 */

public class DefenseEvent extends Event {


    public int skill;

    public DefenseEvent(){

    }

    public DefenseEvent(double timestamp, int skill) {
        super(timestamp);
        this.skill = skill;
    }
}

package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by dwall on 16/01/17.
 */

public class DefenseEvent extends Event {

    public static final int objectiveId = 24;

    public int skill;

    public DefenseEvent(){

    }

    public DefenseEvent(double timestamp,  int skill) {
        super(timestamp);
        this.skill = skill;
    }
}

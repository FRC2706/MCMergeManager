package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import java.io.Serializable;

// For feeding the CycleDisplay window
public class Cycle implements Serializable{
    public enum CycleType {
        GEAR, HIGH_GOAL, LOW_GOAL, CLIMB, DEFENSE;
    }

    public CycleType cycleType;
    public double startTime=0.0;
    public double endTime=0.0;
    public boolean success=true;

    public Cycle() { }

    public Cycle(CycleType type) {
        this.cycleType = type;
    }

    public Cycle clone() {
        Cycle c = new Cycle();

        c.cycleType = this.cycleType;
        c.startTime = this.startTime;
        c.endTime = this.endTime;
        c.success = this.success;

        return c;
    }

    public Cycle clone(CycleType newType) {
        Cycle c = new Cycle();

        c.cycleType = newType;
        c.startTime = this.startTime;
        c.endTime = this.endTime;
        c.success = this.success;

        return c;
    }


}
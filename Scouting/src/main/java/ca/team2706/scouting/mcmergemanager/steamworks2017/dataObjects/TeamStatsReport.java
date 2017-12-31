package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.RepairTimeObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;

public class TeamStatsReport implements Serializable {

    // Overall Stats
    public int    teamNo;
    public int    numMatchesPlayed;
    public int    wins;
    public int    losses;
    public int    ties;
    public double OPR;  // Offensive Power Rating, a standard stat in FRC
    public double DPR;  // Defensive Power Rating, a standard stat in FRC
    // could also include CCWM, and PMR, see http://www.chiefdelphi.com/media/papers/2174

    public double scheduleToughnessByWLT;    // Whether, on average, their opponents were stronger than their allies, or the other way around.
                                             // ie.: Are they seeded artificially high or artificially low by schedule luck?
    public double scheduleToughnessByOPR;

    public List<TeamDataObject> repairTimeObjects;
    public double repair_time_percent;
    public double working_time_percent;
    // To get the total number of gearDeliveryData points, use repairTimeObjects.length

    public boolean badManners; // TODO: I have no idea how we're capturing this


    // Auto Stats
    public int auto_numTimesCrossedBaseline;
    public int auto_numGearsDelivered;
    public int auto_numGearsFailed;
    public int auto_numHighGoalAttempts;  // number of matches in which they attempted to score in the high goal
    public double auto_avgNumFuelScoredHigh;
    public int auto_numLowGoalAttempts;  // number of matches in which they attempted to score in the low goal
    public double auto_avgNumFuelScoredLow;



    // Teleop Stats

    /**
     * For feeding the CycleDisplay window..
     */
    public static class CyclesInAMatch implements Serializable {
        public int matchNo;
        public ArrayList<Cycle> cycles = new ArrayList<>();

        public CyclesInAMatch(int matchNo) {
            this.matchNo = matchNo;
        }
    }
    public ArrayList<CyclesInAMatch> cycleMatches = new ArrayList<>();

    // No time for comments; here's a bunch of stuff
    public int numFuelGroundCycles = 0;
    public int numFuelWallCycles = 0;
    public int numFuelHopperCycles = 0;
    public int numFuelHighCycles = 0;
    public int numFuelLowCycles = 0;
    public int numGearGroundCycles = 0;
    public int numGearWallCycles = 0;
    public int numGearCycles = 0;
    public int totalGroundCycles = 0;
    public int totalWallCycles = 0;
    public String favouriteCycleType = "(No Scouting Data)";
    public String favouritePickupLocation = "(No Scouting Data)";

    // Fuel pickups
    public double teleop_fuelGroundPickups_avgPerMatch;
    public double teleop_fuelWallPickups_avgPerMatch;
    public double teleop_fuelHopperPickups_avgPerMatch;

    public double teleop_fuelGroundPickups_avgCycleTime;
    public double teleop_fuelWallPickups_avgCycleTime;
    public double teleop_fuelHopperPickups_avgCycleTime;


    // Fuel scoring
    public double teleop_fuelScoredHigh_avgPerMatch;
    public double teleop_fuelScoredHigh_avgPerCycle;
    public double teleop_fuelScoredHigh_total;
    public double teleop_fuelMissedHigh_avgPerMatch;
    public double teleop_fuelScoredLow_avgPerMatch;
    public double teleop_fuelScoredLow_avgPerCycle;
    public double teleop_fuelScoredLow_total;
    public double teleop_fuelMissedLow_avgPerMatch;

    public double teleop_fuelHigh_aveCycleTime;
    public double teleop_fuelHigh_minCycleTime;
    public double teleop_fuelHigh_maxCycleTime;
    public double teleop_fuelLow_aveCycleTime;
    public double teleop_fuelLow_minCycleTime;
    public double teleop_fuelLow_maxCycleTime;


    // Gears
    public double teleop_gearsDelivered_avgPerMatch;
    public double teleop_gearsDropped_avgPerMatch;
    public double teleop_gearsPickupWall_avgPerMatch;
    public double teleop_gearsPickupGround_avgPerMatch;
    public double teleop_gears_avgCycleTime;
    public double teleop_gears_minCycleTime;
    public double teleop_gears_maxCycleTime;
    public GearDelivevryEvent.Lift teleop_gears_preferedLift = GearDelivevryEvent.Lift.NONE;  // FEEDER_SIDE, CENTRE, BOILER_SIDE
    public int teleop_gearsScored_feederSide;
    public int teleop_gearsScored_centre;
    public int teleop_gearsScored_boilerSide;



    // Defense and deadness
    public double avgTimeSpentPlayingDef;
    public double avgDeadness;      // an int representing % of match spent wich mech problems
    public int numMatchesNoDeadness;
    public double highestDeadness;  // the highest % deadness in a single match.


    // Climbing
    public int      climbSuccesses;
    public int      climbFailures;
    public int      climbAttepmts;
    public double   climb_avgTime;
    public double   climb_minTime;  // Their single, fastest climb


    public String notes = "";


    public MatchSchedule teamMatcheSchedule;
    public MatchData teamMatchData;

}

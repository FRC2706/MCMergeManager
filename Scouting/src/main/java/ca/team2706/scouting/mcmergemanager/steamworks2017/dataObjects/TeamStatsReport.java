package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public double scheduleToughness;    // Whether, on average, their opponents were stronger than their allies, or the other way around.
    // ie.: Are they seeded artificially high or artificially low by schedule luck?

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
    public ArrayList<Cycle> cycles = new ArrayList<>();



    // TODO -- we'll come back to the pin map later
        public List<BallShot> missedTeleopShots;         // to draw pins on map
        public List<BallShot> successfulTeleopHighShots; // to draw pins on map
        public List<BallShot> successfulTeleopLowShots;  // to draw pins on map

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
    public GearDelivevryEvent.Lift teleop_gears_preferedLift;  // FEEDER_SIDE, CENTRE, BOILER_SIDE
    public int teleop_gearsScored_feederSide;
    public int teleop_gearsScored_centre;
    public int teleop_gearsScored_boilerSide;



    // Defense and deadness
    public double avgTimeSpentPlayingDef;
    public double avgDeadness;      // an int representing % of match spent wich mech problems
    public double highestDeadness;  // the highest % deadness in a single match.


    // Climbing
    public int      climbSuccesses;
    public int      climbFailures;
    public int      climbAttepmts;
    public double   climb_avgTime;


    public String notes = "";


    public ca.team2706.scouting.mcmergemanager.backend.MatchSchedule teamMatcheSchedule;
    public MatchData teamMatchData;


    public TeamStatsReport() {
            missedTeleopShots = new ArrayList<>();
            successfulTeleopHighShots = new ArrayList<>();
            successfulTeleopLowShots = new ArrayList<>();
    }

}

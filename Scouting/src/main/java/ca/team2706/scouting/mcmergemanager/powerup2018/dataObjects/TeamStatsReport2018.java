package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;

/**
 * Created by daniel on 12/02/18.
 */

public class TeamStatsReport2018 {

    // Overall Stats
    public int    teamNumber;
    public int    numMatchesPlayed;
    public int    wins;
    public int    losses;
    public int    ties;
    public double OPR;  // Offensive Power Rating, a standard stat in FRC
    public double DPR;  // Defensive Power Rating, a standard stat in FRC

    public double scheduleToughnessByWLT;    // Whether, on average, their opponents were stronger than their allies, or the other way around.
    // ie.: Are they seeded artificially high or artificially low by schedule luck?
    public double scheduleToughnessByOPR;

    public List<TeamDataObject> repairTimeObjects;
    public double repair_time_percent;
    public double working_time_percent;

    public boolean badManners;


    // Auto mode vars
    public int autoPickupPortal, autoPickupGround, autoPickupExchange;
    public int autoPlaceAllianceSwitch, autoPlaceScale, autoPlaceExchange, autoPlaceDropped;
    public int autoTotalPlacedCubes;
    public int autoCrossedLine;
    public int autoMalfunction;
    public double autoAvgMalfunctions;

    // Teleop vars
    public int totalPickupPortal, totalPickupGround, totalPickupExchange;
    public int totalPlaceSwitch, totalPlaceScale, totalPlaceExchange, totalPlaceDropped;

    public double pickupPortalAvgMatch, pickupGroundAvgMatch, pickupExchangeAvgMatch;
    public double placeSwitchAvgMatch, placeScaleAvgMatch, placeExchangeAvgMatch, droppedAvgMatch;

    public double pickupGroundAvgCycleTime, pickupPortalAvgCycleTime, pickupExchangeAvgCycleTime;
    public double placeSwitchAvgCycleTime, placeScaleAvgCycleTime, placeExchangeAvgCycleTime, droppedAvgCycleTime;

    public CubePickupEvent.PICKUP_TYPE favouritePickup;
    public CubePlacementEvent.PLACEMENT_TYPE favouritePlacement;

    // Post game stuff
    public double avgDeadness, highestDeadness, numMatchesNoDeadness;

    public MatchSchedule teamMatcheSchedule;
    public MatchData teamMatchData;
    public ArrayList<CyclesInAMatch> cycleMatches = new ArrayList<>();

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
}

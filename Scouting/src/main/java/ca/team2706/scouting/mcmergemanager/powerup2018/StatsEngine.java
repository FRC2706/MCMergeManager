package ca.team2706.scouting.mcmergemanager.powerup2018;


import android.util.Log;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoLineCrossEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoMalfunctionEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.ClimbEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Cycle;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;

import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.ALLIANCE_SWITCH;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.DROPPED;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.EXCHANGE;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.SCALE;

public class StatsEngine implements Serializable {

    public static final String OPR_FILENAME = "OPRs.json";


    private MatchData matchData;
    private MatchSchedule matchSchedule;
    private List<TeamDataObject> repairTimeObjects;

    // mapping team numbers to various stats
    private Map<Integer, Double> OPRs;
    private Map<Integer, Double> DPRs;
    private Map<Integer, WLT> records;  // need for computation of schedule toughness

    public StatsEngine(MatchData matchData, MatchSchedule matchSchedule) {
        this(matchData, matchSchedule, null);
    }

    /**
     * Contructor
     *
     * @param repairTimeObjects May be null
     **/
    public StatsEngine(MatchData matchData, MatchSchedule matchSchedule, List<TeamDataObject> repairTimeObjects) {
        this.matchData = matchData;
        this.matchSchedule = matchSchedule;
        this.repairTimeObjects = repairTimeObjects;
    }

    public TeamStatsReport getTeamStatsReport(int teamNumber) {
        // If there is no match data then there is no data to compile
        if (matchData == null)
            throw new IllegalStateException("no match data");

        TeamStatsReport teamStatsReport = new TeamStatsReport();
        teamStatsReport.teamMatchData = matchData.filterByTeam(teamNumber);

        // Fill in the stats for the given team
        fillInOverallStats(teamStatsReport, teamNumber);
        fillInAutoStats(teamStatsReport);
        fillInTeleopStats(teamStatsReport);

        return teamStatsReport;
    }

    // Fill in the stats that don't need to analyse the match
    private void fillInOverallStats(TeamStatsReport teamStatsReport, int teamNumber) {
        teamStatsReport.teamNumber = teamNumber;

        teamStatsReport.numMatchesPlayed = 0;
        for (MatchSchedule.Match match : teamStatsReport.teamMatcheSchedule.getMatches()) {
            if (match.getBlueScore() > 0 && match.getRedScore() > 0)
                teamStatsReport.numMatchesPlayed++;
        }

        if (records == null)
            computeRecords();

        if (records.get(teamNumber) != null) {
            teamStatsReport.wins = records.get(teamNumber).wins;
            teamStatsReport.losses = records.get(teamNumber).losses;
            teamStatsReport.ties = records.get(teamNumber).ties;
        }

        // The oprs from tba
        try {
            teamStatsReport.OPR = FileUtils.getOprsFromFile().getJSONObject("oprs").getInt("frc" + teamNumber);
            teamStatsReport.DPR = FileUtils.getOprsFromFile().getJSONObject("dprs").getInt("frc" + teamNumber);
            teamStatsReport.CCWM = FileUtils.getOprsFromFile().getJSONObject("ccwms").getInt("frc" + teamNumber);
        } catch(JSONException e) {
            Log.d("json err", e.toString());
        }

        teamStatsReport.scheduleToughnessByWLT = computeScheduleToughnessByWLT(teamNumber);
        teamStatsReport.scheduleToughnessByOPR = computeScheduleToughnessByOPR(teamNumber);

        // TODO: repair team objects

        // TODO: add bad manners
    }

    // Fill in the stats that are only to do with auto mode
    //TODO: Avg delivery time in auto
    private void fillInAutoStats(TeamStatsReport teamStatsReport) {
        for (MatchData.Match match : teamStatsReport.teamMatchData.matches) {
            AutoScoutingObject autoScouting = match.autoScoutingObject;

            // Loop through all the auto events and add the stats to the team-stats
            for (Event event : autoScouting.getEvents()) {
                if (event instanceof AutoCubePickupEvent) {
                    switch (((AutoCubePickupEvent) event).pickupType) {
                        case EXCHANGE:
                            teamStatsReport.autoPickupExchange++;
                            break;
                        case GROUND:
                            teamStatsReport.autoPickupGround++;
                            break;
                        case PORTAL:
                            teamStatsReport.autoPickupPortal++;
                            break;
                    }
                }
                if (event instanceof AutoCubePlacementEvent) {
                    switch (((AutoCubePlacementEvent) event).placementType) {
                        case SCALE:
                            teamStatsReport.autoPlaceScale++;
                            break;
                        case DROPPED:
                            teamStatsReport.autoPlaceDropped++;
                            break;
                        case EXCHANGE:
                            teamStatsReport.autoPlaceExchange++;
                            break;
                        case ALLIANCE_SWITCH:
                            teamStatsReport.autoPlaceAllianceSwitch++;
                            break;
                    }
                }
                if (event instanceof AutoLineCrossEvent) {
                    if (((AutoLineCrossEvent) event).crossedAutoLine)
                        teamStatsReport.autoCrossedLine++;
                }
                if (event instanceof AutoMalfunctionEvent) {
                    if (((AutoMalfunctionEvent) event).autoMalfunction)
                        teamStatsReport.autoMalfunction++;
                }
            }
        }

        // Compute the averages and totals, when calculating averages make sure no divide by zero
        teamStatsReport.autoTotalPlacedCubes = teamStatsReport.autoPlaceAllianceSwitch
                + teamStatsReport.autoPlaceScale + teamStatsReport.autoPlaceExchange;
        if (teamStatsReport.numMatchesPlayed != 0)
            teamStatsReport.autoAvgMalfunctions = teamStatsReport.autoMalfunction / teamStatsReport.numMatchesPlayed;
    }

    private void fillInTeleopStats(TeamStatsReport teamStatsReport) {
        // Loop through all the matches, calculate cycles using a big ol state maching
        for (MatchData.Match match : teamStatsReport.teamMatchData.matches) {
            // Process all the events during this match in a big state machine.
            ArrayList<Event> events = match.teleopScoutingObject.getEvents();

            TeamStatsReport.CyclesInAMatch cyclesInThisMatch = new TeamStatsReport.CyclesInAMatch(match.preGameObject.matchNumber);

            // State machine state vars
            boolean inCubePickupCycle = false, inCubePlaceCycle = false;
            Cycle cubeCycle = new Cycle();

            // Loop through all events in the match
            for (Event event : events) {

                if (event instanceof CubePickupEvent) {
                    CubePickupEvent c = (CubePickupEvent) event;

                    // Get the timestamp
                    cubeCycle.startTime = c.timestamp;

                    // Add the stats and
                    switch (c.pickupType) {
                        case EXCHANGE:
                            teamStatsReport.pickupExchangeAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPickupExchange++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PICKUP_EXCHANGE));
                            break;
                        case GROUND:
                            teamStatsReport.pickupGroundAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPickupGround++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PICKUP_GROUND));
                            break;
                        case PORTAL:
                            teamStatsReport.pickupPortalAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPickupPortal++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PICKUP_PORTAL));
                            break;
                    }
                } else if (event instanceof CubePlacementEvent) {
                    CubePlacementEvent c = (CubePlacementEvent) event;

                    // Time stamp stuff
                    cubeCycle.endTime = c.timestamp;

                    // If dropped fail the cycle
                    if (c.placementType == DROPPED)
                        cubeCycle.success = false;
                    else
                        cubeCycle.success = true;

                    // Add stats and cycle
                    switch (c.placementType) {
                        case EXCHANGE:
                            teamStatsReport.placeExchangeAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPlaceExchange++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PLACE_EXCHANGE));
                            break;
                        case ALLIANCE_SWITCH:   // TODO: Could change, but the opposing switch and ours are basically the same thing
                        case OPPOSING_SWITCH:
                            teamStatsReport.placeSwitchAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPlaceSwitch++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PLACE_SWITCH));
                            break;
                        case DROPPED:
                            teamStatsReport.droppedAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPlaceDropped++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PLACE_DROPPED));
                            break;
                        case SCALE:
                            teamStatsReport.placeScaleAvgCycleTime += cubeCycle.getCycleTime();
                            teamStatsReport.totalPlaceScale++;
                            cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.PLACE_SCALE));
                            break;
                    }
                } else if(event instanceof ClimbEvent) {
                    ClimbEvent c = (ClimbEvent) event;

                    // Type of climb
                    switch(c.climbType) {
                        case FAIL:
                            teamStatsReport.failClimb++;
                            break;
                        case NO_CLIMB:
                            teamStatsReport.noClimb++;
                            break;
                        case SUCCESS_ASSISTED:
                            teamStatsReport.assistedClimb++;
                            break;
                        case SUCCESS_INDEPENDENT:
                            teamStatsReport.independentClimb++;
                            break;
                        case SUCCESS_ASSISTED_OTHERS:
                            teamStatsReport.assistedOthersClimb++;
                            break;
                    }

                    // Time for climbing
                    teamStatsReport.avgClimbTime += c.climb_time;
                    if(c.climb_time > teamStatsReport.maxClimbTime) {
                        teamStatsReport.maxClimbTime = c.climb_time;
                    } else if(c.climb_time < teamStatsReport.minClimbTime) {
                        teamStatsReport.minClimbTime = c.climb_time;
                    }
                } else if(event instanceof PostGameObject) {
                    PostGameObject p = (PostGameObject) event;

                    // Defending
                    teamStatsReport.avgTimeDefending += p.time_defending;
                    if(p.time_defending > teamStatsReport.maxTimeDefending) {
                        teamStatsReport.maxTimeDefending = p.time_defending;
                    }

                    // Time dead
                    if (p.time_dead > 0) {
                        teamStatsReport.avgDeadness += p.time_dead;

                        if (p.time_dead > teamStatsReport.highestDeadness)
                            teamStatsReport.highestDeadness = p.time_dead;
                    } else {
                        teamStatsReport.numMatchesNoDeadness++;
                    }
                }

                // TODO: i don't think i need to finish cycles
            }

            // Add the cycles to the report
            teamStatsReport.cycleMatches.add(cyclesInThisMatch);
        }


        // Calculate the averages for the team, make sure not dividing by zero
        int numMatchesPlayed = matchData.matches.size();
        if (teamStatsReport.numMatchesPlayed != 0) {

            // Avg times
            teamStatsReport.pickupGroundAvgCycleTime /= teamStatsReport.totalPickupGround;
            teamStatsReport.pickupPortalAvgCycleTime /= teamStatsReport.totalPickupPortal;
            teamStatsReport.pickupExchangeAvgCycleTime /= teamStatsReport.totalPickupExchange;

            teamStatsReport.placeSwitchAvgCycleTime /= teamStatsReport.totalPlaceSwitch;
            teamStatsReport.placeScaleAvgCycleTime /= teamStatsReport.totalPlaceScale;
            teamStatsReport.placeExchangeAvgCycleTime /= teamStatsReport.totalPlaceExchange;
            teamStatsReport.droppedAvgCycleTime /= teamStatsReport.totalPlaceDropped;

            // Avg per match
            teamStatsReport.pickupGroundAvgMatch /= numMatchesPlayed;
            teamStatsReport.pickupPortalAvgMatch /= numMatchesPlayed;
            teamStatsReport.pickupExchangeAvgMatch /= numMatchesPlayed;

            teamStatsReport.placeSwitchAvgMatch /= numMatchesPlayed;
            teamStatsReport.placeScaleAvgMatch /= numMatchesPlayed;
            teamStatsReport.placeExchangeAvgMatch /= numMatchesPlayed;
            teamStatsReport.droppedAvgMatch /= numMatchesPlayed;

            // Deadness
            teamStatsReport.avgDeadness /= numMatchesPlayed;

            // Climbing
            teamStatsReport.avgClimbTime /= numMatchesPlayed;

            // Defending
            teamStatsReport.avgTimeDefending /= numMatchesPlayed;
        }

        // Find the favourites of teams
        if(teamStatsReport.pickupPortalAvgMatch > teamStatsReport.pickupExchangeAvgMatch &&
                teamStatsReport.pickupPortalAvgMatch > teamStatsReport.pickupGroundAvgMatch) {
            teamStatsReport.favouritePickup = CubePickupEvent.PickupType.PORTAL;
        } else if(teamStatsReport.pickupGroundAvgMatch > teamStatsReport.pickupExchangeAvgMatch) {
            teamStatsReport.favouritePickup = CubePickupEvent.PickupType.GROUND;
        } else {
            teamStatsReport.favouritePickup = CubePickupEvent.PickupType.EXCHANGE;
        }

        if(teamStatsReport.placeExchangeAvgMatch > teamStatsReport.placeScaleAvgMatch &&
                teamStatsReport.placeExchangeAvgMatch > teamStatsReport.placeSwitchAvgMatch &&
                teamStatsReport.placeExchangeAvgMatch > teamStatsReport.droppedAvgMatch) {
            teamStatsReport.favouritePlacement = EXCHANGE;
        } else if(teamStatsReport.placeScaleAvgMatch > teamStatsReport.placeSwitchAvgMatch &&
                teamStatsReport.placeScaleAvgMatch > teamStatsReport.droppedAvgMatch) {
            teamStatsReport.favouritePlacement = SCALE;
        } else if(teamStatsReport.placeSwitchAvgMatch > teamStatsReport.droppedAvgMatch) {
            teamStatsReport.favouritePlacement = ALLIANCE_SWITCH;   // Either alliance or opposing switch
        } else {
            teamStatsReport.favouritePlacement = CubePlacementEvent.PlacementType.DROPPED;
        }

    }

    // Determines the wlt of team
    private void computeRecords() {
        records = new HashMap<>();

        for (MatchSchedule.Match match : matchSchedule.getMatches()) {
            if (match.getBlueScore() == -1 || match.getRedScore() == -1)
                continue;   // this match has not been played yet

            if (match.getBlueScore() > match.getRedScore()) {
                createAndGetTeam(match.getBlue1()).addWin();
                createAndGetTeam(match.getBlue2()).addWin();
                createAndGetTeam(match.getBlue3()).addWin();
                createAndGetTeam(match.getRed1()).addLoss();
                createAndGetTeam(match.getRed2()).addLoss();
                createAndGetTeam(match.getRed3()).addLoss();
            } else if (match.getBlueScore() < match.getRedScore()) {
                createAndGetTeam(match.getBlue1()).addLoss();
                createAndGetTeam(match.getBlue2()).addLoss();
                createAndGetTeam(match.getBlue3()).addLoss();
                createAndGetTeam(match.getRed1()).addWin();
                createAndGetTeam(match.getRed2()).addWin();
                createAndGetTeam(match.getRed3()).addWin();
            } else {
                createAndGetTeam(match.getBlue1()).addTie();
                createAndGetTeam(match.getBlue2()).addTie();
                createAndGetTeam(match.getBlue3()).addTie();
                createAndGetTeam(match.getRed1()).addTie();
                createAndGetTeam(match.getRed2()).addTie();
                createAndGetTeam(match.getRed3()).addTie();
            }
        }
    }

    private WLT createAndGetTeam(int teamNo) {
        if (!records.containsKey(teamNo))
            records.put(teamNo, new WLT());

        return records.get(teamNo);
    }

    // Win, loss, and ties of team
    // Used for computing the schedule toughness
    public class WLT {
        int wins;
        int losses;
        int ties;

        void addWin() {
            wins++;
        }

        void addLoss() {
            losses++;
        }

        void addTie() {
            ties++;
        }
    }

    /**
     * Schedule toughness is a measure of your opponents' wins over your allies' wins.
     * A toughness of 1.0 is a neutral schedule.
     */
    private double computeScheduleToughnessByWLT(int teamNo) {
        if (records == null)
            computeRecords();

        int alliesWins = 0;
        int opponentsWins = 0;

        try {
            for (MatchSchedule.Match match : matchSchedule.filterByTeam(teamNo).getMatches()) {
                // am I blue or red?
                if (match.getBlue1() == teamNo || match.getBlue2() == teamNo || match.getBlue3() == teamNo) {
                    if (match.getBlue1() != teamNo)
                        alliesWins += records.get(match.getBlue1()).wins;

                    if (match.getBlue2() != teamNo)
                        alliesWins += records.get(match.getBlue2()).wins;

                    if (match.getBlue3() != teamNo)
                        alliesWins += records.get(match.getBlue3()).wins;

                    opponentsWins += records.get(match.getRed1()).wins;
                    opponentsWins += records.get(match.getRed2()).wins;
                    opponentsWins += records.get(match.getRed3()).wins;
                } else {
                    if (match.getRed1() != teamNo)
                        alliesWins += records.get(match.getRed1()).wins;

                    if (match.getRed2() != teamNo)
                        alliesWins += records.get(match.getRed2()).wins;

                    if (match.getRed3() != teamNo)
                        alliesWins += records.get(match.getRed3()).wins;

                    opponentsWins += records.get(match.getBlue1()).wins;
                    opponentsWins += records.get(match.getBlue2()).wins;
                    opponentsWins += records.get(match.getBlue3()).wins;
                }
            }
        } catch (NullPointerException e) {
            //nothing
        }
        if (alliesWins == 0 || opponentsWins == 0)
            return 1.0;

        return (((double) opponentsWins * 2) / (alliesWins * 3));
    }

    /**
     * Schedule toughness is a measure of your opponents' wins over your allies' wins.
     * A toughness of 1.0 is a neutral schedule.
     */
    private double computeScheduleToughnessByOPR(int teamNo) {
        if (OPRs == null)
            return 1.0;

        double alliesOPRs = 0;
        double opponentsOPRs = 0;

        try {
            for (MatchSchedule.Match match : matchSchedule.filterByTeam(teamNo).getMatches()) {
                // am I blue or red?
                if (match.getBlue1() == teamNo || match.getBlue2() == teamNo || match.getBlue3() == teamNo) {
                    if (match.getBlue1() != teamNo)
                        alliesOPRs += OPRs.get(match.getBlue1());

                    if (match.getBlue2() != teamNo)
                        alliesOPRs += OPRs.get(match.getBlue2());

                    if (match.getBlue3() != teamNo)
                        alliesOPRs += OPRs.get(match.getBlue3());

                    opponentsOPRs += OPRs.get(match.getRed1());
                    opponentsOPRs += OPRs.get(match.getRed2());
                    opponentsOPRs += OPRs.get(match.getRed3());
                } else {
                    if (match.getRed1() != teamNo)
                        alliesOPRs += OPRs.get(match.getRed1());

                    if (match.getRed2() != teamNo)
                        alliesOPRs += OPRs.get(match.getRed2());

                    if (match.getRed3() != teamNo)
                        alliesOPRs += OPRs.get(match.getRed3());

                    opponentsOPRs += OPRs.get(match.getBlue1());
                    opponentsOPRs += OPRs.get(match.getBlue2());
                    opponentsOPRs += OPRs.get(match.getBlue3());
                }
            }
        } catch (NullPointerException e) {
            //nothing
        }
        if (alliesOPRs == 0 || opponentsOPRs == 0)
            return 1.0;

        return (opponentsOPRs * 2) / (alliesOPRs * 3);
    }
}

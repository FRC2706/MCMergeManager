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
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubeDroppedEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Cycle;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BlueSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BoostEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ForceEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.LevitateEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.RedSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;

import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.ALLIANCE_SWITCH;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent.PlacementType.SCALE;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject.AllianceColour.BLUE;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject.AllianceColour.NEUTRAL;
import static ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject.AllianceColour.RED;

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

    public StatsEngine(MatchSchedule matchSchedule) {
        this.matchSchedule = matchSchedule;
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

    public TeamStatsReport getTeamStatsReport(int teamNumber, boolean fieldWatcherStats) {
        TeamStatsReport teamStatsReport = new TeamStatsReport();
        teamStatsReport.teamMatchData = FileUtils.loadMatchData(teamNumber);

        // If there is no match data then there is no data to compile
        if(teamStatsReport.teamMatchData == null)
            throw new IllegalStateException("no match data");

        // Fill in the stats for the given team
        fillInOverallStats(teamStatsReport, teamNumber);
        fillInAutoStats(teamStatsReport);
        fillInTeleopStats(teamStatsReport);
        if(matchSchedule != null && fieldWatcherStats)
            fillInFieldWatcherStats(teamStatsReport, teamNumber);

        return teamStatsReport;
    }


    public void fillInFieldWatcherStats(TeamStatsReport teamStatsReport, int teamNumber) {
        for(MatchSchedule.Match match : teamStatsReport.teamMatcheSchedule.getMatches()) {
            MatchData.Match fieldWatcherData = FileUtils.getFieldWatcherMatch(match.getMatchNo());

            // Check if there is data for that match
            if(fieldWatcherData == null)
                continue;

            // Find what alliance team is on
            FieldWatcherObject.AllianceColour alliance = RED;   // true is blue, false is red
            if(match.getBlue1() == teamNumber || match.getBlue2() == teamNumber || match.getBlue3() == teamNumber)
                alliance = BLUE;

            // Booleans to keep track of who has what. True is blue, false is red.
            // Booleans and cycles are used for both auto and teleop because the positions should carry over
            FieldWatcherObject.AllianceColour blueSwitch = NEUTRAL, redSwitch = NEUTRAL, scale = NEUTRAL;
            Cycle blueSwitchCycle = new Cycle();
            Cycle redSwitchCycle = new Cycle();
            Cycle scaleCycle = new Cycle();

            TeamStatsReport.CyclesInAMatch cyclesInAMatch = new TeamStatsReport.CyclesInAMatch(match.getMatchNo());
            for(Event event : fieldWatcherData.autoScoutingObject.getEvents()) {
                if(event instanceof BlueSwitchEvent) {
                    BlueSwitchEvent e = (BlueSwitchEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != blueSwitch) {

                        // Finish last cycle
                        blueSwitchCycle.endTime = e.timestamp;
                        switch (blueSwitch) {
                            case RED:
                                if(alliance == RED) {
                                    teamStatsReport.avgOpposingSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                                } else {
                                   teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                   cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if(alliance == BLUE) {
                                    teamStatsReport.avgAllianceSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                // No matter what team, doesn't have possession
                                if(alliance == BLUE) {
                                    teamStatsReport.avgAllianceSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                                }
                                break;
                        }

                        // Reset start time for cycle
                        blueSwitchCycle.startTime = e.timestamp;

                        // Set which team has it
                        blueSwitch = e.getAllianceColour();
                    }
                } else if(event instanceof BoostEvent) {
                    BoostEvent b = (BoostEvent) event;

                    // Which team got it
                    if(alliance == b.getAllianceColour()) {
                        teamStatsReport.boost++;
                        teamStatsReport.boostTimes.add(b.timestamp);
                    } else {
                        teamStatsReport.oppBoost++;
                    }
                } else if(event instanceof ForceEvent) {
                    ForceEvent f = (ForceEvent) event;

                    // Which team got it
                    if(alliance == f.getAllianceColour()) {
                        teamStatsReport.force++;
                        teamStatsReport.forceTimes.add(f.timestamp);
                    } else {
                        teamStatsReport.oppForce++;
                    }
                } else if(event instanceof LevitateEvent) {
                    LevitateEvent l = (LevitateEvent) event;

                    // Which team got it
                    if(alliance == l.getAllianceColour()) {
                        teamStatsReport.levitate++;
                        teamStatsReport.levitateTimes.add(l.timestamp);
                    } else {
                        teamStatsReport.oppLevitate++;
                    }
                } else if(event instanceof RedSwitchEvent) {
                    RedSwitchEvent e = (RedSwitchEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != redSwitch) {

                        // Finish last cycle
                        redSwitchCycle.endTime = e.timestamp;
                        switch (redSwitch) {
                            case RED:
                                if (alliance == RED) {
                                    teamStatsReport.avgAllianceSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if (alliance == BLUE) {
                                    teamStatsReport.avgOpposingSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                if (alliance == RED) {
                                    teamStatsReport.avgAllianceSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                                }
                                break;
                        }

                        // Reset start time for cycle
                        redSwitchCycle.startTime = e.timestamp;

                        // Set which team has it
                        redSwitch = e.getAllianceColour();
                    }
                } else if(event instanceof ScaleEvent) {
                    ScaleEvent e = (ScaleEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != scale) {

                        // Finish last cycle
                        scaleCycle.endTime = e.timestamp;
                        switch (scale) {
                            case RED:
                                if (alliance == RED) {
                                    teamStatsReport.avgScalePossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                                } else {
                                    teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if (alliance == BLUE) {
                                    teamStatsReport.avgScalePossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                                } else {
                                    teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                teamStatsReport.avgScaleNeutralPerMatch += scaleCycle.getCycleTime();
                                cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NEUTRAL));
                                break;
                        }

                        // Reset start time for cycle
                        scaleCycle.startTime = e.timestamp;

                        // Set which team has it
                        scale = e.getAllianceColour();
                    }
                }


            }

            for(Event event : fieldWatcherData.fieldWatcherObject.getEvents()) {
                if(event instanceof BlueSwitchEvent) {
                    BlueSwitchEvent e = (BlueSwitchEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != blueSwitch) {

                        // Finish last cycle
                        blueSwitchCycle.endTime = e.timestamp;
                        switch (blueSwitch) {
                            case RED:
                                if(alliance == RED) {
                                    teamStatsReport.avgOpposingSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if(alliance == BLUE) {
                                    teamStatsReport.avgAllianceSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                // No matter what team, doesn't have possession
                                if(alliance == BLUE) {
                                    teamStatsReport.avgAllianceSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                                }
                                break;
                        }

                        // Reset start time for cycle
                        blueSwitchCycle.startTime = e.timestamp;

                        // Set which team has it
                        blueSwitch = e.getAllianceColour();
                    }
                } else if(event instanceof BoostEvent) {
                    BoostEvent b = (BoostEvent) event;

                    // Which team got it
                    if(alliance == b.getAllianceColour()) {
                        teamStatsReport.boost++;
                        teamStatsReport.boostTimes.add(b.timestamp);
                    } else {
                        teamStatsReport.oppBoost++;
                    }
                } else if(event instanceof ForceEvent) {
                    ForceEvent f = (ForceEvent) event;

                    // Which team got it
                    if(alliance == f.getAllianceColour()) {
                        teamStatsReport.force++;
                        teamStatsReport.forceTimes.add(f.timestamp);
                    } else {
                        teamStatsReport.oppForce++;
                    }
                } else if(event instanceof LevitateEvent) {
                    LevitateEvent l = (LevitateEvent) event;

                    // Which team got it
                    if(alliance == l.getAllianceColour()) {
                        teamStatsReport.levitate++;
                        teamStatsReport.levitateTimes.add(l.timestamp);
                    } else {
                        teamStatsReport.oppLevitate++;
                    }
                } else if(event instanceof RedSwitchEvent) {
                    RedSwitchEvent e = (RedSwitchEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != redSwitch) {

                        // Finish last cycle
                        redSwitchCycle.endTime = e.timestamp;
                        switch (redSwitch) {
                            case RED:
                                if (alliance == RED) {
                                    teamStatsReport.avgAllianceSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if (alliance == BLUE) {
                                    teamStatsReport.avgOpposingSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                                } else {
                                    teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                if (alliance == RED) {
                                    teamStatsReport.avgAllianceSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                                } else {
                                    teamStatsReport.avgOpposingSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                                }
                                break;
                        }

                        // Reset start time for cycle
                        redSwitchCycle.startTime = e.timestamp;

                        // Set which team has it
                        redSwitch = e.getAllianceColour();
                    }
                } else if(event instanceof ScaleEvent) {
                    ScaleEvent e = (ScaleEvent) event;

                    // Make sure they didn't tap twice
                    if(e.getAllianceColour() != scale) {

                        // Finish last cycle
                        scaleCycle.endTime = e.timestamp;
                        switch (scale) {
                            case RED:
                                if (alliance == RED) {
                                    teamStatsReport.avgScalePossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                                } else {
                                    teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                                }
                                break;
                            case BLUE:
                                if (alliance == BLUE) {
                                    teamStatsReport.avgScalePossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                                } else {
                                    teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                                }
                                break;
                            case NEUTRAL:
                                teamStatsReport.avgScaleNeutralPerMatch += scaleCycle.getCycleTime();
                                cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NEUTRAL));
                                break;
                        }

                        // Reset start time for cycle
                        scaleCycle.startTime = e.timestamp;

                        // Set which team has it
                        scale = e.getAllianceColour();
                    }
                }
            }

            // Finish the cycles for the match
            switch(blueSwitch) {
                case NEUTRAL:
                    if(alliance == BLUE) {
                        teamStatsReport.avgAllianceSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                    } else {
                        teamStatsReport.avgOpposingSwitchNeutralPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                    }
                    break;
                case BLUE:
                    if(alliance == BLUE) {
                        teamStatsReport.avgAllianceSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                    } else {
                        teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                    }
                    break;
                case RED:
                    if(alliance == BLUE) {
                        teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                    } else {
                        teamStatsReport.avgOpposingSwitchPossessionPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                    }
                    break;
            }
            switch(redSwitch) {
                case NEUTRAL:
                    if(alliance == RED) {
                        teamStatsReport.avgAllianceSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NEUTRAL));
                    } else {
                        teamStatsReport.avgOpposingSwitchNeutralPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NEUTRAL));
                    }
                    break;
                case BLUE:
                    if(alliance == RED) {
                        teamStatsReport.avgAllianceSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_POSSESSION));
                    } else {
                        teamStatsReport.avgOpposingSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_NO_POSSESSION));
                    }
                    break;
                case RED:
                    if(alliance == RED) {
                        teamStatsReport.avgAllianceSwitchNotPossessionPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.ALLIANCE_SWITCH_NO_POSSESSION));
                    } else {
                        teamStatsReport.avgOpposingSwitchPossessionPerMatch += redSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(redSwitchCycle.clone(Cycle.CycleType.OPPOSING_SWITCH_POSSESSION));
                    }
                    break;
            }
            switch(scale) {
                case NEUTRAL:
                    teamStatsReport.avgScaleNeutralPerMatch += scaleCycle.getCycleTime();
                    cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NEUTRAL));
                    break;
                case BLUE:
                    if(alliance == BLUE) {
                        teamStatsReport.avgScalePossessionPerMatch += scaleCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                    } else {
                        teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                    }
                    break;
                case RED:
                    if(alliance == BLUE) {
                        teamStatsReport.avgScaleNotPossessionPerMatch += scaleCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(scaleCycle.clone(Cycle.CycleType.SCALE_NO_POSSESSION));
                    } else {
                        teamStatsReport.avgScalePossessionPerMatch += blueSwitchCycle.getCycleTime();
                        cyclesInAMatch.cycles.add(blueSwitchCycle.clone(Cycle.CycleType.SCALE_POSSESSION));
                    }
                    break;
            }
        }

        // Get average times per match
        if(teamStatsReport.numMatchesPlayed != 0) {
            teamStatsReport.avgAllianceSwitchPossessionPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgOpposingSwitchPossessionPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgScalePossessionPerMatch /= teamStatsReport.numMatchesPlayed;

            teamStatsReport.avgAllianceSwitchNotPossessionPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgOpposingSwitchNotPossessionPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgScaleNotPossessionPerMatch /= teamStatsReport.numMatchesPlayed;

            teamStatsReport.avgAllianceSwitchNeutralPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgOpposingSwitchNeutralPerMatch /= teamStatsReport.numMatchesPlayed;
            teamStatsReport.avgScaleNeutralPerMatch /= teamStatsReport.numMatchesPlayed;
        }
    }

    // Fill in the stats that don't need to analyse the match
    private void fillInOverallStats(TeamStatsReport teamStatsReport, int teamNumber) {
        teamStatsReport.teamNumber = teamNumber;

        teamStatsReport.teamMatcheSchedule = matchSchedule.filterByTeam(teamNumber);

        teamStatsReport.numMatchesPlayed = 0;
        for (MatchSchedule.Match match : teamStatsReport.teamMatcheSchedule.getMatches()) {
            if (match.getBlueScore() > 0 || match.getRedScore() > 0)
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
                        case SWITCH:
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
            boolean inSwitchCycle = false, inScaleCycle = false, inExchangeCycle = false;
            Cycle cubeCycle = new Cycle();

            // Loop through all events in the match
            for (Event event : events) {

                if (event instanceof CubePickupEvent) {
                    CubePickupEvent c = (CubePickupEvent) event;

                    double cycleTime = c.timestamp - cubeCycle.startTime;

                    if(inSwitchCycle) {
                        teamStatsReport.switchAvgCycleTime += cycleTime;
                        cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.SWITCH));
                        inSwitchCycle = false;
                    }
                    if(inScaleCycle) {
                        teamStatsReport.scaleAvgCycleTime += cycleTime;
                        cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.SCALE));
                        inScaleCycle = false;
                    }
                    if(inExchangeCycle) {
                        teamStatsReport.exchangeAvgCycleTime += cycleTime;
                        cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.EXCHANGE));
                        inExchangeCycle = false;
                    }

                    // Get the timestamp
                    cubeCycle.startTime = c.timestamp;


                    // Add the stats and
                    switch (c.pickupType) {
                        case EXCHANGE:
                            teamStatsReport.totalPickupExchange++;
                            break;
                        case GROUND:
                            teamStatsReport.totalPickupGround++;
                            break;
                        case PORTAL:
                            teamStatsReport.totalPickupPortal++;
                            break;
                        case PYRAMID:
                            teamStatsReport.totalPickupPyramid++;
                            break;
                    }
                } else if (event instanceof CubePlacementEvent) {
                    CubePlacementEvent c = (CubePlacementEvent) event;

                    // Time stamp stuff
                    cubeCycle.endTime = c.timestamp;

                    // Cycle has succeeded
                    cubeCycle.success = true;

                    // Add stats and cycle
                    switch (c.placementType) {
                        case EXCHANGE:
                            teamStatsReport.totalPlaceExchange++;
                            inExchangeCycle = true;
                            break;
                        case ALLIANCE_SWITCH:   // TODO: Could change, but the opposing switch and ours are basically the same thing
                        case OPPOSING_SWITCH:
                            teamStatsReport.totalPlaceSwitch++;
                            inSwitchCycle = true;
                            break;
                        case SCALE:
                            teamStatsReport.totalPlaceScale++;
                            inScaleCycle = true;
                            break;
                    }
                } else if(event instanceof CubeDroppedEvent) {
                    CubeDroppedEvent c = (CubeDroppedEvent) event;

                    cubeCycle.endTime = c.timestamp;

                    switch(c.dropType) {
                        case FUMBLE:
                            teamStatsReport.totalFumbles++;
                            break;
                        case LEFT_IT:
                            teamStatsReport.totalLeftIt++;
                            break;
                        case EASY_PICKUP:
                            teamStatsReport.totalEasyDrop++;
                            break;
                    }

                    teamStatsReport.droppedAvgCycleTime += cubeCycle.getCycleTime();
                    cyclesInThisMatch.cycles.add(cubeCycle.clone(Cycle.CycleType.DROPPED));
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
                        case ON_BASE:
                            teamStatsReport.onBase++;
                            break;
                    }

                    // Time for climbing
                    teamStatsReport.avgClimbTime += c.climb_time;
                    if(c.climb_time > teamStatsReport.maxClimbTime) {
                        teamStatsReport.maxClimbTime = c.climb_time;
                    }
                    // Don't include climb times of zero
                    if(c.climb_time != 0 && c.climb_time < teamStatsReport.minClimbTime) {
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
        teamStatsReport.numMatchesPlayed = teamStatsReport.teamMatchData.matches.size();
        if (teamStatsReport.numMatchesPlayed != 0) {

            // Avg times
            teamStatsReport.switchAvgCycleTime /= (double) teamStatsReport.totalPlaceSwitch;
            teamStatsReport.scaleAvgCycleTime /= (double) teamStatsReport.totalPlaceScale;
            teamStatsReport.exchangeAvgCycleTime /= (double) teamStatsReport.totalPlaceExchange;
            teamStatsReport.droppedAvgCycleTime /= (double) teamStatsReport.totalPlaceDropped;

            // Avg per match
            teamStatsReport.pickupGroundAvgMatch = (double) teamStatsReport.totalPickupGround / teamStatsReport.numMatchesPlayed;
            teamStatsReport.pickupPortalAvgMatch = (double) teamStatsReport.totalPickupPortal / teamStatsReport.numMatchesPlayed;
            teamStatsReport.pickupExchangeAvgMatch = (double) teamStatsReport.totalPickupExchange / teamStatsReport.numMatchesPlayed;
            teamStatsReport.pickupPyramidAvgMatch = (double) teamStatsReport.totalPickupPyramid / teamStatsReport.numMatchesPlayed;

            teamStatsReport.placeSwitchAvgMatch = (double) teamStatsReport.totalPlaceSwitch / teamStatsReport.numMatchesPlayed;
            teamStatsReport.placeScaleAvgMatch = (double) teamStatsReport.totalPlaceScale / teamStatsReport.numMatchesPlayed;
            teamStatsReport.placeExchangeAvgMatch = (double) teamStatsReport.totalPlaceExchange / teamStatsReport.numMatchesPlayed;
            teamStatsReport.droppedAvgMatch = (double) teamStatsReport.totalPlaceDropped / teamStatsReport.numMatchesPlayed;

            // Deadness
            teamStatsReport.avgDeadness /= teamStatsReport.numMatchesPlayed;

            // Climbing
            teamStatsReport.avgClimbTime /= teamStatsReport.numMatchesPlayed;

            // Defending
            teamStatsReport.avgTimeDefending /= teamStatsReport.numMatchesPlayed;
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
                teamStatsReport.placeExchangeAvgMatch > teamStatsReport.placeSwitchAvgMatch) {
            teamStatsReport.favouritePlacement = CubePlacementEvent.PlacementType.EXCHANGE;
        } else if(teamStatsReport.placeScaleAvgMatch > teamStatsReport.placeSwitchAvgMatch) {
            teamStatsReport.favouritePlacement = SCALE;
        } else {
            teamStatsReport.favouritePlacement = ALLIANCE_SWITCH;   // Either alliance or opposing switch
        }

        // Make sure min climb isnt a huuge number
        if(teamStatsReport.minClimbTime > 150)
            teamStatsReport.minClimbTime = 0;
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

package ca.team2706.scouting.mcmergemanager.steamworks2017;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.RepairTimeObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Cycle;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.DefenseEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelShotEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearDelivevryEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.MatchData;

import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;


public class StatsEngine implements Serializable{


    /** Class Stats Engine **/

    private MatchData matchData;
    private MatchSchedule matchSchedule;
    private List<TeamDataObject> repairTimeObjects;


    /**
     * Constructor
     */
    public StatsEngine(MatchData matchData, MatchSchedule matchSchedule) {
        this(matchData, matchSchedule, null);
    }

    /** Contructor
     *
     * @param repairTimeObjects May be null
     **/
    public StatsEngine(MatchData matchData, MatchSchedule matchSchedule, List<TeamDataObject> repairTimeObjects) {
        this.matchData = matchData;
        this.matchSchedule = matchSchedule;
        this.repairTimeObjects = repairTimeObjects;
    }

    /** This constructor meant more for testing than for actual use **/
    public StatsEngine(MatchSchedule matchSchedule) {
        this.matchSchedule = matchSchedule;
        this.matchData = new MatchData();

        computeOPRs();
    }


    /**
     * Fill in a TeamStatsReport about the given team.
     */
    public TeamStatsReport getTeamStatsReport(int teamNo) {
        if (matchData == null)
            throw new IllegalStateException("matchData is null");

        TeamStatsReport teamStatsReport = new TeamStatsReport();
        teamStatsReport.teamMatchData = matchData.filterByTeam(teamNo);

        fillInOverallStats(teamStatsReport, teamNo);
        fillInAutoStats(teamStatsReport);
        fillInTeleopStats(teamStatsReport);

        return teamStatsReport;
    }





    // mapping team numbers to various stats
    private Map<Integer, Double> OPRs;
    private Map<Integer, Double> DPRs;
    private Map<Integer, WLT> records;  // need for computation of schedule toughness


    private class WLT implements Serializable {
        int wins;
        int losses;
        int ties;

        void addWin()  { wins++; }
        void addLoss() { losses++; }
        void addTie()  { ties++; }
    }

    public Map<Integer, Double> getOPRs() {
        if (OPRs == null)
            computeOPRs();

        return OPRs;
    }


    private void computeDPRs() {
        // TODO
        DPRs = new HashMap<>();
    }

    /** Do some linear algebra to compute Offensive Power Rating for each team. **/
    private void computeOPRs() {
        if ( matchSchedule == null)
            return;


        // First, build the participation matrix
        // ie which matches did which team participate in.
        // Only include matches that we have a score for.

        // Figure out what the axis are for our matrix
        ArrayList<Integer> teams = new ArrayList<>();
        HashMap<Integer, Integer> blueScores = new HashMap<>();  // map of match #'s to scores
        HashMap<Integer, Integer> redScores = new HashMap<>();  // map of match #'s to scores

        for(MatchSchedule.Match match : matchSchedule.getMatches()) {
            if (match.getBlueScore() != -1) {
                blueScores.put(match.getMatchNo(), match.getBlueScore());

                if (!teams.contains(match.getBlue1()))
                    teams.add(match.getBlue1());

                if (!teams.contains(match.getBlue2()))
                    teams.add(match.getBlue2());

                if (!teams.contains(match.getBlue3()))
                    teams.add(match.getBlue3());
            }

            if (match.getRedScore() != -1) {
                redScores.put(match.getMatchNo(), match.getRedScore());

                if (! teams.contains(match.getRed1()) )
                    teams.add(match.getRed1());

                if (! teams.contains(match.getRed2()) )
                    teams.add(match.getRed2());

                if (! teams.contains(match.getRed3()) )
                    teams.add(match.getRed3());
            }
        }

        // sort all arrays and use position in array as indices into the matrix
        ArrayList<Integer> blueMatcheNos = new ArrayList<>(blueScores.keySet());
        ArrayList<Integer> redMatcheNos = new ArrayList<>(redScores.keySet());

        Collections.sort(teams);
        Collections.sort(blueMatcheNos);
        Collections.sort(redMatcheNos);

        int offset = blueScores.size();

        // build the matrix
        // This is a participation matrix with the matches in the first axis, and teams in the second.
        // If a team has participated in a match, that cell gets a 1, otherwise it gets a 0.
        // Note that we treat the blue and red alliances as if they were playing completely seperate matches for the sake of OPR.
        double[][] Mdbl = new double[blueScores.size()+redScores.size()][teams.size()];
        double[] allScores = new double[blueScores.size()+redScores.size()];

        for(MatchSchedule.Match match : matchSchedule.getMatches()) {
            if (match.getBlueScore() != -1) {
                int matchNoIdx = blueMatcheNos.indexOf(match.getMatchNo());
                Mdbl[matchNoIdx][ teams.indexOf(match.getBlue1()) ] = 1;
                Mdbl[matchNoIdx][ teams.indexOf(match.getBlue2()) ] = 1;
                Mdbl[matchNoIdx][ teams.indexOf(match.getBlue3()) ] = 1;

                allScores[matchNoIdx] = match.getBlueScore();
            }

            if (match.getRedScore() != -1) {
                int matchNoIdx = blueMatcheNos.indexOf(match.getMatchNo()) + offset;
                Mdbl[matchNoIdx][ teams.indexOf(match.getRed1()) ] = 1;
                Mdbl[matchNoIdx][ teams.indexOf(match.getRed2()) ] = 1;
                Mdbl[matchNoIdx][ teams.indexOf(match.getRed3()) ] = 1;

                allScores[matchNoIdx] = match.getRedScore();
            }
        }

        if(allScores.length == 0) {
            // we don't have scores for any match
            OPRs = new HashMap<>();
            for (int i = 0; i < teams.size(); i++) {
                OPRs.put(teams.get(i), 0d);
            }

        } else {
            Matrix M = new Matrix(Mdbl);
            Matrix Y = new Matrix(allScores, allScores.length);

            try {

                Matrix MatOPRs = M.transpose().times(M).inverse().times(M.transpose()).times(Y);

                // now that we have the gearDeliveryData, fill in the hashmap
                OPRs = new HashMap<>();
                for (int i = 0; i < teams.size(); i++) {
                    OPRs.put(teams.get(i), MatOPRs.get(i, 0));
                }
            } catch (Exception e) {
                // probably a Singular Matrix exception -- means we don't have enough gearDeliveryData yet

                // we don't have scores for any match
                OPRs = new HashMap<>();
                for (int i = 0; i < teams.size(); i++) {
                    OPRs.put(teams.get(i), 0d);
                }
            }
        }
    }

    private void computeRecords() {
        records = new HashMap<>();

        for(MatchSchedule.Match match : matchSchedule.getMatches()) {
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

    /** I wish this was included in the default Map interface **/
    private WLT createAndGetTeam(int teamNo) {
        if (!records.containsKey(teamNo))
            records.put(teamNo, new WLT());

        return records.get(teamNo);
    }

    /**
     * Schedule toughness is a measure of your opponents' wins over your allies' wins.
     * A toughness of 1.0 is a neutral schedule.
     */
    private double computeScheduleToughness(int teamNo) {
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
        if (alliesWins == 0) {
            if (opponentsWins == 0)
                return 1.0;
            else
                return Double.POSITIVE_INFINITY;
        }

        return (((double) opponentsWins * 2) / (alliesWins * 3));
    }


    /**
     *
     */
    private void fillInAutoStats(TeamStatsReport teamStatsReport) {
        // assumption: the teamStatsReport starts zeroed out.

        if (matchData == null)
            throw new IllegalStateException("matchData is null");

        // loop over all matches that this team was in
        for(MatchData.Match match : teamStatsReport.teamMatchData.matches) {
            AutoScoutingObject autoData = match.autoScoutingObject;

            teamStatsReport.auto_numTimesCrossedBaseline += autoData.crossedBaseline ? 1 : 0;

            switch (autoData.gear_delivered) {

                case (AutoScoutingObject.SUCCESS_DELIVERY):
                    teamStatsReport.auto_numGearsDelivered += 1;
                    break;

                case (AutoScoutingObject.FAIL_DELIVERY):
                    teamStatsReport.auto_numGearsFailed += 1;
                    break;

                case (AutoScoutingObject.NOT_DELIVERED):
                    // We're not displaying this, so nothing to record.
                    break;
            }


            switch (autoData.boiler_attempted) {
                case (AutoScoutingObject.LOW_BOILER_ATTEPMTED):
                    teamStatsReport.auto_numLowGoalAttempts += 1;
                    teamStatsReport.auto_avgNumFuelScoredLow += autoData.numFuelScored;
                    break;

                case (AutoScoutingObject.HIGH_BOILER_ATTEMPTED):
                    teamStatsReport.auto_numHighGoalAttempts += 1;
                    teamStatsReport.auto_avgNumFuelScoredHigh += autoData.numFuelScored;
                    break;
            }
        }


        // compute averages
        if (teamStatsReport.auto_numLowGoalAttempts != 0)
            teamStatsReport.auto_avgNumFuelScoredLow /= teamStatsReport.auto_numLowGoalAttempts;

        if (teamStatsReport.auto_numHighGoalAttempts != 0)
            teamStatsReport.auto_avgNumFuelScoredHigh /= teamStatsReport.auto_numHighGoalAttempts;
    }

    /**
     *
     */
    private void fillInOverallStats(TeamStatsReport teamStatsReport, int teamNo) {
        teamStatsReport.teamNo = teamNo;

        if (matchData == null)
            throw new IllegalStateException("matchData is null");

        teamStatsReport.teamMatcheSchedule = matchSchedule.filterByTeam(teamNo);

        teamStatsReport.numMatchesPlayed = 0;
        for(MatchSchedule.Match match : teamStatsReport.teamMatcheSchedule.getMatches() ) {
            if (match.getBlueScore() > 0 && match.getRedScore() > 0)
                teamStatsReport.numMatchesPlayed++;
        }

        if (records == null)
            computeRecords();

        if (records.get(teamNo) != null) {
            teamStatsReport.wins = records.get(teamNo).wins;
            teamStatsReport.losses = records.get(teamNo).losses;
            teamStatsReport.ties = records.get(teamNo).ties;
        }

        if (OPRs == null)
            computeOPRs();

        if (OPRs.get(teamNo) != null)
            teamStatsReport.OPR = OPRs.get(teamNo);

        if (DPRs == null)
            computeDPRs();

        if (DPRs.get(teamNo) != null)
            teamStatsReport.DPR = DPRs.get(teamNo);

        teamStatsReport.scheduleToughness = computeScheduleToughness(teamNo);

        // Deal with RepairTimeObjects to see how much of the event teams spent
        // repairing their robot.

        List<TeamDataObject> teamRepairTimeObjects = FileUtils.filterTeamDataByTeam(teamNo, repairTimeObjects);

        if(teamRepairTimeObjects != null) {
            int repairCount = 0;
            int workingCount = 0;
            for (TeamDataObject teamDataObject : teamRepairTimeObjects) {
                RepairTimeObject repairTimeObject = (RepairTimeObject) teamDataObject;
                switch (repairTimeObject.getRepairStatus()) {
                    case REPAIRING:
                        repairCount++;
                        break;
                    case WORKING:
                        workingCount++;
                        break;
                }
            }
            teamStatsReport.repair_time_percent  = ((double) repairCount) / teamRepairTimeObjects.size() * 100;
            teamStatsReport.working_time_percent = ((double) workingCount) / teamRepairTimeObjects.size() * 100;
            teamStatsReport.repairTimeObjects = teamRepairTimeObjects;
        }

        // TODO:  Do bad manners stuff
        //teamStatsReport.badManners = ?

    }

    /**
     *
     */
    private void fillInTeleopStats(TeamStatsReport teamStatsReport) {
        // assumption: the teamStatsReport starts zeroed out.

        if (matchData == null)
            throw new IllegalStateException("matchData is null");

        for(MatchData.Match match : teamStatsReport.teamMatchData.matches) {

            // Process all the events during this match in a big state machine.

            ArrayList<Event> events = match.teleopScoutingObject.getEvents();

            TeamStatsReport.CyclesInAMatch cyclesInThisMatch = new TeamStatsReport.CyclesInAMatch(match.preGameObject.matchNumber);

            // state machine state vars
            boolean justScoredFuel=false;
            boolean inFuelCycle=false, inFuelGroundCycle=false, inFuelWallCycle=false, inFuelHopperCycle=false;
            boolean inFuelHighCycle=false, inFuelLowCycle=false;
            Cycle currFuelCycle = new Cycle();

            boolean inGearCycle=false;
            Cycle currGearCycle = new Cycle(Cycle.CycleType.GEAR);

            // Events in the list are already sorted in chronological order, so just loop over them!
            // loop over all events
            for (Event event : events) {

                // Fuel pickup
                if (event instanceof FuelPickupEvent) {
                    FuelPickupEvent fuelPickupEvent = (FuelPickupEvent) event;

                    // Deal with completing the previous fuel cycle
                    if (justScoredFuel) {
                        // complicated logic:
                        //      A cycle ends eiter with a pickup or end of match.
                        //      The reason a shoot event does not complete a cycle is that they may stop shooting,
                        //      then start shooting again without loading more, which should count as the same cycle.

                        // Do the math to finish the last cycle
                        double cycleTime = currFuelCycle.endTime - currFuelCycle.startTime;

                        // You can be in more than one type of fuel cycle at the same time.

                        if(inFuelGroundCycle) {
                            teamStatsReport.teleop_fuelGroundPickups_avgCycleTime += cycleTime;
                            teamStatsReport.numFuelGroundCycles++;
                        }

                        if (inFuelWallCycle) {
                            teamStatsReport.teleop_fuelWallPickups_avgCycleTime += cycleTime;
                            teamStatsReport.numFuelWallCycles++;
                        }

                        if (inFuelHopperCycle) {
                            teamStatsReport.teleop_fuelHopperPickups_avgCycleTime += cycleTime;
                            teamStatsReport.numFuelHopperCycles++;
                        }

                        if (inFuelHighCycle) {
                            teamStatsReport.teleop_fuelHigh_aveCycleTime += cycleTime;

                            if (cycleTime > teamStatsReport.teleop_fuelHigh_maxCycleTime)
                                teamStatsReport.teleop_fuelHigh_maxCycleTime = cycleTime;

                            if (cycleTime < teamStatsReport.teleop_fuelHigh_minCycleTime)
                                teamStatsReport.teleop_fuelHigh_minCycleTime = cycleTime;

                            Cycle c = currFuelCycle.clone(Cycle.CycleType.HIGH_GOAL);
                            cyclesInThisMatch.cycles.add(c);
                            teamStatsReport.numFuelHighCycles++;
                        }

                        if (inFuelLowCycle) {
                            teamStatsReport.teleop_fuelLow_aveCycleTime += cycleTime;

                            if (cycleTime > teamStatsReport.teleop_fuelLow_maxCycleTime)
                                teamStatsReport.teleop_fuelLow_maxCycleTime = cycleTime;

                            if (cycleTime < teamStatsReport.teleop_fuelLow_minCycleTime)
                                teamStatsReport.teleop_fuelLow_minCycleTime = cycleTime;

                            cyclesInThisMatch.cycles.add(currFuelCycle.clone(Cycle.CycleType.LOW_GOAL));
                            teamStatsReport.numFuelLowCycles++;
                        }

                        // reset all the state vars
                        inFuelCycle = false;
                        inFuelGroundCycle = inFuelWallCycle = inFuelHopperCycle = false;
                        inFuelHighCycle = inFuelLowCycle = false;
                    }


                    // if we're already in a fuel cycle, don't update the cycle start time
                    if (!inFuelCycle) {
                        currFuelCycle.startTime = fuelPickupEvent.timestamp;
                    }

                    inFuelCycle = true; justScoredFuel=false;

                    switch (fuelPickupEvent.pickupType) {

                        case GROUND:
                            inFuelGroundCycle = true;
                            teamStatsReport.teleop_fuelGroundPickups_avgPerMatch += fuelPickupEvent.amount;
                            break;

                        case WALL:
                            inFuelWallCycle = true;
                            teamStatsReport.teleop_fuelWallPickups_avgPerMatch += fuelPickupEvent.amount;
                            break;

                        case HOPPER:
                            inFuelHopperCycle = true;
                            teamStatsReport.teleop_fuelHopperPickups_avgPerMatch++;
                            break;
                    }
                } // instanceof FuelPickupEvent




                // Fuel scoring
                else if (event instanceof FuelShotEvent) {
                    FuelShotEvent fuelShotEvent = (FuelShotEvent) event;
                    // complicated logic:
                    //      A cycle ends eiter with a pickup or end of match.
                    //      The reason a shoot event does not complete a cycle is that they maystop shooting,
                    //      then start shooting again without loading more which should count as the same cycle.

                    currFuelCycle.endTime = fuelShotEvent.timestamp;

                    if (fuelShotEvent.boiler) {  // high
                        teamStatsReport.teleop_fuelScoredHigh_total += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelScoredHigh_avgPerMatch += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelScoredHigh_avgPerCycle += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelMissedHigh_avgPerMatch += fuelShotEvent.numMissed;

                        inFuelHighCycle = true;
                    }
                    else { // low
                        teamStatsReport.teleop_fuelScoredLow_total += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelScoredLow_avgPerMatch += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelScoredLow_avgPerCycle += fuelShotEvent.numScored;
                        teamStatsReport.teleop_fuelMissedLow_avgPerMatch += fuelShotEvent.numMissed;

                        inFuelLowCycle = true;
                    }
                } // instanceof FuelShotEvent



                // Gear Pickups
                else if (event instanceof GearPickupEvent) {
                    GearPickupEvent gearPickupEvent = (GearPickupEvent) event;

                    // Since you can only hold one gear at a time, the logic for completing a cycle is _much_ simpler. Phew.

                    // if we're already in a gear cycle, don't update the cycle start time
                    if (!inGearCycle) {
                        currGearCycle.startTime = gearPickupEvent.timestamp;
                    }

                    inGearCycle = true;

                    switch (gearPickupEvent.pickupType) {
                        case GROUND:
                            teamStatsReport.teleop_gearsPickupGround_avgPerMatch++;
                            teamStatsReport.numGearGroundCycles++;
                            break;

                        case WALL:
                            teamStatsReport.numGearWallCycles++;
                            teamStatsReport.teleop_gearsPickupWall_avgPerMatch++;
                            break;
                    }
                } // instanceof GearPickupEvent


                // Gear Delivery
                else if (event instanceof GearDelivevryEvent) {
                    GearDelivevryEvent gearDelivevryEvent = (GearDelivevryEvent) event;

                    // Since you can only hold one gear at a time, the logic for completing a cycle is _much_ simpler. Phew.

                    if (gearDelivevryEvent.deliveryStatus == GearDelivevryEvent.GearDeliveryStatus.DROPPED_MOVING ||
                            gearDelivevryEvent.deliveryStatus == GearDelivevryEvent.GearDeliveryStatus.DROPPED_DELIVERING) {
                        teamStatsReport.teleop_gearsDropped_avgPerMatch++;
                    }
                    else if (gearDelivevryEvent.deliveryStatus == GearDelivevryEvent.GearDeliveryStatus.DELIVERED) {
                        inGearCycle = false;
                        currGearCycle.endTime = gearDelivevryEvent.timestamp;
                        double cycleTime = currGearCycle.endTime - currGearCycle.startTime;

                        teamStatsReport.teleop_gearsDelivered_avgPerMatch++;
                        teamStatsReport.teleop_gears_avgCycleTime += cycleTime;
                        teamStatsReport.numGearCycles++;

                        if (cycleTime > teamStatsReport.teleop_gears_maxCycleTime)
                            teamStatsReport.teleop_gears_maxCycleTime = cycleTime;

                        if (cycleTime < teamStatsReport.teleop_gears_minCycleTime)
                            teamStatsReport.teleop_gears_minCycleTime = cycleTime;

                        switch (gearDelivevryEvent.lift) {
                            case FEEDER_SIDE:
                                teamStatsReport.teleop_gearsScored_feederSide++;
                                break;
                            case CENTRE:
                                teamStatsReport.teleop_gearsScored_centre++;
                                break;
                            case BOILER_SIDE:
                                teamStatsReport.teleop_gearsScored_boilerSide++;
                                break;
                        }

                        cyclesInThisMatch.cycles.add(currGearCycle.clone());
                    }
                }

                else if (event instanceof DefenseEvent) {
                    // TODO once we have a way to record the end of a defense cycle
                }

                else {
                    // uhh??
                }
            } // for event


            // Climb
            Cycle climbCycle = new Cycle(Cycle.CycleType.CLIMB);
            climbCycle.startTime = 135 - match.postGameObject.climb_time;
            climbCycle.endTime = 135;

            switch (match.postGameObject.climbType) {
                case SUCCESS:
                    teamStatsReport.climbSuccesses++;
                    teamStatsReport.climbAttepmts++;
                    teamStatsReport.climb_avgTime += match.postGameObject.climb_time;
                    climbCycle.success = true;
                    cyclesInThisMatch.cycles.add(climbCycle);
                    break;
                case FAIL:
                    teamStatsReport.climbFailures++;
                    teamStatsReport.climbAttepmts++;
                    teamStatsReport.climb_avgTime += match.postGameObject.climb_time;
                    climbCycle.success = false;
                    cyclesInThisMatch.cycles.add(climbCycle);
                    break;
                case NO_CLIMB:
                    break;
            }


            // Post match gearDeliveryData

            if (! match.postGameObject.notes.equals("") )
                teamStatsReport.notes += "\t\t- " + match.postGameObject.notes + "\n";

            // wrap up any incomplete cycles
            if (inFuelCycle) {

                if (inFuelHighCycle) {
                    Cycle c = currFuelCycle.clone(Cycle.CycleType.HIGH_GOAL);
                    cyclesInThisMatch.cycles.add(c);
                }

                if (inFuelLowCycle) {
                    cyclesInThisMatch.cycles.add(currFuelCycle.clone(Cycle.CycleType.LOW_GOAL));
                }
            }


            if (inGearCycle) {
                currGearCycle.endTime = 135;
                currGearCycle.success = false;
                cyclesInThisMatch.cycles.add(currGearCycle);
            }

            teamStatsReport.avgDeadness += match.postGameObject.time_dead;

            if(match.postGameObject.time_dead > teamStatsReport.highestDeadness)
                teamStatsReport.highestDeadness = match.postGameObject.time_dead;


            teamStatsReport.avgTimeSpentPlayingDef += match.postGameObject.time_defending;

            teamStatsReport.cycleMatches.add(cyclesInThisMatch);

        } // for match


        // avereges and stuff
        int numMatchesPlayed = teamStatsReport.teamMatchData.matches.size();

        if (numMatchesPlayed != 0) {

            // Fuel pickup
            teamStatsReport.teleop_fuelGroundPickups_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_fuelGroundPickups_avgCycleTime /= teamStatsReport.numFuelGroundCycles;
            teamStatsReport.teleop_fuelWallPickups_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_fuelWallPickups_avgCycleTime /= teamStatsReport.numFuelWallCycles;
            teamStatsReport.teleop_fuelHopperPickups_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_fuelHopperPickups_avgCycleTime /= teamStatsReport.numFuelHopperCycles;

            // Fuel scoring
            teamStatsReport.teleop_fuelScoredHigh_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_fuelScoredHigh_avgPerCycle /= teamStatsReport.numFuelHighCycles;
            teamStatsReport.teleop_fuelMissedHigh_avgPerMatch /= numMatchesPlayed;

            teamStatsReport.teleop_fuelScoredLow_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_fuelScoredLow_avgPerCycle /= teamStatsReport.numFuelLowCycles;
            teamStatsReport.teleop_fuelMissedLow_avgPerMatch /= numMatchesPlayed;

            teamStatsReport.teleop_fuelHigh_aveCycleTime /= teamStatsReport.numFuelHighCycles;
            teamStatsReport.teleop_fuelLow_aveCycleTime /= teamStatsReport.numFuelHighCycles;

            // Gears
            teamStatsReport.teleop_gearsDropped_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_gearsDelivered_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_gearsPickupWall_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_gearsPickupGround_avgPerMatch /= numMatchesPlayed;
            teamStatsReport.teleop_gears_avgCycleTime /= teamStatsReport.numGearCycles;

            // figure out which lift they prefer

            // None -- they have never scored a gear.
            if (teamStatsReport.teleop_gearsScored_feederSide == 0 && teamStatsReport.teleop_gearsScored_centre == 0 &&
                    teamStatsReport.teleop_gearsScored_boilerSide == 0)
                teamStatsReport.teleop_gears_preferedLift = GearDelivevryEvent.Lift.NONE;

            // Feeder Side
            if (teamStatsReport.teleop_gearsScored_feederSide > teamStatsReport.teleop_gearsScored_centre &&
                    teamStatsReport.teleop_gearsScored_feederSide > teamStatsReport.teleop_gearsScored_boilerSide)

                teamStatsReport.teleop_gears_preferedLift = GearDelivevryEvent.Lift.FEEDER_SIDE;

            // Centre
            else if (teamStatsReport.teleop_gearsScored_centre > teamStatsReport.teleop_gearsScored_feederSide &&
                    teamStatsReport.teleop_gearsScored_centre > teamStatsReport.teleop_gearsScored_boilerSide)

                teamStatsReport.teleop_gears_preferedLift = GearDelivevryEvent.Lift.CENTRE;

            // Boiler Side
            else
                teamStatsReport.teleop_gears_preferedLift = GearDelivevryEvent.Lift.BOILER_SIDE;


            if (teamStatsReport.climbAttepmts != 0)
                teamStatsReport.climb_avgTime /= teamStatsReport.climbAttepmts;

            teamStatsReport.avgDeadness /= numMatchesPlayed;
            teamStatsReport.avgTimeSpentPlayingDef /= numMatchesPlayed;

            // Find favourite cycle type
            if(teamStatsReport.numGearCycles > teamStatsReport.numFuelHighCycles &&
                    teamStatsReport.numGearCycles > teamStatsReport.numFuelLowCycles) {
                teamStatsReport.favouriteCycleType = "Gears";
            }
            else if(teamStatsReport.numFuelHighCycles > teamStatsReport.numGearCycles &&
                    teamStatsReport.numFuelHighCycles > teamStatsReport.numFuelLowCycles) {
                teamStatsReport.favouriteCycleType = "Fuel (High)";
            }
            else if(teamStatsReport.numFuelLowCycles > teamStatsReport.numGearCycles &&
                    teamStatsReport.numFuelHighCycles > teamStatsReport.numFuelLowCycles) {
                teamStatsReport.favouriteCycleType = "Fuel (Low)";
            }
            else {
                teamStatsReport.favouriteCycleType = "(trying to be PC)";
            }

            teamStatsReport.totalGroundCycles = teamStatsReport.numFuelGroundCycles + teamStatsReport.numGearGroundCycles;
            teamStatsReport.totalWallCycles= teamStatsReport.numFuelWallCycles + teamStatsReport.numGearWallCycles;


            // Find favourite pickup location
            if(teamStatsReport.totalGroundCycles > teamStatsReport.totalWallCycles &&
                    teamStatsReport.totalGroundCycles > teamStatsReport.numFuelHopperCycles) {
                teamStatsReport.favouritePickupLocation = "Ground";
            }
            else if(teamStatsReport.totalWallCycles > teamStatsReport.totalGroundCycles &&
                    teamStatsReport.totalWallCycles > teamStatsReport.numFuelHopperCycles) {
                teamStatsReport.favouritePickupLocation = "Chute";
            }
            else if(teamStatsReport.numFuelHopperCycles > teamStatsReport.totalGroundCycles &&
                    teamStatsReport.numFuelHopperCycles > teamStatsReport.totalWallCycles) {
                teamStatsReport.favouritePickupLocation = "Hopper";
            }
            else {
                teamStatsReport.favouritePickupLocation = "(trying to be PC?)";
            }



        } // end averages

        // Override number of matches from schedule
        if (numMatchesPlayed > teamStatsReport.numMatchesPlayed)
            teamStatsReport.numMatchesPlayed = numMatchesPlayed;
    }

}

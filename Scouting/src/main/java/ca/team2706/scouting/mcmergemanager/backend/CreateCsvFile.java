package ca.team2706.scouting.mcmergemanager.backend;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.gui.MainActivity;
import ca.team2706.scouting.mcmergemanager.powerup2018.StatsEngine;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;

/**
 * Creates a csv file using the data collected from matches
 * TODO: need to make a button for this, and update all the stuff to this year
 */

public class CreateCsvFile {

    private CreateCsvFile() {
        // Private constructor because everything should be static
    }

    private static void writeCell(StringBuilder sb, String o) {
        sb.append(o);
        sb.append(",");
    }
    private static void writeCell(StringBuilder sb, int o) {
        sb.append(o);
        sb.append(",");
    }
    private static void writeCell(StringBuilder sb, double o) {
        sb.append(o);
        sb.append(",");
    }

    private static String createCsvString() {
        // Stats engine stuff
        StatsEngine statsEngine = new StatsEngine(MainActivity.sMatchSchedule);
        JSONArray teamArray = BlueAllianceUtils.getTeamListForEvent();

        StringBuilder sb = new StringBuilder();
        // Add the headercolumns
        writeCell(sb, "TEAM NUMBER");
        writeCell(sb, "OPR");
        writeCell(sb, "DPR");
        writeCell(sb, "CCWM");
        writeCell(sb, "SCHEDULE TOUGHNESS OPR");
        writeCell(sb, "WINS");
        writeCell(sb, "LOSSES");
        writeCell(sb, "TIES");
        writeCell(sb, "AUTO PICKUP GROUND");
        writeCell(sb, "AUTO TOTAL PLACED CUBES");
        writeCell(sb, "AUTO CROSSED LINE");
        writeCell(sb, "AUTO MALFUNCTION");
        writeCell(sb, "TELEOP AVG PORTAL PICKUP");
        writeCell(sb, "TELEOP AVG GROUND PICKUP");
        writeCell(sb, "TELEOP AVG EXCHANGE PICKUP");
        writeCell(sb, "TELEOP AVG SCALE PLACE");
        writeCell(sb, "TELEOP AVG SWITCH PLACE");
        writeCell(sb, "TELEOP AVG EXCHANGE PLACE");
        writeCell(sb, "TELEOP TOTAL FUMBLES");
        writeCell(sb, "TELEOP TOTAL EASY DROP");
        writeCell(sb, "TELEOP TOTAL DROP & LEFT");
        writeCell(sb, "TELEOP MEAN SWITCH CYCLE TIME");
        writeCell(sb, "TELEOP MEAN SCALE CYCLE TIME");
        writeCell(sb, "TELEOP MEAN EXCHANGE CYCLE TIME");
        writeCell(sb, "TELEOP MEAN DROPPED CYCLE TIME");
        writeCell(sb, "TELEOP MEDIAN SWITCH CYCLE TIME");
        writeCell(sb, "TELEOP MEDIAN SCALE CYCLE TIME");
        writeCell(sb, "TELEOP MEDIAN EXCHANGE CYCLE TIME");
        writeCell(sb, "TELEOP MEDIAN DROPPED CYCLE TIME");
        writeCell(sb, "TELEOP LATER SWITCH CYCLE TIME");
        writeCell(sb, "TELEOP LATER SCALE CYCLE TIME");
        writeCell(sb, "TELEOP LATER EXCHANGE CYCLE TIME");
        writeCell(sb, "TELEOP LATER DROPPED CYCLE TIME");
        writeCell(sb, "AVG DEADNESS");
        writeCell(sb, "HIGHEST DEADNESS");
        writeCell(sb, "DEAD MATCHES");
        writeCell(sb, "AVG CLIMB TIME");
        writeCell(sb, "NO CLIMB");
        writeCell(sb, "FAIL CLIMB");
        writeCell(sb, "INDEPENDENT CLIMB");
        writeCell(sb, "ASSISSTED CLIMB");
        writeCell(sb, "ASSISTED OTHER CLIMB");
        writeCell(sb, "ON BASE");
        writeCell(sb, "AVG DEFENDING");
        writeCell(sb, "MAX DEFENDING");
        writeCell(sb, "ALL SWITCH CYCLES");
        writeCell(sb, "ALL SCALE CYCLES");
        writeCell(sb, "ALL EXCHANGE CYCLES");
        writeCell(sb, "ALL DROPPED CYCLES");
        sb.append("\n");

        // Print all the stuff
        for(int i = 0; i < teamArray.length(); i++) {
            try {
                TeamStatsReport teamStatsReport = statsEngine.getTeamStatsReport(Integer.parseInt(((String) teamArray.get(i)).substring(3)), false);

                writeCell(sb, teamStatsReport.teamNumber);
                writeCell(sb, teamStatsReport.OPR);
                writeCell(sb, teamStatsReport.DPR);
                writeCell(sb, teamStatsReport.CCWM);
                writeCell(sb, teamStatsReport.scheduleToughnessByOPR);
                writeCell(sb, teamStatsReport.wins);
                writeCell(sb, teamStatsReport.losses);
                writeCell(sb, teamStatsReport.ties);
                writeCell(sb, teamStatsReport.autoPickupGround);
                writeCell(sb, teamStatsReport.autoTotalPlacedCubes);
                writeCell(sb, teamStatsReport.autoCrossedLine);
                writeCell(sb, teamStatsReport.autoMalfunction);
                writeCell(sb, teamStatsReport.pickupPortalAvgMatch);
                writeCell(sb, teamStatsReport.pickupGroundAvgMatch);
                writeCell(sb, teamStatsReport.pickupExchangeAvgMatch);
                writeCell(sb, teamStatsReport.placeScaleAvgMatch);
                writeCell(sb, teamStatsReport.placeSwitchAvgMatch);
                writeCell(sb, teamStatsReport.placeExchangeAvgMatch);
                writeCell(sb, teamStatsReport.totalFumbles);
                writeCell(sb, teamStatsReport.totalEasyDrop);
                writeCell(sb, teamStatsReport.totalLeftIt);
                writeCell(sb, teamStatsReport.switchAvgCycleTime);
                writeCell(sb, teamStatsReport.scaleAvgCycleTime);
                writeCell(sb, teamStatsReport.exchangeAvgCycleTime);
                writeCell(sb, teamStatsReport.droppedAvgCycleTime);
                writeCell(sb, teamStatsReport.switchMedianCycleTime);
                writeCell(sb, teamStatsReport.scaleMedianCycleTime);
                writeCell(sb, teamStatsReport.exchangeMedianCycleTime);
                writeCell(sb, teamStatsReport.droppedMedianCycleTimes);
                writeCell(sb, teamStatsReport.switchAvgLaterCycleTime);
                writeCell(sb, teamStatsReport.scaleAvgLaterCycleTime);
                writeCell(sb, teamStatsReport.exchangeAvgLaterCycleTime);
                writeCell(sb, teamStatsReport.droppedAvgLaterCycleTime);
                writeCell(sb, teamStatsReport.avgDeadness);
                writeCell(sb, teamStatsReport.highestDeadness);
                writeCell(sb, (teamStatsReport.numMatchesPlayed - teamStatsReport.numMatchesNoDeadness));
                writeCell(sb, teamStatsReport.avgClimbTime);
                writeCell(sb, teamStatsReport.noClimb);
                writeCell(sb, teamStatsReport.failClimb);
                writeCell(sb, teamStatsReport.independentClimb);
                writeCell(sb, teamStatsReport.assistedClimb);
                writeCell(sb, teamStatsReport.assistedOthersClimb);
                writeCell(sb, teamStatsReport.onBase);
                writeCell(sb, teamStatsReport.avgTimeDefending);
                writeCell(sb, teamStatsReport.maxTimeDefending);
                writeCell(sb, arrayToString(teamStatsReport.switchCycleTimes));
                writeCell(sb, arrayToString(teamStatsReport.scaleCycleTimes));
                writeCell(sb, arrayToString(teamStatsReport.exchangeCycleTimes));
                writeCell(sb, arrayToString(teamStatsReport.droppedCycleTimes));

                // New line
                sb.append("\n");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static void saveCsvFile(String filename) {
        double startTime = System.currentTimeMillis();

        // Create the file
        File file = new File(FileUtils.sLocalToplevelFilePath + "/" + filename);

        try {
            (new File(file.getParent())).mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));

            bw.write(createCsvString());

            bw.close();

            // Force the midea scanner to scan this file so it shows up from a PC over USB.
            App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch(IOException e) {
            Log.d("Error saving csv", e.toString());
        }

        System.out.println("Totla time: " + (System.currentTimeMillis() - startTime));
    }

    public static String arrayToString(ArrayList<Double> arr) {
        String s = "";

        for(Double d : arr) {
            s += d + " ";
        }

        return s;
    }
}

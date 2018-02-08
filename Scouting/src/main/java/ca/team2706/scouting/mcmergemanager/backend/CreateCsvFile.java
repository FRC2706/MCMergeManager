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

import ca.team2706.scouting.mcmergemanager.gui.MainActivity;
import ca.team2706.scouting.mcmergemanager.steamworks2017.StatsEngine;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;

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
        StatsEngine statsEngine = new StatsEngine(MainActivity.sMatchData, MainActivity.sMatchSchedule, MainActivity.sRepairTimeObjects);
        JSONArray teamArray = BlueAllianceUtils.getTeamListForEvent();

        StringBuilder sb = new StringBuilder();

        // Add the headercolumns
        writeCell(sb, "TEAM NUMBER");
        writeCell(sb, "OPR");
        writeCell(sb, "DPR");
        writeCell(sb, "SCHEDULE TOUGHNESS");
        sb.append("\n");

        // Print all the stuff
        for(int i = 0; i < teamArray.length(); i++) {
            try {
                TeamStatsReport teamStatsReport = statsEngine.getTeamStatsReport(Integer.parseInt(((String) teamArray.get(i)).substring(3)));

                writeCell(sb, teamStatsReport.teamNo);
                writeCell(sb, teamStatsReport.OPR);
                writeCell(sb, teamStatsReport.DPR);
                writeCell(sb, teamStatsReport.scheduleToughnessByOPR);

                // New line
                sb.append("\n");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static void saveCsvFile(String filename) {
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
    }
}

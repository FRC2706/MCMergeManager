package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.MatchSchedule;

/**
 * Created by alden on 2017-02-04.
 */

public class RepairTimeCollection extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_repair_collection);

        // Unbundle the match schedule
        Intent intent = getIntent();
//        String matchScheduleStr = intent.getStringExtra(getResources().getString(R.string.EXTRA_MATCH_SCHEDULE));
//        MatchSchedule matchSchedule = new MatchSchedule(matchScheduleStr);

        // Get main layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.repair_collection_main_layout);

        // Get list of teams
        //List<String> teams = matchSchedule.getTeamNumsAtEvent();
        
        // TODO Test Code

        ArrayList<String> teams = new ArrayList<>();
        teams.add("2706");
        teams.add("2708");
        teams.add("3453");
        teams.add("5646");

        for(String team: teams) {

            View row = generateRow(team);
            mainLayout.addView(row);

        }

    }

    public View generateRow(String teamNumber) {

        // Create a row
        LayoutInflater inflater = this.getLayoutInflater();
        View curRow = inflater.inflate(R.layout.steamworks2017_repair_collection_row, null);

        // Set the text thing
        TextView teamNumberTV = (TextView) curRow.findViewById(R.id.teamNumber);
        teamNumberTV.setText(teamNumber);

        return curRow;

    }

}

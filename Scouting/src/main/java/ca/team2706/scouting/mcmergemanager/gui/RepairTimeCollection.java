package ca.team2706.scouting.mcmergemanager.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by alden on 2017-02-04.
 */

public class RepairTimeCollection extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_repair_collection);

        // Unbundle the match schedule
        Intent intent = getIntent();
        //String matchScheduleStr = intent.getStringExtra(getResources().getString(R.string.EXTRA_MATCH_SCHEDULE));
        //MatchSchedule matchSchedule = new MatchSchedule(matchScheduleStr);

        // Get main layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.repair_collection_main_layout);

        // Get list of teams
        //List<String> teams = matchSchedule.getTeamNumsAtEvent();

        ArrayList<String> teams = new ArrayList<String>();
        teams.add("3455");
        teams.add("5674");
        teams.add("5464");
        teams.add("6786");

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

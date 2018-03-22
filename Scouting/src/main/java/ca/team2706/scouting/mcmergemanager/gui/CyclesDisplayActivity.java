package ca.team2706.scouting.mcmergemanager.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Cycle;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;


public class CyclesDisplayActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycles_display_activity);


        TeamStatsReport teamStatsReport = (TeamStatsReport) getIntent().getSerializableExtra(getString(R.string.EXTRA_TEAM_STATS_REPORT));
        int teamNo = teamStatsReport.teamNumber;

        ((TextView) findViewById(R.id.cyclesDisplayTitleTv)).setText("Cycles by Team " + teamNo);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.cycles_display_main_layout);

        for(TeamStatsReport.CyclesInAMatch cyclesInAMatch : teamStatsReport.cycleMatches) {

            CycleDisplayLayout cycleDisplayLayout = (CycleDisplayLayout) getLayoutInflater()
                    .inflate(R.layout.cycles_display, null);

            cycleDisplayLayout.setMatchNo(cyclesInAMatch.matchNo);

            for (Cycle cycle : cyclesInAMatch.cycles) {
                cycleDisplayLayout.addCycle(cycle);
            }


            mainLayout.addView(cycleDisplayLayout);

        }
    }

    /**
     * Just a helper function for testing and debugging.
     *
     * I'll leave it here for posterity.
     */
    private void simulateMatchData(CycleDisplayLayout cycleDisplayLayout, int matchNo) {
        // set the match number
        cycleDisplayLayout.setMatchNo(matchNo);

        // simulate some gear cycles
        for(double timer=0; timer<135; ) {
            double startTime = (timer += Math.random()*20);
            double endTime   = (timer += 12.5 + Math.random()*30);
            boolean succeeded = Math.random() < 0.8;

            cycleDisplayLayout.addCycle(CycleDisplayLayout.GEAR_CYCLE, startTime, endTime, succeeded);

            timer += 5 + Math.random()*20;
        }


        // simulate some defense cycles
        int numDefenses = (int) (Math.random()*3);
        double timer = 0;
        for(int j=0; j<numDefenses && timer < 135; j++) {
            double startTime = (timer += 10 + Math.random()*20);
            double endTime   = (timer += 5 + Math.random()*15);
            boolean succeeded = Math.random() < 0.8;

            cycleDisplayLayout.addCycle(CycleDisplayLayout.DEFENSE_CYCLE, startTime, endTime, succeeded);

            timer += 5 + Math.random()*30;
        }


        // simulate a climb?
        if(Math.random() < 0.6) {
            double startTime = 115 - Math.random()*20;

            // succeeded?
            boolean succeeded = Math.random() < 0.7;

            cycleDisplayLayout.addCycle(CycleDisplayLayout.CLIMB_CYCLE, startTime, 135, succeeded);
        }
    }
}

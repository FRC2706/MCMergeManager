package ca.team2706.scouting.mcmergemanager.steamworks2017.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;

public class TeamStatsActivity extends AppCompatActivity {

    TeamStatsReport m_teamStatsReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_team_stats);

        // unbundle the stats gearDeliveryData from the intent
        Intent intent = getIntent();

        try {
            m_teamStatsReport = (TeamStatsReport) intent.getSerializableExtra(getString(R.string.EXTRA_TEAM_STATS_REPORT));
        } catch (Exception e) {
            // maybe the extra wasn't there?
            // Nothing to display then
            m_teamStatsReport = null;
        }

        displayStats();
    }


    private void displayStats() {

        if (m_teamStatsReport == null)
            return;

        /** Title **/
        ((TextView) findViewById(R.id.fullStatsReportTitle)).setText("Stats Report for Team "+m_teamStatsReport.teamNo);



        /** General Info **/

        ((TextView) findViewById(R.id.fullStatsReportRecordTV)).setText( String.format("Record (W/L/T): %d/%d/%d",
                                                                                m_teamStatsReport.wins,
                                                                                m_teamStatsReport.losses,
                                                                                m_teamStatsReport.ties) );

        ((TextView) findViewById(R.id.numMatchesPlayedTV)).setText( String.format("Matches Played: %d", m_teamStatsReport.numMatchesPlayed));

        ((TextView) findViewById(R.id.OPRtv)).setText(String.format("OPR: %.2f", m_teamStatsReport.OPR));
        ((TextView) findViewById(R.id.DPRtv)).setText( String.format("DPR: %.2f", m_teamStatsReport.DPR) );

        if (m_teamStatsReport.scheduleToughnessByWLT < 1.0)
            ((TextView) findViewById(R.id.schedToughnessWltTV)).setText( String.format("Schedule Toughness (by WLT): %.2f (easy)", m_teamStatsReport.scheduleToughnessByWLT) );
        else
            ((TextView) findViewById(R.id.schedToughnessWltTV)).setText( String.format("Schedule Toughness (by WLT): %.2f (hard)", m_teamStatsReport.scheduleToughnessByWLT) );

        if (m_teamStatsReport.scheduleToughnessByOPR < 1.0)
            ((TextView) findViewById(R.id.schedToughnessOprTV)).setText( String.format("Schedule Toughness (by OPR): %.2f (easy)", m_teamStatsReport.scheduleToughnessByOPR) );
        else
            ((TextView) findViewById(R.id.schedToughnessOprTV)).setText( String.format("Schedule Toughness (by OPR): %.2f (hard)", m_teamStatsReport.scheduleToughnessByOPR) );


        CheckBox badManners = (CheckBox) findViewById(R.id.badManners);
        badManners.setChecked(m_teamStatsReport.badManners);

        ((TextView) findViewById(R.id.repairTimeTV)).setText( String.format("Time Spent Repairing: %d%% (%d)", (int) m_teamStatsReport.repair_time_percent, m_teamStatsReport.repairTimeObjects.size()) );



        /** Auto Mode **/


        if (m_teamStatsReport.numMatchesPlayed != 0) {  // protects against divide-by-zero


            ((TextView) findViewById(R.id.autoHighGoalsTV)).setText(String.format("%.2f",
                                                                                m_teamStatsReport.auto_avgNumFuelScoredHigh));

            ((TextView) findViewById(R.id.autoLowGoalsTV)).setText(String.format("%.2f",
                                                                                m_teamStatsReport.auto_avgNumFuelScoredLow));


            ((TextView) findViewById(R.id.crossedBaseLineTV)).setText(String.format("%d / %d (%.2f)",
                                                                                m_teamStatsReport.auto_numTimesCrossedBaseline,
                                                                                m_teamStatsReport.numMatchesPlayed,
                                                                                ((double) m_teamStatsReport.auto_numTimesCrossedBaseline) / m_teamStatsReport.numMatchesPlayed));

            ((TextView) findViewById(R.id.installedGearTV)).setText(String.format("%d / %d (%.2f)",
                                                                                m_teamStatsReport.auto_numGearsDelivered,
                                                                                m_teamStatsReport.numMatchesPlayed,
                                                                                ((double) m_teamStatsReport.auto_numGearsDelivered) / m_teamStatsReport.numMatchesPlayed));

        }


        /** Teleop Mode **/


        if (m_teamStatsReport.numMatchesPlayed != 0) {  // protects against divide-by-zero


            ((TextView) findViewById(R.id.teleopHighGoalsTV)).setText(String.format("%.2f (%.2f)",
                                                                            m_teamStatsReport.teleop_fuelScoredHigh_avgPerCycle,
                                                                            m_teamStatsReport.teleop_fuelScoredHigh_avgPerMatch));


            ((TextView) findViewById(R.id.teleopHighSetupTimeTV)).setText(String.format("\t%.1f s",
                                                                            m_teamStatsReport.teleop_fuelHigh_aveCycleTime) );


            ((TextView) findViewById(R.id.teleopLowGoalsTV)).setText(String.format("%.2f / (%.2f)",
                                                                            m_teamStatsReport.teleop_fuelScoredLow_avgPerCycle,
                                                                            m_teamStatsReport.teleop_fuelScoredLow_avgPerMatch));


            ((TextView) findViewById(R.id.teleopLowSetupTimeTV)).setText(String.format("\t%.1f s",
                                                                            m_teamStatsReport.teleop_fuelLow_aveCycleTime) );



            ((TextView) findViewById(R.id.highMissedShots)).setText(String.format("%.2f",
                                                                            m_teamStatsReport.teleop_fuelMissedHigh_avgPerMatch));

            ((TextView) findViewById(R.id.lowMissedShots)).setText(String.format("%.2f",
                                                                            m_teamStatsReport.teleop_fuelMissedLow_avgPerMatch));


            ((TextView) findViewById(R.id.hopperPickupsTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_fuelHopperPickups_avgPerMatch));

            ((TextView) findViewById(R.id.chutePickupsTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_fuelWallPickups_avgPerMatch));

            ((TextView) findViewById(R.id.teleopGroundPickupsTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_fuelGroundPickups_avgPerMatch));

            ((TextView) findViewById(R.id.teleopTimePlayingDTV)).setText(String.format("\t%.1f s",
                                                                            m_teamStatsReport.avgTimeSpentPlayingDef) );

            ((TextView) findViewById(R.id.avgDeadnessTV)).setText(String.format("%.2f s",
                                                                            m_teamStatsReport.avgDeadness) );

            ((TextView) findViewById(R.id.highestDeadnessTV)).setText(String.format("%.2f s",
                                                                            m_teamStatsReport.highestDeadness) );

            ((TextView) findViewById(R.id.numDeadnessMatchesTV)).setText(String.format("%d",
                    m_teamStatsReport.numMatchesNoDeadness) );

            ((TextView) findViewById(R.id.climbsTV)).setText(String.format("%d / %d (%.2f)",
                                                                            m_teamStatsReport.climbSuccesses,
                                                                            m_teamStatsReport.climbAttepmts,
                                                                            ((double) m_teamStatsReport.climbSuccesses) / m_teamStatsReport.climbAttepmts));

            ((TextView) findViewById(R.id.climbTimeTV)).setText(String.format("\t%.1f s",
                                                                            m_teamStatsReport.climb_avgTime) );

            ((TextView) findViewById(R.id.minClimbTimeTV)).setText(String.format("\t%.1f s",
                    m_teamStatsReport.climb_minTime) );

            ((TextView) findViewById(R.id.failedClimbsTV)).setText(String.format("%d / %d (%.2f)",
                                                                            m_teamStatsReport.climbFailures,
                                                                            m_teamStatsReport.climbAttepmts,
                                                                            ((double) m_teamStatsReport.climbFailures) / m_teamStatsReport.climbAttepmts));


        }

        /** Gears Stuff **/
        if(m_teamStatsReport.numMatchesPlayed != 0) { // protects against divide by zero exception

            ((TextView) findViewById(R.id.teleopMissedGearsTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_gearsDropped_avgPerMatch));

            ((TextView) findViewById(R.id.gearsInstalled)).setText(String.format("%.2f",
                                                                            m_teamStatsReport.teleop_gearsDelivered_avgPerMatch));

            ((TextView) findViewById(R.id.teleopGearsChutePickupTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_gearsPickupWall_avgPerMatch));

            ((TextView) findViewById(R.id.teleopGearsFloorPickupTV)).setText(String.format("%.2f",
                    m_teamStatsReport.teleop_gearsPickupGround_avgPerMatch));


        }

    }
}

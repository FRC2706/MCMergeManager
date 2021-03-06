package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;

public class TeamStatsActivity extends AppCompatActivity {

    TeamStatsReport m_teamStatsReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_team_stats);

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
        ((TextView) findViewById(R.id.fullStatsReportTitle)).setText("Stats Report for Team " + m_teamStatsReport.teamNumber);


        /** General Info **/

        ((TextView) findViewById(R.id.fullStatsReportRecordTV)).setText(String.format("Record (W/L/T): %d/%d/%d",
                m_teamStatsReport.wins,
                m_teamStatsReport.losses,
                m_teamStatsReport.ties));

        ((TextView) findViewById(R.id.numMatchesPlayedTV)).setText(String.format("Matches Played: %d", m_teamStatsReport.numMatchesPlayed));

        ((TextView) findViewById(R.id.OPRtv)).setText(String.format("OPR: %.2f", m_teamStatsReport.OPR));
        ((TextView) findViewById(R.id.DPRtv)).setText(String.format("DPR: %.2f", m_teamStatsReport.DPR));

        if (m_teamStatsReport.scheduleToughnessByWLT < 1.0)
            ((TextView) findViewById(R.id.schedToughnessWltTV)).setText(String.format("Schedule Toughness (by WLT): %.2f (easy)", m_teamStatsReport.scheduleToughnessByWLT));
        else
            ((TextView) findViewById(R.id.schedToughnessWltTV)).setText(String.format("Schedule Toughness (by WLT): %.2f (hard)", m_teamStatsReport.scheduleToughnessByWLT));

        if (m_teamStatsReport.scheduleToughnessByOPR < 1.0)
            ((TextView) findViewById(R.id.schedToughnessOprTV)).setText(String.format("Schedule Toughness (by OPR): %.2f (easy)", m_teamStatsReport.scheduleToughnessByOPR));
        else
            ((TextView) findViewById(R.id.schedToughnessOprTV)).setText(String.format("Schedule Toughness (by OPR): %.2f (hard)", m_teamStatsReport.scheduleToughnessByOPR));


        CheckBox badManners = (CheckBox) findViewById(R.id.badManners);
        badManners.setChecked(m_teamStatsReport.badManners);

//        TODO: ((TextView) findViewById(R.id.repairTimeTV)).setText( String.format("Time Spent Repairing: %d%% (%d)", (int) m_teamStatsReport.repair_time_percent, m_teamStatsReport.repairTimeObjects.size()) );


        /** Auto Mode **/
        ((TextView) findViewById(R.id.crossedBaseLineTV)).setText(m_teamStatsReport.autoCrossedLine
                + " / " + m_teamStatsReport.numMatchesPlayed);

        ((TextView) findViewById(R.id.autoCubesTV)).setText(Integer.toString(m_teamStatsReport.autoTotalPlacedCubes));

        ((TextView) findViewById(R.id.autoPickupGroundTV)).setText(Integer.toString(m_teamStatsReport.autoPickupGround));

        ((TextView) findViewById(R.id.autoPlacedAllianceSwitchTV)).setText(Integer.toString(m_teamStatsReport.autoPlaceAllianceSwitch));
        ((TextView) findViewById(R.id.autoPlacedExchangeTV)).setText(Integer.toString(m_teamStatsReport.autoPlaceExchange));
        ((TextView) findViewById(R.id.autoPlacedScaleTV)).setText(Integer.toString(m_teamStatsReport.autoPlaceScale));
        ((TextView) findViewById(R.id.autoPlacedExchangeTV)).setText(Integer.toString(m_teamStatsReport.autoPlaceExchange));

        ((TextView) findViewById(R.id.autoDroppedTV)).setText(Integer.toString(m_teamStatsReport.autoPlaceDropped));

        ((TextView) findViewById(R.id.autoMalfunctionsTV)).setText(m_teamStatsReport.autoMalfunction
                + " / " + m_teamStatsReport.numMatchesPlayed);


        /** Teleop Mode **/
        ((TextView) findViewById(R.id.telePickupGroundTV)).setText(String.format("%.2f", m_teamStatsReport.pickupGroundAvgMatch));
        ((TextView) findViewById(R.id.telePickupPortalTV)).setText(String.format("%.2f", m_teamStatsReport.pickupPortalAvgMatch));
        ((TextView) findViewById(R.id.telePickupExchangeTV)).setText(String.format("%.2f", m_teamStatsReport.pickupExchangeAvgMatch));
        ((TextView) findViewById(R.id.telePickupPyramidTV)).setText(String.format("%.2f", m_teamStatsReport.pickupPyramidAvgMatch));

        ((TextView) findViewById(R.id.telePlacedExchangeTV)).setText(String.format("%.2f", m_teamStatsReport.placeExchangeAvgMatch));
        ((TextView) findViewById(R.id.telePlacedSwitchTV)).setText(String.format("%.2f", m_teamStatsReport.placeSwitchAvgMatch));
        ((TextView) findViewById(R.id.telePlacedScaleTV)).setText(String.format("%.2f", m_teamStatsReport.placeScaleAvgMatch));

        ((TextView) findViewById(R.id.teleEasyDropTV)).setText(Integer.toString(m_teamStatsReport.totalEasyDrop));
        ((TextView) findViewById(R.id.teleLeftItTV)).setText(Integer.toString(m_teamStatsReport.totalLeftIt));
        ((TextView) findViewById(R.id.teleFumbleTV)).setText(Integer.toString(m_teamStatsReport.totalFumbles));

        ((TextView) findViewById(R.id.avgSwitchPlaceTimeTV)).setText(String.format("%.2f", m_teamStatsReport.switchAvgCycleTime) + "s");
        ((TextView) findViewById(R.id.avgScalePlaceTimeTV)).setText(String.format("%.2f", m_teamStatsReport.scaleAvgCycleTime) + "s");
        ((TextView) findViewById(R.id.avgExchangePlaceTimeTV)).setText(String.format("%.2f", m_teamStatsReport.exchangeAvgCycleTime) + "s");

        ((TextView) findViewById(R.id.favouritePickupTV)).setText(m_teamStatsReport.favouritePickup.toString());
        ((TextView) findViewById(R.id.favouritePlacementTV)).setText(m_teamStatsReport.favouritePlacement.toString());


        // Climbing stuff
        ((TextView) findViewById(R.id.avgClimbTimeTV)).setText(String.format("%.2f", m_teamStatsReport.avgClimbTime) + "s");
        ((TextView) findViewById(R.id.minClimbTimeTV)).setText(String.format("%.2f", m_teamStatsReport.minClimbTime) + "s");
        ((TextView) findViewById(R.id.maxClimbTimeTV)).setText(String.format("%.2f", m_teamStatsReport.maxClimbTime) + "s");

        ((TextView) findViewById(R.id.noClimbTV)).setText(Integer.toString(m_teamStatsReport.noClimb));
        ((TextView) findViewById(R.id.failClimbTV)).setText(Integer.toString(m_teamStatsReport.failClimb));
        ((TextView) findViewById(R.id.independentClimbTV)).setText(Integer.toString(m_teamStatsReport.independentClimb));
        ((TextView) findViewById(R.id.assistedClimbTV)).setText(Integer.toString(m_teamStatsReport.assistedClimb));
        ((TextView) findViewById(R.id.assistedOthersClimbTV)).setText(Integer.toString(m_teamStatsReport.assistedOthersClimb));
        ((TextView) findViewById(R.id.onBaseClimbTV)).setText(Integer.toString(m_teamStatsReport.onBase));

        // Post game
        ((TextView) findViewById(R.id.avgTimeDefendingTV)).setText(String.format("%.2f", m_teamStatsReport.avgTimeDefending) + "s");
        ((TextView) findViewById(R.id.maxTimeDefendingTV)).setText(String.format("%.2f", m_teamStatsReport.maxTimeDefending) + "s");

        ((TextView) findViewById(R.id.avgDeadnessTV)).setText(String.format("%.2f", m_teamStatsReport.avgDeadness) + "s");
        ((TextView) findViewById(R.id.maxDeadnessTV)).setText(String.format("%.2f", m_teamStatsReport.highestDeadness) + "s");

        // Field Watcher Stuff
        ((TextView) findViewById(R.id.avgAllianceSwitchPossTV)).setText(String.format("%.2f", m_teamStatsReport.avgAllianceSwitchPossessionPerMatch) + "s");
        ((TextView) findViewById(R.id.avgOpposingSwitchPossTV)).setText(String.format("%.2f", m_teamStatsReport.avgOpposingSwitchPossessionPerMatch) + "s");
        ((TextView) findViewById(R.id.avgScalePossTV)).setText(String.format("%.2f", m_teamStatsReport.avgScalePossessionPerMatch) + "s");

        ((TextView) findViewById(R.id.forceUsesTV)).setText(Integer.toString(m_teamStatsReport.force));
        ((TextView) findViewById(R.id.boostUsesTV)).setText(Integer.toString(m_teamStatsReport.boost));
        ((TextView) findViewById(R.id.levitateUsesTV)).setText(Integer.toString(m_teamStatsReport.levitate));
    }
}

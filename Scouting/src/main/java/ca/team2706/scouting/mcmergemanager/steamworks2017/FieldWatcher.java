package ca.team2706.scouting.mcmergemanager.steamworks2017;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
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


public class FieldWatcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_field_watcher);


    }
}
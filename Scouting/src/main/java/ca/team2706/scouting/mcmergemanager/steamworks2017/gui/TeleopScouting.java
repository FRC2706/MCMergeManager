package ca.team2706.scouting.mcmergemanager.steamworks2017.gui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelShotEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearDelivevryEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeleopScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.BallPickupFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.BallShootingFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.ClimbingFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.FragmentListener;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.GearDeliveryFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.GearPickupFragment;

public class TeleopScouting extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_teleop_scouting);

    }
}




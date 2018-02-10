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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.DroppedCubeFragment;
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

public class TeleopScouting extends AppCompatActivity implements FragmentListener{

    @Override
    public void editNameDialogComplete(DialogFragment dialogFragment, Bundle data){

    }

    @Override
    public void editNameDialogCancel(DialogFragment dialogFragment){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_teleop_scouting);

        final Spinner cubeDeliverySpinner = (Spinner) findViewById(R.id.cube_delivery_spinner);

        //TeleopScoutingObject teleopScouting = new TeleopScouting();



        cubeDeliverySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String text = (String) cubeDeliverySpinner.getItemAtPosition(position);

                        switch (position){

                            case 1:


                            case 3:
                                showCubeDropped();
                                break;
                        }
                    }
                    public  void onNothingSelected(AdapterView<?> parent){}
                }

        );
        View view = cubeDeliverySpinner.getChildAt(3);

    }

    private void showCubeDropped(){
        FragmentManager fm = getFragmentManager();
        DroppedCubeFragment droppedCubeFragment = DroppedCubeFragment.newInstance("subscribe", this);
        droppedCubeFragment.show(fm, "fragment_dropped_cube");

    }

    public void startedClimbing(View view){


    }

}




package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

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
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
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


    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;
    private int remainTime = 135;
    public int ballsHeld;
    public boolean gearHeld = false;
    public boolean gearDropped = false;
    public String ballsHeldString;
    public Event event = new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_teleop_scouting);

        final Spinner cubeDeliverySpinner = (Spinner) findViewById(R.id.cube_delivery_spinner);

        TeleopScoutingObject teleopScouting = new TeleopScoutingObject();

        final Event event;

        final TextView tvGameTime = (TextView) findViewById(R.id.match_timer_textView);



        m_handler = new Handler();

        m_handlerTask = new Runnable() {
            @Override
            public void run() {

                if (remainTime == 0) {
                    tvGameTime.setText("Game Over! Please Select Climb Type.");
                } else {
                    remainTime--;
                    int minuets = remainTime / 60;
                    int remainSec = remainTime - minuets * 60;
                    String remainSecString;
                    if (remainSec < 10)
                        remainSecString = "0" + remainSec;
                    else
                        remainSecString = remainSec + "";
// woo
                    tvGameTime.setText(minuets + ":" + remainSecString);

                    // set an alarm to run this again in 1 second
                    if (!stopTimer)
                        m_handler.postDelayed(m_handlerTask, 1000);  // 1 second delay
                }
            }
        };
        m_handlerTask.run();



        cubeDeliverySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String text = (String) cubeDeliverySpinner.getItemAtPosition(position);

                        switch (position){

                            case 1:
                                // event = new CubePlacementEvent();


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




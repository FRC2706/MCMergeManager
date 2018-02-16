package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.powerup2018.DroppedCubeFragment;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelShotEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearDelivevryEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.BallPickupFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.BallShootingFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.ClimbingFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.FragmentListener;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.GearDeliveryFragment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.GearPickupFragment;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;

import android.view.View.OnKeyListener;

public class TeleopScouting extends AppCompatActivity implements FragmentListener{

    public static int teamNum = -1;

    private View v;

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

    ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject teleopScoutingObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_teleop_scouting);

        teleopScoutingObject = new TeleopScoutingObject();


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



        Event event;

        //Recording data for delivery
        final Spinner cubeDeliverySpinner = (Spinner) findViewById(R.id.cube_delivery_spinner);

        cubeDeliverySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String text = (String) cubeDeliverySpinner.getItemAtPosition(position);

                        CubePlacementEvent cubePlacementEvent;

                        switch (position){

                            case 1:
                                cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.placementType.ALLIANCE_SWITCH);
                                teleopScoutingObject.add(cubePlacementEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 2:
                                cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.placementType.SCALE);
                                teleopScoutingObject.add(cubePlacementEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 3:
                                cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.placementType.EXCHANGE);
                                teleopScoutingObject.add(cubePlacementEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 4:
                                cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.placementType.DROPPED);
                                teleopScoutingObject.add(cubePlacementEvent);

                                //show the cube dropped popup
                                showCubeDropped();

                                cubeDeliverySpinner.setSelection(0);
                                break;
                        }
                    }
                    public  void onNothingSelected(AdapterView<?> parent){}
                }

        );


        //Recording data for pickup
        final Spinner cubePickupSpinner = (Spinner) findViewById(R.id.cube_pickup_spinner);

        cubePickupSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String text = (String) cubePickupSpinner.getItemAtPosition(position);

                        CubePickupEvent cubePickupEvent;

                        switch (position){

                            case 0:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.pickupType.PYRAMID);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 1:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.pickupType.PORTAL);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 2:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.pickupType.EXCHANGE);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;

                            case 3:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.pickupType.GROUND);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubeDeliverySpinner.setSelection(0);
                                break;
                        }
                    }
                    public  void onNothingSelected(AdapterView<?> parent){}
                }

        );

        View view = cubeDeliverySpinner.getChildAt(3);



        final EditText teamNumber = (EditText) findViewById(R.id.teamNumber);

        final EditText comment = (EditText) findViewById(R.id.comment);



        teamNumber.setOnKeyListener(new OnKeyListener(){

            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                teamNum = CommentListener.getTeamNum(keyCode, keyevent, teamNumber, comment);
                if (teamNum == -1) {
                    return false;
                }
                return true;
            }
        });

        comment.setOnKeyListener(new OnKeyListener(){
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                CommentListener.saveComment(keyCode, keyevent, comment, teamNum, teamNumber, v, getContext());
                return true;
            }
        });


    }

    private void showCubeDropped(){
        FragmentManager fm = getFragmentManager();
        DroppedCubeFragment droppedCubeFragment = DroppedCubeFragment.newInstance("subscribe", this);
        droppedCubeFragment.show(fm, "fragment_dropped_cube");

    }

    public void startedClimbing(View view){

        Intent intent = new Intent(this, Postgame.class);
        startActivity(intent);

    }




}




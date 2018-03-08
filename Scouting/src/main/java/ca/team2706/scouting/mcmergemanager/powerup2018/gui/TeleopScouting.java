package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.powerup2018.DroppedCubeFragment;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubeDroppedEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.FragmentListener;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;

import android.view.View.OnKeyListener;
import android.widget.Toast;

import java.io.Serializable;

public class TeleopScouting extends AppCompatActivity implements FragmentListener{

    public static int teamNum = -1;
    volatile boolean cubeHolding;

    @Override
    public void editNameDialogComplete(DialogFragment dialogFragment, Bundle data){

        if(dialogFragment instanceof DroppedCubeFragment) {
            CubeDroppedEvent cubeDroppedEvent = (CubeDroppedEvent) data.getSerializable("dropped_cube");
            cubeDroppedEvent.timestamp = event.timestamp;
            teleopScoutingObject.add(cubeDroppedEvent);
        }

    }

    @Override
    public void editNameDialogCancel(DialogFragment dialogFragment){
        dialogFragment.dismiss();
    }


    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;
    private int remainTime = 135;

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

        if(cubeHolding) {
            cubeDeliverySpinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            String text = (String) cubeDeliverySpinner.getItemAtPosition(position);

                            CubePlacementEvent cubePlacementEvent;

                            CubeDroppedEvent cubeDroppedEvent;

                            switch (position) {

                                case 1:
                                    cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.PlacementType.ALLIANCE_SWITCH);
                                    teleopScoutingObject.add(cubePlacementEvent);
                                    cubeDeliverySpinner.setSelection(0);
                                    break;

                                case 2:
                                    cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.PlacementType.SCALE);
                                    teleopScoutingObject.add(cubePlacementEvent);
                                    cubeDeliverySpinner.setSelection(0);
                                    break;

                                case 3:
                                    cubePlacementEvent = new CubePlacementEvent(135 - remainTime, CubePlacementEvent.PlacementType.EXCHANGE);
                                    teleopScoutingObject.add(cubePlacementEvent);
                                    cubeDeliverySpinner.setSelection(0);
                                    break;

                                case 4:
                                    //show the cube dropped popup
                                    showCubeDropped();

                                    cubeDeliverySpinner.setSelection(0);
                                    break;
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    }

            );

        }else{
            Toast.makeText(this, "You are not holding a cube.", Toast.LENGTH_SHORT).show();
        }

        //Recording data for pickup
        final Spinner cubePickupSpinner = (Spinner) findViewById(R.id.cube_pickup_spinner);

        cubePickupSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String text = (String) cubePickupSpinner.getItemAtPosition(position);

                        CubePickupEvent cubePickupEvent;

                        switch (position){

                            case 1:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.PickupType.PYRAMID);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubePickupSpinner.setSelection(0);
                                cubeHolding = true;
                                break;

                            case 2:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.PickupType.PORTAL);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubePickupSpinner.setSelection(0);
                                cubeHolding = true;
                                break;

                            case 3:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.PickupType.EXCHANGE);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubePickupSpinner.setSelection(0);
                                cubeHolding = true;
                                break;

                            case 4:
                                cubePickupEvent = new CubePickupEvent(135 - remainTime, CubePickupEvent.PickupType.GROUND);
                                teleopScoutingObject.add(cubePickupEvent);
                                cubePickupSpinner.setSelection(0);
                                cubeHolding = true;
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


        PreGameObject preGameObject = (PreGameObject) getIntent().getSerializableExtra("PreGameData");
        final Integer teamNum = preGameObject.teamNumber;

        comment.setOnKeyListener(new OnKeyListener(){
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                CommentListener.saveComment(keyCode, keyevent, comment, teamNum, teamNumber, view, getContext());
                teamNumber.setText(teamNum.toString());
                return true;
            }
        });


        teamNumber.setText(teamNum.toString());

      //  int i = preGameObject.teamNumber;


    }


    Event event = new Event();

    private void showCubeDropped(){
        FragmentManager fm = getFragmentManager();
        DroppedCubeFragment droppedCubeFragment = DroppedCubeFragment.newInstance("subscribe", this);
        droppedCubeFragment.show(fm, "fragment_dropped_cube");
        event.timestamp = 135 - remainTime;

    }



    public void startedClimbing(View view){
        Intent intent = new Intent(this, PostGame.class);
        intent.putExtra("PreGameData", getIntent().getSerializableExtra("PreGameData"));
        intent.putExtra("AutoScoutingData", getIntent().getSerializableExtra("AutoScoutingData"));
        intent.putExtra("TeleopScoutingData", teleopScoutingObject);
        startActivity(intent);

    }

}




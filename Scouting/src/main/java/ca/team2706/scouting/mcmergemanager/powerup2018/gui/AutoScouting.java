package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoLineCrossEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoMalfunctionEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;


public class AutoScouting extends AppCompatActivity {

    private AutoScoutingObject autoScoutingObject2018;
    public int pointsScored;

    SeekBar simpleSeekBar;


    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;
    private int remainTime = 15;
    public Event event = new Event();
    public static int teamNum = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_scouting);
        // initiate  views

        autoScoutingObject2018 = new AutoScoutingObject();

       // TODO Add the code for comment_bar

        m_handler = new Handler();

        final TextView tvGameTime = (TextView) findViewById(R.id.autoTimer);

        m_handler = new Handler();

        m_handlerTask = new Runnable() {
            @Override
            public void run() {

                if (remainTime == 0) {
                    tvGameTime.setText("Auto Over! Please Go To Teleop.");
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


        final EditText teamNumber = (EditText) findViewById(R.id.teamNumber);
        final EditText comment = (EditText) findViewById(R.id.comment);

        teamNumber.setOnKeyListener(new View.OnKeyListener(){

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

        comment.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                CommentListener.saveComment(keyCode, keyevent, comment, teamNum, teamNumber, view, getContext());
                teamNumber.setText(teamNum.toString());
                return true;
            }
        });


        teamNumber.setText(teamNum.toString());


        m_handlerTask.run();

        final CheckBox checkBox = (CheckBox) findViewById(R.id.baselineCheckbox);
        checkBox.setChecked(false);
        final Event event;
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {

                    AutoLineCrossEvent autoLineCrossEvent = new AutoLineCrossEvent(15 - remainTime, true);
                    autoScoutingObject2018.add(autoLineCrossEvent);
                    checkBox.setChecked(true);

                } else {
                    checkBox.setChecked(false);
                }
            }
        });
        

        final CheckBox malfunctioncheckbox = (CheckBox) findViewById(R.id.autoMalfunctionCheckbox);
        malfunctioncheckbox.setChecked(false);
        final Event mevent;
        malfunctioncheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (malfunctioncheckbox.isChecked()) {
                    AutoMalfunctionEvent autoMalfunctionEvent = new AutoMalfunctionEvent(15 - remainTime, true);
                    autoScoutingObject2018.add(autoMalfunctionEvent);
                    malfunctioncheckbox.setChecked(true);
                } else

                {
                    malfunctioncheckbox.setChecked(false);
                }
            }
        });

        final CheckBox groundCheckbox = (CheckBox) findViewById(R.id.autoGroundCheckbox);
        groundCheckbox.setChecked(false);
        final Event cevent;
        groundCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groundCheckbox.isChecked()) {

                    AutoCubePickupEvent autoCubePickupEvent = new AutoCubePickupEvent(15 - remainTime, AutoCubePickupEvent.PickupType.GROUND);
                    autoScoutingObject2018.add(autoCubePickupEvent);
                    groundCheckbox.setChecked(true);

                } else {
                    groundCheckbox.setChecked(false);
                }

                final CheckBox switchCheckbox = (CheckBox) findViewById(R.id.autoSwitchCheckbox);
                switchCheckbox.setChecked(false);
                final Event swevent;
                switchCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (switchCheckbox.isChecked()) {

                            AutoCubePlacementEvent autoCubePlacementEvent = new AutoCubePlacementEvent(15 - remainTime, AutoCubePlacementEvent.PlacementType.ALLIANCE_SWITCH);
                            autoScoutingObject2018.add(autoCubePlacementEvent);
                            switchCheckbox.setChecked(true);
                        } else {
                            switchCheckbox.setChecked(false);
                        }

                    }
                });

                final CheckBox scaleCheckbox = (CheckBox) findViewById(R.id.autoScaleCheckbox);
                scaleCheckbox.setChecked(false);
                final Event scevent;
                scaleCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){

                        if (scaleCheckbox.isChecked()) {

                            AutoCubePlacementEvent autoCubePlacementEvent = new AutoCubePlacementEvent(15 - remainTime, AutoCubePlacementEvent.PlacementType.SCALE);
                            autoScoutingObject2018.add(autoCubePlacementEvent);
                            scaleCheckbox.setChecked(true);
                        } else {
                            scaleCheckbox.setChecked(false);
                        }

                    }
                });

                final CheckBox exchangeCheckbox = (CheckBox) findViewById(R.id.autoExchangeCheckbox);
                exchangeCheckbox.setChecked(false);
                final Event eevent;
                exchangeCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (exchangeCheckbox.isChecked()) {

                            AutoCubePlacementEvent autoCubePlacementEvent = new AutoCubePlacementEvent(15 - remainTime, AutoCubePlacementEvent.PlacementType.EXCHANGE);
                            autoScoutingObject2018.add(autoCubePlacementEvent);
                            exchangeCheckbox.setChecked(true);
                        } else {
                            exchangeCheckbox.setChecked(false);
                        }
                    }
                });

                final CheckBox droppedCheckbox = (CheckBox) findViewById(R.id.autoDroppedCheckbox);
                droppedCheckbox.setChecked(false);
                final Event devent;
                droppedCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (droppedCheckbox.isChecked()) {

                            AutoCubePlacementEvent autoCubePlacementEvent = new AutoCubePlacementEvent(15 - remainTime, AutoCubePlacementEvent.PlacementType.DROPPED);
                            autoScoutingObject2018.add(autoCubePlacementEvent);
                            droppedCheckbox.setChecked(true);
                        } else {
                            droppedCheckbox.setChecked(false);
                        }
                    }
                });


            }


        });



    }


    public void toTeleop(View view) {
        Intent intent = new Intent(this, TeleopScouting.class);
        intent.putExtra("PreGameData", getIntent().getSerializableExtra("PreGameData"));
        intent.putExtra("AutoScoutingData", autoScoutingObject2018);
        startActivity(intent);
    }
}




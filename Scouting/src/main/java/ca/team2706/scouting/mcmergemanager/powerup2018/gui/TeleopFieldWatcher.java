package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.gui.PreGameActivity;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BlueSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ForceEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.LevitateEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BoostEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.RedSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;

/**
 * Created by LaraLim on 2018-02-24.
 */

public class TeleopFieldWatcher extends AppCompatActivity{

    BlueSwitchEvent blueSwitchEvent = new BlueSwitchEvent();
    ForceEvent forceEvent = new ForceEvent();
    LevitateEvent levitateEvent = new LevitateEvent();
    BoostEvent boostEvent = new BoostEvent();
    ScaleEvent scaleEvent = new ScaleEvent();

    public static int teamNum = -1;
    int remainTime = 135;
    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;



    FieldWatcherObject fieldWatcherObject;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent thisIntent = getIntent();
        fieldWatcherObject = (FieldWatcherObject) thisIntent.getSerializableExtra("FieldWatcherObject");

        setContentView(R.layout.powerup2018_activity_teleop_field_watcher);

        final TextView tvGameTime = (TextView) findViewById(R.id.teleop_fieldwatcher_timer);



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



    //Blue alliance switch
        final Button blueSwitchBluebutton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_b);
        blueSwitchBluebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);

            }
        });

        final Button blueSwitchNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_n);
        blueSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });

        final Button blueSwitchRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_r);
        blueSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });

    //Red alliance switch
        final Button redSwitchBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_b);
        redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);
            }
        });

        final Button redSwitchNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_n);
        redSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });

        final Button redSwitchRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_r);
        redSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });


    //Force
        final Button redForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_r);
        redForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new ForceEvent(135 - remainTime, ForceEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);

                redForceButton.setVisibility(View.INVISIBLE);
            }
        });

        final Button blueForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_b);
        blueForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new ForceEvent(135 - remainTime, ForceEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);

                blueForceButton.setVisibility(View.INVISIBLE);
            }
        });

    //Levitate
        final Button redLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_r);
        redLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);

                redLevitateButton.setVisibility(View.INVISIBLE);
            }
        });

        final Button blueLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_b);
        blueLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);

                blueLevitateButton.setVisibility(View.INVISIBLE);
            }
        });

    //Boost
        final Button redBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_r);
        redBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new BoostEvent(135 - remainTime, BoostEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);

                redBoostButton.setVisibility(View.INVISIBLE);
            }
        });

        final Button blueBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_b);
        blueBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);

                blueBoostButton.setVisibility(View.INVISIBLE);
            }
        });


    //Scale
        final Button scaleBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_b);
        scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);
            }
        });

        final Button scaleNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_n);
        scaleNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });

        final Button scaleRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_r);
        scaleRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });

        //Comment bar
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


        comment.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                CommentListener.saveComment(keyCode, keyevent, comment, teamNum, teamNumber, view, getContext());
                return true;
            }
        });

    }

    public void returnHome(View view){
        Intent thisIntent = getIntent();
        PreGameObject preGameObject = (PreGameObject) thisIntent.getSerializableExtra("PreGameObject");
        int i = preGameObject.teamNumber;
        FieldWatcherObject fieldWatcherObject1 = fieldWatcherObject;


        MatchData.Match match = new MatchData.Match(preGameObject, fieldWatcherObject);

        try {
            match.toJson();
        }catch(JSONException e) {
            Log.d("JSON error", e.getMessage());
        }



        Intent intent = new Intent(this, PreGameFieldWatcher.class);
        startActivity(intent);
    }
}

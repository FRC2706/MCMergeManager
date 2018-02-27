package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.SwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ForceEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.LevitateEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BoostEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.FieldWatcher;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;

/**
 * Created by LaraLim on 2018-02-24.
 */

public class TeleopFieldWatcher extends AppCompatActivity{

    SwitchEvent switchEvent = new SwitchEvent();
    ForceEvent forceEvent = new ForceEvent();
    LevitateEvent levitateEvent = new LevitateEvent();
    BoostEvent boostEvent = new BoostEvent();
    ScaleEvent scaleEvent = new ScaleEvent();

    public static int teamNum = -1;
    int remainTime = 135;
    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                switchEvent = new SwitchEvent(135 - remainTime, SwitchEvent.AllianceColour.BLUE);

            }
        });

        final Button blueSwitchNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_n);
        blueSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button blueSwitchRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_r);
        blueSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchEvent = new SwitchEvent(135 - remainTime, SwitchEvent.AllianceColour.RED);
            }
        });

    //Red alliance switch
        final Button redSwitchBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_b);
        redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchEvent = new SwitchEvent(135 - remainTime, SwitchEvent.AllianceColour.BLUE);
            }
        });

        final Button redSwitchNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_n);
        redSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button redSwitchRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_r);
        redSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switchEvent = new SwitchEvent(135 - remainTime, SwitchEvent.AllianceColour.RED);
            }
        });


    //Force
        final Button redForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_r);
        redForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                forceEvent = new ForceEvent(135 - remainTime, ForceEvent.AllianceColour.RED);
            }
        });

        final Button blueForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_b);
        blueForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                forceEvent = new ForceEvent(135 - remainTime, ForceEvent.AllianceColour.BLUE);
            }
        });

    //Levitate
        final Button redLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_r);
        redLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                levitateEvent = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.RED);
            }
        });

        final Button blueLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_b);
        blueLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                levitateEvent = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.BLUE);
            }
        });

    //Boost
        final Button redBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_r);
        redBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boostEvent = new BoostEvent(135 - remainTime, BoostEvent.AllianceColour.RED);
            }
        });

        final Button blueBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_b);
        blueBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                levitateEvent = new LevitateEvent(135 - remainTime, LevitateEvent.AllianceColour.BLUE);
            }
        });


    //Scale
        final Button scaleBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_b);
        scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scaleEvent = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.BLUE);
            }
        });

        final Button scaleNeutralButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_n);
        scaleNeutralButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button scaleRedButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_r);
        scaleRedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scaleEvent = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.RED);
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

    }
}

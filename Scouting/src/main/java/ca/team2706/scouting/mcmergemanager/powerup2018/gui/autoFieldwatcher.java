package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.SwitchEvent;

/**
 * Created by awesomedana on 2018-02-24.
 */

public class autoFieldwatcher extends AppCompatActivity {


    private Handler m_handler;
    private Runnable m_handlerTask;
    private volatile boolean stopTimer;
    private int remainTime = 15;
    public Event event = new Event();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_field_watcher);


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


        final Button blueSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_b);
        final Event bbevent;

        blueSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchEvent switchEventbb = new SwitchEvent(15 - remainTime, SwitchEvent.AllianceColour.BLUE);

            }
        });


        final Button blueSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_n);
        final Event bnevent;

        blueSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


            final Button blueSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_r);
            final Event brevent;

            blueSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchEvent switchEventbr = new SwitchEvent(15 - remainTime, SwitchEvent.AllianceColour.RED);


            }
        });


            final Button redSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_b);
            final Event rbevent;

            redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchEvent switchEventrb = new SwitchEvent(15 - remainTime, SwitchEvent.AllianceColour.BLUE);


            }
        });


            final Button redSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_n);
            final Event rnevent;

            redSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button redSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_r);
            final Event rrevent;

            redSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchEvent switchEventrr = new SwitchEvent(15 - remainTime, SwitchEvent.AllianceColour.RED);


            }
        });


            final Button scaleBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_b);
            final Event bevent;

            scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleEvent scaleEventb = new ScaleEvent(15 - remainTime, ScaleEvent.AllianceColour.BLUE);


            }
        });


            final Button scaleNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_n);
            final Event nevent;

            scaleNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button scaleRedButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_r);
            final Event revent;

            scaleRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleEvent scaleEventr = new ScaleEvent(15 - remainTime, ScaleEvent.AllianceColour.RED);

            }
        });



    }
}

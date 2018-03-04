package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.RedSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BlueSwitchEvent;

/**
 * Created by awesomedana on 2018-02-24.
 */

public class autoFieldwatcher extends AppCompatActivity {

    int remainTime = 135;

    FieldWatcherObject fieldWatcherObject = new FieldWatcherObject();
    Event event;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_field_watcher);

        final Button blueSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_b);

        blueSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);
            }
        });


        final Button blueSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_n);

        blueSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });


            final Button blueSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_r);
            blueSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new BlueSwitchEvent(135 - remainTime, BlueSwitchEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });


            final Button redSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_b);
            redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);
            }
        });


            final Button redSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_n);
            redSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });


            final Button redSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_r);
            redSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new RedSwitchEvent(135 - remainTime, RedSwitchEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });


            final Button scaleBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_b);
            scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.BLUE);
                fieldWatcherObject.add(event);
            }
        });


            final Button scaleNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_n);
            scaleNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.NEUTRAL);
                fieldWatcherObject.add(event);
            }
        });


            final Button scaleRedButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_r);
            scaleRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = new ScaleEvent(135 - remainTime, ScaleEvent.AllianceColour.RED);
                fieldWatcherObject.add(event);
            }
        });



    }

    public void toTeleopFieldWatcher(View view){
        Intent intent = new Intent(this, TeleopFieldWatcher.class);
        intent.putExtra("FieldWatcherObject", fieldWatcherObject);
        startActivity(intent);
    }
}

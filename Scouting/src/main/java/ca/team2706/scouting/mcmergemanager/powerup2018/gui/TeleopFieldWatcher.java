package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by LaraLim on 2018-02-24.
 */

public class TeleopFieldWatcher extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //Blue alliance switch
        final Button blueSwitchBluebutton = (Button) findViewById(R.id.teleop_fieldwatcher_bs_b);
        blueSwitchBluebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



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

            }
        });

    //Red alliance switch
        final Button redSwitchBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_rs_b);
        redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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

            }
        });


    //Force
        final Button redForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_r);
        redForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button blueForceButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_force_b);
        blueForceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    //Levitate
        final Button redLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_r);
        redLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button blueLevitateButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_levitate_b);
        blueLevitateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    //Boost
        final Button redBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_r);
        redBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        final Button blueBoostButton = (Button) findViewById(R.id.teleop_fieldwatcher_powerup_boost_b);
        blueBoostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



            }
        });


    //Scale
        final Button scaleBlueButton = (Button) findViewById(R.id.teleop_fieldwatcher_scale_b);
        scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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

            }
        });

    }
}

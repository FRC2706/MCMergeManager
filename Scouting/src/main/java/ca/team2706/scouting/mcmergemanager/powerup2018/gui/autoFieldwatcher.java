package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by awesomedana on 2018-02-24.
 */

public class autoFieldwatcher extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_field_watcher);

        final Button blueSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_b);

        blueSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        final Button blueSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_n);

        blueSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button blueSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_bs_r);
            blueSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button redSwitchBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_b);
            redSwitchBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button redSwitchNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_n);
            redSwitchNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button redSwitchRedButton = (Button) findViewById(R.id.auto_fieldwatcher_rs_r);
            redSwitchRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button scaleBlueButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_b);
            scaleBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button scaleNeutralButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_n);
            scaleNeutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


            final Button scaleRedButton = (Button) findViewById(R.id.auto_fieldwatcher_scale_r);
            scaleRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}

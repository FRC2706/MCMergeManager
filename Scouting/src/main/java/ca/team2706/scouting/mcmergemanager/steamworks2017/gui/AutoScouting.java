package ca.team2706.scouting.mcmergemanager.steamworks2017.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.AutoScoutingObject;

public class AutoScouting extends AppCompatActivity {

    private AutoScoutingObject autoScoutingObject2017 = new AutoScoutingObject();
    public int pointsScored;

    SeekBar simpleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_auto_scouting);
        // initiate  views

        simpleSeekBar=(SeekBar)findViewById(R.id.autoBallSeekBar);
        // perform seek bar change listener event used for getting the progress value
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) findViewById(R.id.autoBallScoredTextView);
                tv.setText(progressChangedValue*5 + " points were scored");
                pointsScored = progressChangedValue*5;
            }
        });
    }

    public void toTeleop(View view) {


        autoScoutingObject2017.numFuelScored = pointsScored;

        final CheckBox checkBox = (CheckBox) findViewById(R.id.crossedBaselineCheckBox);
        if (checkBox.isChecked()) {
            autoScoutingObject2017.crossedBaseline = true;
        }
        else {
            autoScoutingObject2017.crossedBaseline = false;
        }

        final CheckBox cb = (CheckBox) findViewById(R.id.startingGearCheckBox);
        if (cb.isChecked()) {

            autoScoutingObject2017.start_gear = true;
        }
        else {
            autoScoutingObject2017.start_gear = false;
        }

        final CheckBox checkiestOfBoxes = (CheckBox) findViewById(R.id.startingBallsCheckBox);
        if (checkiestOfBoxes.isChecked()) {
            autoScoutingObject2017.start_fuel = true;
        }
        else {
            autoScoutingObject2017.start_fuel = false;}

        final CheckBox autoGearDeliveredCheckBox = (CheckBox) findViewById(R.id.autoGearDelivered);
        if (autoGearDeliveredCheckBox.isChecked()) {
            autoScoutingObject2017.gear_delivered = 2;
        }
        else {
            autoScoutingObject2017.gear_delivered = 0;}

        Intent intent = new Intent(this, TeleopScouting.class);
        intent.putExtra("PreGameData", getIntent().getSerializableExtra("PreGameData"));
        intent.putExtra("AutoScoutingData", autoScoutingObject2017);
        startActivity(intent);
    }


}

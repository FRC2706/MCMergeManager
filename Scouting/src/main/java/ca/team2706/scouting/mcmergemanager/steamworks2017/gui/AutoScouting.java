package ca.team2706.scouting.mcmergemanager.steamworks2017.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.gui.TeleopScouting;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.AutoScoutingObject;

public class AutoScouting extends AppCompatActivity {

    private AutoScoutingObject autoScoutingObject2017 = new AutoScoutingObject();
    public int pointsScored;

    SeekBar simpleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_scouting);
        // initiate  views

//        simpleSeekBar=(SeekBar)findViewById(R.id.autoBallSeekBar);
//        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progressChangedValue = 0;
//
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progressChangedValue = progress;
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                TextView tv = (TextView) findViewById(R.id.autoBallScoredTextView);
//                tv.setText(progressChangedValue*5 + " points were scored");
//                pointsScored = progressChangedValue*5;
//            }
//        });
        // perform seek bar change listener event used for getting the progress value
    }

    public void toTeleop(View view) {



        Intent intent = new Intent(this, TeleopScouting.class);
        startActivity(intent);
    }


}

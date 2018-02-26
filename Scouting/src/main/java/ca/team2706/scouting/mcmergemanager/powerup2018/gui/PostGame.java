package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import android.widget.SeekBar.*;
import org.json.JSONException;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.gui.PreGameActivity;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.ClimbEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Comment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.DefenseEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject;


public class PostGame extends AppCompatActivity {

    private PostGameObject postGameObject = new PostGameObject();
    private ClimbEvent climbEvent = new ClimbEvent();
    private DefenseEvent defenseEvent = new DefenseEvent();

    public String notesText;
    public String noEntry = "Notes...";
    public String textViewDisplayString;
    public String pointsScoredString;
    public String test = " seconds to climb";

    public int pointsScored;

    SeekBar timeDeadSeekBar;
    SeekBar defenseSeekBar;
    SeekBar climbTimeSeekBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_post_game);


        final PostGameObject postGameObject = new PostGameObject();

       // postGameObject = (PostGameObject) getIntent().getSerializableExtra("PostGameData");  // climb was set in climbingFragment

        //CLIMB TIME
        climbTimeSeekBar = (SeekBar) findViewById(R.id.climb_time_seekBar);
        // perform seek bar change listener event used for getting the progress value
        climbTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tvd = (TextView) findViewById(R.id.climbTime_tracker_textView);
                pointsScored = progressChangedValue*5;
                climbEvent.climb_time = progressChangedValue * 5;
                pointsScoredString = String.valueOf(pointsScored);
                textViewDisplayString = pointsScoredString + test;
                tvd.setText(textViewDisplayString);

                climbEvent.climb_time = pointsScored;
            }
        });

        //DEAD TIME
        // initiate  views
        timeDeadSeekBar = (SeekBar) findViewById(R.id.deadTime_seekBar);
        // perform seek bar change listener event used for getting the progress value
        timeDeadSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) findViewById(R.id.deadTime_tracker_textView);
                tv.setText(progressChangedValue * 5 + " seconds dead");
                postGameObject.time_dead = progressChangedValue * 5;
            }
        });

        //DEFENSE
        defenseSeekBar = (SeekBar) findViewById(R.id.defense_seekBar);

        defenseSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing.
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) findViewById(R.id.defense_tracker_textView);
                tv.setText(progressChangedValue * 5 + " seconds defending");
                postGameObject.time_defending = progressChangedValue * 5;
            }
        });

    }


        public void returnHome(View view){

            final CheckBox noClimbCheckbox = (CheckBox) findViewById(R.id.climbTypeNoClimb);
            final CheckBox climbFailCheckbox = (CheckBox) findViewById(R.id.climbTypeFail);
            final CheckBox climbBarCheckbox = (CheckBox) findViewById(R.id.climbTypeBar);
            final CheckBox climbAssistCheckbox = (CheckBox) findViewById(R.id.climbTypeAssisted);
            final CheckBox climbWasAssistedCheckbox = (CheckBox) findViewById(R.id.climbTypeWasAssisted);

            if (noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()) {
                // postGameObject.climbType(PostGameObject.ClimbType.NO_CLIMB);
                climbEvent.climbType = ClimbEvent.ClimbType.NO_CLIMB;

            } else if (!noClimbCheckbox.isChecked() && climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()) {
                climbEvent.climbType = ClimbEvent.ClimbType.FAIL;

            } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()) {
                climbEvent.climbType = ClimbEvent.ClimbType.SUCCESS_INDEPENDENT;

            } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()) {
                climbEvent.climbType = ClimbEvent.ClimbType.SUCCESS_ASSISTED_OTHERS;

            } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && climbWasAssistedCheckbox.isChecked()) {
                climbEvent.climbType = ClimbEvent.ClimbType.SUCCESS_ASSISTED;

            } else {
                climbEvent.climbType = ClimbEvent.ClimbType.NO_CLIMB;
            }
            Intent thisIntent = getIntent();

            TeleopScoutingObject t  = (TeleopScoutingObject) getIntent().getSerializableExtra("TeleopScoutingData");



            t.add(climbEvent);
            PreGameObject pre = (PreGameObject) getIntent().getSerializableExtra("PreGameData");
            AutoScoutingObject a = (AutoScoutingObject) thisIntent.getSerializableExtra("AutoScoutingData");
            t.add(climbEvent);
            t.add(postGameObject);

            MatchData.Match match =  new MatchData.Match(pre, a ,t);

            try {
                match.toJson();
            } catch (JSONException e) {
                Toast.makeText(this, "JSON Failed to save", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(this, PreGameActivity.class);
            startActivity(intent);
        }

    }

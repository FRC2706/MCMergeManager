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

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.gui.PreGameActivity;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Comment;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.DefenseEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeleopScoutingObject;





public class PostGame extends AppCompatActivity {

    private PostGameObject postGameObject = new PostGameObject();
    private DefenseEvent defenseEvent = new DefenseEvent();

    public String notesText;
    public String noEntry = "Notes...";
    SeekBar deadTimeSeekBar;
    SeekBar defenseSeekBar;
    SeekBar climbTimeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_post_game);


        // Using this  onClickListener so the text disappears when clicked.
        final EditText notes = (EditText) findViewById(R.id.postGameNotes);
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notes.setText("");
            }
        });

        final CheckBox noClimbCheckbox = (CheckBox) findViewById(R.id.climbTypeNoClimb);
        final CheckBox climbFailCheckbox = (CheckBox) findViewById(R.id.climbTypeFail);
        final CheckBox climbBarCheckbox = (CheckBox) findViewById(R.id.climbTypeBar);
        final CheckBox climbAssistCheckbox = (CheckBox) findViewById(R.id.climbTypeAssisted);
        final CheckBox climbWasAssistedCheckbox = (CheckBox) findViewById(R.id.climbTypeWasAssisted);

        if (noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()){
           // postGameObject.climbType(PostGameObject.ClimbType.NO_CLIMB);
            postGameObject.climbType = PostGameObject.ClimbType.NO_CLIMB;

        } else if (!noClimbCheckbox.isChecked() && climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()){
            postGameObject.climbType = PostGameObject.ClimbType.FAIL;

        } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()) {
            postGameObject.climbType = PostGameObject.ClimbType.SUCCESS_INDEPENDENT;

        } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && climbAssistCheckbox.isChecked() && !climbWasAssistedCheckbox.isChecked()){
            postGameObject.climbType = PostGameObject.ClimbType.SUCCESS_ASSISTED_OTHERS;

        } else if (!noClimbCheckbox.isChecked() && !climbFailCheckbox.isChecked() && !climbBarCheckbox.isChecked() && !climbAssistCheckbox.isChecked() && climbWasAssistedCheckbox.isChecked()){
            postGameObject.climbType = PostGameObject.ClimbType.SUCCESS_ASSISTED;

        } else {
            postGameObject.climbType = PostGameObject.ClimbType.NO_CLIMB;
        }



        postGameObject = (PostGameObject) getIntent().getSerializableExtra("PostGameData");  // climb was set in climbingFragment.

        // initiate  views
        deadTimeSeekBar = (SeekBar) findViewById(R.id.deadTime_seekBar);
        // perform seek bar change listener event used for getting the progress value
        deadTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        climbTimeSeekBar = (SeekBar) findViewById(R.id.climb_time_seekBar);

        climbTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing.
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) findViewById(R.id.climbTime_tracker_textView);
                tv.setText(progressChangedValue * 5 + " seconds climbing");
                postGameObject.climb_time = progressChangedValue * 5;
            }
        });

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


        final Comment comment = new Comment();

        Button fab = (Button) findViewById(R.id.done_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notesText = notes.getText().toString();
                if (!notesText.equals(noEntry)) {
                    postGameObject.notes = notes.getText().toString();
                    comment.setComment(notesText);
                }

                returnHome();
            }
        });





//        notesText = notes.getText().toString();
//
//        if (!notesText.equals(noEntry)) {
//            postGameObject.notes = notes.getText().toString();
//            comment.setComment(notesText);
//        }
    }



        public void returnHome(){
            Intent thisIntent = getIntent();

            PreGameObject pre = (PreGameObject) thisIntent.getSerializableExtra("PreGameData");
            AutoScoutingObject a = (AutoScoutingObject) thisIntent.getSerializableExtra("AutoScoutingData");
            TeleopScoutingObject t  = (TeleopScoutingObject) thisIntent.getSerializableExtra("TeleopScoutingData");

            Intent intent = new Intent(this,PreGameActivity.class);

//            MatchData.Match match = new MatchData.Match(pre, postGameObject, t, a);
//
//            FileUtils.checkLocalFileStructure(this);
//            // save the file to the synced file, if posting fails save to unsynced as well
//            FileUtils.appendToMatchDataFile(match, FileUtils.FileType.SYNCHED);
//            FileUtils.postMatchToServer(this, match.toJson());

            startActivity(intent);

    }
}







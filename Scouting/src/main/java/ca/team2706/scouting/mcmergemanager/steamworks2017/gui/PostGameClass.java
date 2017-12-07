package ca.team2706.scouting.mcmergemanager.steamworks2017.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeleopScoutingObject;


/**
 * Created by JustinT on 2017-02-12.
 */

public class PostGameClass extends AppCompatActivity {

    private PostGameObject postGameObject;
    private DefenseEvent defenseEvent = new DefenseEvent();

    public String notesText;
    public String noEntry = "Notes...";
    SeekBar deadTimeSeekBar;
    SeekBar defenseSeekBar;

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


        postGameObject = (PostGameObject) getIntent().getSerializableExtra("PostGameData");  // climb was set in climbingFragment.

        // initiate  views
        deadTimeSeekBar = (SeekBar) findViewById(R.id.timeDeadSeekBar);
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
                TextView tv = (TextView) findViewById(R.id.time_dead_text_view);
                tv.setText(progressChangedValue * 5 + " seconds dead");
                postGameObject.time_dead = progressChangedValue * 5;
            }
        });

        defenseSeekBar = (SeekBar) findViewById(R.id.time_defending_seekbar);

        defenseSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing.
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) findViewById(R.id.time_defending_text_view);
                tv.setText(progressChangedValue * 5 + " seconds defending");
                postGameObject.time_defending = progressChangedValue * 5;
            }
        });

        final Comment comment = new Comment();

        Button fab = (Button) findViewById(R.id.post_game_submit_button);
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

            MatchData.Match match = new MatchData.Match(pre, postGameObject, t, a);

            FileUtils.checkLocalFileStructure(this);
            // save the file to the synced file, if posting fails save to unsynced as well
            FileUtils.appendToMatchDataFile(match, FileUtils.FileType.SYNCHED);
            FileUtils.postMatchToServer(this, match.toJson());

            startActivity(intent);

    }
}







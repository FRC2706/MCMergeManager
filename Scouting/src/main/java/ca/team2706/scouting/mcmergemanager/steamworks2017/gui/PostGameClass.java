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
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.DefenseEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PostGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeleopScoutingObject;


/**
 * Created by JustinT on 2017-02-12.
 */

public class PostGameClass extends AppCompatActivity {

    public int timeDead;
    public int timeDefending;
    public String woo = "Woo";

    private PostGameObject postGameObject = new PostGameObject();
    private DefenseEvent defenseEvent = new DefenseEvent();


    SeekBar deadTimeSeekBar;
    SeekBar defenseSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_post_game);
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
                timeDead = progressChangedValue*5;
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
                timeDefending = progressChangedValue*5;

            }
        });

        Button fab = (Button) findViewById(R.id.post_game_submit_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defenseEvent.skill = timeDefending;
                postGameObject.time_dead = timeDead;

                returnHome(view);
            }
        });

        EditText notes = (EditText) findViewById(R.id.postGameNotes);
        postGameObject.notes = notes.getText().toString();
    }

        public void returnHome(View view ){
            Intent thisIntent = getIntent();

            PreGameObject pre = (PreGameObject) thisIntent.getSerializableExtra("PreGameData");
            AutoScoutingObject a = (AutoScoutingObject) thisIntent.getSerializableExtra("AutoScoutingData");
            TeleopScoutingObject t  = (TeleopScoutingObject) thisIntent.getSerializableExtra("TeleopScoutingData");
            PostGameObject post = (PostGameObject) thisIntent.getSerializableExtra("PostGameData");  // climb was set in climbingFragment.
            post.time_dead = timeDead;
            post.time_defending = timeDefending;

            Intent intent = new Intent(this,PreGameActivity.class);

            MatchData.Match match = new MatchData.Match(pre, post, t, a);
            FileUtils.checkLocalFileStructure(this);
            FileUtils.appendToMatchDataFile(match);

            startActivity(intent);

    }
}







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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_post_game);


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


       // postGameObject = (PostGameObject) getIntent().getSerializableExtra("PostGameData");  // climb was set in climbingFragment

    }


        public void returnHome(View view){
            Intent thisIntent = getIntent();

            PreGameObject pre = (PreGameObject) getIntent().getSerializableExtra("PreGameData");
            AutoScoutingObject a = (AutoScoutingObject) thisIntent.getSerializableExtra("AutoScoutingData");
            TeleopScoutingObject t  = (TeleopScoutingObject) thisIntent.getSerializableExtra("TeleopScoutingData");

            Intent intent = new Intent(this, PreGameActivity.class);


            startActivity(intent);

    }
}







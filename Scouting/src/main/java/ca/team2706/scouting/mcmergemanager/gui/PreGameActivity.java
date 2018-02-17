package ca.team2706.scouting.mcmergemanager.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.AutoScouting;

public class PreGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game);
        Intent intent = getIntent();

        int matchNo = intent.getIntExtra(getString(R.string.EXTRA_MATCH_NO), -1);
        MatchSchedule matchSchedule = (MatchSchedule) intent.getSerializableExtra( getString(R.string.EXTRA_MATCH_SCHEDULE));

        if (matchNo != -1)
            ((EditText) findViewById(R.id.match_num_field)).setText(""+matchNo);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Buttons
    public void startGame(View view) {
        EditText matchNumField = (EditText) findViewById(R.id.match_num_field);
        String matchNum = matchNumField.getText().toString();

        EditText teamNumField = (EditText) findViewById(R.id.team_num_field);
        String teamNum = teamNumField.getText().toString();

        int matchNumInt;
        int teamNumInt;

        try {
            matchNumInt = Integer.parseInt(matchNum);
            teamNumInt = Integer.parseInt(teamNum);
        } catch(NumberFormatException e) {
            return;
        }


        Intent intent = new Intent(this,AutoScouting.class);
        intent.putExtra("PreGameData",new PreGameObject(teamNumInt, matchNumInt));
        startActivity(intent);
    }

}

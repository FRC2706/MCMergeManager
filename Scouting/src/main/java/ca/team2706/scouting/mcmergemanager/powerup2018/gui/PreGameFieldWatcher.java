package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;

/**
 * Created by LaraLim on 2018-03-05.
 */

public class PreGameFieldWatcher extends AppCompatActivity {

    PreGameObject preGameObject = new PreGameObject();

    EditText matchNumField;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_pre_fieldwatcher);

        Intent intent = getIntent();

        int matchNo = intent.getIntExtra(getString(R.string.EXTRA_MATCH_NO), -1);
        MatchSchedule matchSchedule = (MatchSchedule) intent.getSerializableExtra( getString(R.string.EXTRA_MATCH_SCHEDULE));


        if (matchNo != -1)
            ((EditText) findViewById(R.id.match_num_field)).setText(matchNo);

        matchNumField = (EditText) findViewById(R.id.Match_number_editText);

    }

    public void toAutoFieldWatcher(View view){
        Intent intent = new Intent(this, autoFieldwatcher.class);

        String matchNum = matchNumField.getText().toString();

        int matchNumInt;

        try {
            matchNumInt = Integer.parseInt(matchNum);

        } catch(NumberFormatException e) {
            return;
        }

        preGameObject.matchNumber = matchNumInt;
        preGameObject.teamNumber = -1;
        intent.putExtra("PreGameObject", preGameObject);
        startActivity(intent);
    }

}

package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;

/**
 * Created by LaraLim on 2018-03-05.
 */

public class PreGameFieldWatcher extends AppCompatActivity {

    FieldWatcherObject fieldWatcherObject = new FieldWatcherObject();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_pre_fieldwatcher);

    }

    public void toAutoFieldWatcher(View view){
        Intent intent = new Intent(this, autoFieldwatcher.class);
        intent.putExtra("FieldWatcherObject", fieldWatcherObject);
        startActivity(intent);
    }

}

package ca.team2706.scouting.mcmergemanager.gui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtils;

/**
 * Created by Merge on 2018-01-24.
 */

public class ThreatListGenerator extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_threatlist);
    }

    ArrayList<Integer> teamListInt = new ArrayList<>();

    public void parseTBATeamsList() {
        String jsonTeamsList = BlueAllianceUtils.fetchTeamsRegisteredAtEventString();

        if(jsonTeamsList == null)
            return;

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonTeamsList);

            // Loop over each team
            for(int i = 0; i < jsonArray.length(); i++) {
                teamListInt.add(Integer.valueOf(jsonArray.get(i).toString().substring(3)));
            }

            Collections.sort(teamListInt);
        } catch(JSONException e) {
            Log.d("Error parsing json: ", e.toString());
        }
    }

}
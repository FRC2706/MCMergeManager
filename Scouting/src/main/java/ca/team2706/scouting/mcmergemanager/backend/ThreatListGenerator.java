package ca.team2706.scouting.mcmergemanager.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Merge on 2018-01-24.
 */

public class ThreatListGenerator {

    ArrayList<Integer> teamListInt = new ArrayList<>();



    public void parseTBATeamsList(String jsonTeamsList) {
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
package ca.team2706.scouting.mcmergemanager.gui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtils;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.DataRequester;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;

/**
 * Created by Merge on 2018-01-24.
 */

public class ThreatListGenerator extends AppCompatActivity  {

    EditText switch_weight;
    EditText scale_weight;
    EditText defense_weight;
    EditText exchange_weight;
    TextView total;

    ArrayList<String> teams = new ArrayList<>();
    ArrayList<Double> priority = new ArrayList<>();

    public enum Priority {
        DEFENSE, SCALE, SWITCH, EXCHANGE
    }

    int DefenseWeight = 25, ScaleWeight = 25, SwitchWeight = 25, ExchangeWeight = 25, maxValue = 25;

//    public void parseTBATeamsList() {
//        String jsonTeamsList = BlueAllianceUtils.fetchTeamsRegisteredAtEvent(this);
//        String jsonTeamsList = BlueAllianceUtils.fetchTeamsRegisteredAtEventString();
//
//        if(jsonTeamsList == null)
//            return;
//
//        JSONArray jsonArray;
//        try {
//            jsonArray = new JSONArray(jsonTeamsList);
//
//            // Loop over each team
//            for(int i = 0; i < jsonArray.length(); i++) {
//                teamListInt.add(Integer.valueOf(jsonArray.get(i).toString().substring(3)));
//            }
//
//            Collections.sort(teamListInt);
//        } catch(JSONException e) {
//            Log.d("Error parsing json: ", e.toString());
//        }
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_threatlist);

        switch_weight   = (EditText) findViewById(R.id.switch_weight);
        scale_weight    = (EditText) findViewById(R.id.scale_weight);
        defense_weight  = (EditText) findViewById(R.id.defense_weight);
        exchange_weight = (EditText) findViewById(R.id.exhange_weight);
        total           = (TextView) findViewById(R.id.max_text_view);

        // TODO needs to be reworked for server data
        teams = FileUtils.getTeams();

        switch_weight.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                total.setText(getTotal().toString());

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }
    
    private Integer getTotal(){

        int total;

        try {
            total = Integer.parseInt(switch_weight.getText().toString())
                    + Integer.parseInt(scale_weight.getText().toString())
                    + Integer.parseInt(defense_weight.getText().toString())
                    + Integer.parseInt(exchange_weight.getText().toString());
        } catch (NumberFormatException e) {
            total = 100;
        }

        return total;
    }
}









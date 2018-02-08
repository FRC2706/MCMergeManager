package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by Merge on 2018-02-07.
 */

public class MatchData implements Serializable{

    public static final int AUTO_TIMESTAMP = -1;

    public static final int POST_GAME_TIMESTAMP = 140;

    public static final String AUTO_ID = "auto";
    public static final String CUBE_PLACE_ID = "place_cube";
    public static final String CUBE_PICKUP_ID = "pickup_cube";
    public static final String POST_GAME_ID = "post_game";





    public static class Match implements Serializable {

        public TeleopScoutingObject teleopScoutingObject;
        public ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects.AutoScoutingObject autoScoutingObject;
        public PostGameObject postGameObject;
        public PreGameObject preGameObject;

        public Match(PreGameObject preGameObject, PostGameObject postGameObject, TeleopScoutingObject teleopScoutingObject,
                     ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoDataObjects.AutoScoutingObject autoScoutingObject) {
            this.preGameObject = preGameObject;
            this.autoScoutingObject = autoScoutingObject;
            this.postGameObject = postGameObject;
            this.teleopScoutingObject = teleopScoutingObject;
        }

        public Match(JSONObject jsonObject){

            try {
                preGameObject.teamNumber = jsonObject.getInt("team");
                preGameObject.matchNumber = jsonObject.getInt("match_number");

                JSONArray jsonArray = jsonObject.getJSONArray("events");

                for(int i = 0; i < jsonArray.length();i++) {
                    JSONObject obj = new JSONObject(jsonArray.get(i).toString());
                    Event event;

                    switch (obj.getString("goal")){


                    }

                }



            } catch (JSONException je){}


        }


    }
}

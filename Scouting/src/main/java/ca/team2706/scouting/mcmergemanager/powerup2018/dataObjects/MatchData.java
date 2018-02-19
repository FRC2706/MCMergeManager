package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoLineCrossEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoMalfunctionEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;


/**
 * Created by Merge on 2018-02-07.
 */

public class MatchData implements Serializable{

    // Timestamp for events that happen in the post game
    public static final int POST_GAME_TIMESTAMP = 140;

    public static final String AUTO_CUBE_PLACE_ID = "auto_place_cube";
    public static final String AUTO_CUBE_PICKUP_ID = "auto_pickup_cube";
    public static final String AUTO_MALFUNCTION_ID = "auto_malfunction";
    public static final String AUTO_LINE_CROSS_ID = "auto_line_cross";

    public static final String CUBE_PLACE_ID = "place_cube";
    public static final String CUBE_PICKUP_ID = "pickup_cube";
    public static final String POST_GAME_ID = "post_game";
    public static final String CLIMB_ID = "climb";

    public static final String START_TIME = "start_time";
    public static final String EXTRA = "extra";
    public static final String GOAL = "goal";

    public static class Match implements Serializable {

        public TeleopScoutingObject teleopScoutingObject;
        public AutoScoutingObject autoScoutingObject;
        public PreGameObject preGameObject;

        public Match(PreGameObject preGameObject, TeleopScoutingObject teleopScoutingObject, AutoScoutingObject autoScoutingObject) {
            this.preGameObject = preGameObject;
            this.autoScoutingObject = autoScoutingObject;
            this.teleopScoutingObject = teleopScoutingObject;
        }


        public Match(JSONObject jsonObject) throws JSONException {
                preGameObject.teamNumber = jsonObject.getInt("team");
                preGameObject.matchNumber = jsonObject.getInt("match_number");

                JSONArray jsonArray = jsonObject.getJSONArray("events");

                Event event;

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = new JSONObject(jsonArray.get(i).toString());

                    try {
                        switch (obj.getString("goal")) {
                            case AUTO_CUBE_PICKUP_ID:
                                event = new AutoCubePickupEvent(obj.getInt(START_TIME), AutoCubePickupEvent.PickupType.valueOf(obj.getString(EXTRA)));
                                autoScoutingObject.add(event);
                                break;
                            case AUTO_CUBE_PLACE_ID:
                                event = new AutoCubePlacementEvent(obj.getInt(START_TIME), AutoCubePlacementEvent.PlacementType.valueOf(obj.getString(EXTRA)));
                                autoScoutingObject.add(event);
                                break;
                            case AUTO_LINE_CROSS_ID:
                                event = new AutoLineCrossEvent(obj.getInt(START_TIME), Boolean.valueOf(obj.getString(EXTRA)));
                                autoScoutingObject.add(event);
                                break;
                            case AUTO_MALFUNCTION_ID:
                                event = new AutoMalfunctionEvent(obj.getInt(START_TIME), Boolean.valueOf(obj.getString(EXTRA)));
                                autoScoutingObject.add(event);
                                break;
                            case CUBE_PICKUP_ID:
                                event = new CubePickupEvent(obj.getInt(START_TIME), CubePickupEvent.PickupType.valueOf(obj.getString(EXTRA)));
                                teleopScoutingObject.add(event);
                                break;
                            case CUBE_PLACE_ID:
                                event = new CubePlacementEvent(obj.getInt(START_TIME), CubePlacementEvent.PlacementType.valueOf(obj.getString(EXTRA)));
                                teleopScoutingObject.add(event);
                                break;
                            case POST_GAME_ID:

                                break;
                            default:
                                // TODO
                        }
                    } catch (IllegalArgumentException e) {
                        // If an illegal argument is found in the JSON file, the for loop will continue to the next item in the array
                        Log.d("Invalid Argument", "INVALID");
                        i++;
                    }

                }
        }

        public void toJson() throws JSONException {
            // TODO add auto
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            jsonObject.put("team", preGameObject.teamNumber);
            jsonObject.put("match_number", preGameObject.matchNumber);

            for(Event event : autoScoutingObject.getEvents()){
                JSONObject obj = new JSONObject();
                obj.put(START_TIME, event.timestamp);

                if(event instanceof  AutoCubePlacementEvent) {
                    AutoCubePlacementEvent e = (AutoCubePlacementEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.placementType.toString());
                }

                if (event instanceof AutoCubePickupEvent) {
                    AutoCubePickupEvent e = (AutoCubePickupEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.pickupType.toString());
                }
                if (event instanceof AutoLineCrossEvent) {
                    AutoLineCrossEvent e = (AutoLineCrossEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.crossedAutoLine.toString());
                }
                if (event instanceof AutoMalfunctionEvent) {
                    AutoMalfunctionEvent e = (AutoMalfunctionEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.autoMalfunction.toString());
                }
                jsonArray.put(obj);

            }

            for(Event event : teleopScoutingObject.getEvents()){
                JSONObject obj = new JSONObject();
                obj.put(START_TIME, event.timestamp);
                if(event instanceof CubePickupEvent) {
                    CubePickupEvent e = (CubePickupEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.pickupType.toString());
                }
                if(event instanceof CubePlacementEvent) {
                    CubePlacementEvent e = (CubePlacementEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.placementType.toString());
                    }
                if(event instanceof CubeDroppedEvent) {
                    CubeDroppedEvent e = (CubeDroppedEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.dropType.toString());
                }if (event instanceof PostGameObject) {
                    // For this event, we set extra to time_dead and end_time to time_defending
                    PostGameObject e = (PostGameObject) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, 140);
                    obj.put(EXTRA, Double.toString(e.time_dead));
                    obj.put("end_time", e.time_defending);
                } if (event instanceof ClimbEvent) {
                    // For this event, end_time is climb_time
                    ClimbEvent e = (ClimbEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(EXTRA, e.climbType.toString());
                    obj.put(START_TIME, 140);
                    obj.put("end_time", e.climb_time);
                }
                jsonArray.put(obj);
            }

            jsonObject.put( "events", jsonArray);
            FileUtils.saveJsonData(jsonObject);

        }

    }

    // Member Variables
    public ArrayList<Match> matches;

    /** Empty constructor **/
    public MatchData() {
        matches = new ArrayList<>();
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public MatchData filterByTeam(int teamNo) {
        MatchData matchData = new MatchData();

        for(MatchData.Match match : matches) {
            if (match.preGameObject.teamNumber == teamNo)
                matchData.addMatch(match);
        }
        return matchData;
    }
}

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
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BoostEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.FieldWatcherObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ForceEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.LevitateEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.SwitchEvent;


/**
 * Created by Merge on 2018-02-07.
 */

public class MatchData implements Serializable{

    // Timestamp for events that happen in the post game
    public static final int POST_GAME_TIMESTAMP = 140;

    // Auto ID's
    public static final String AUTO_CUBE_PLACE_ID = "auto_cube_placement";
    public static final String AUTO_CUBE_PICKUP_ID = "auto_cube_pickup";
    public static final String AUTO_MALFUNCTION_ID = "auto_malfunction";
    public static final String AUTO_LINE_CROSS_ID = "auto_line_cross";

    // Teleop ID's
    public static final String CUBE_PLACE_ID = "cube_placement";
    public static final String CUBE_PICKUP_ID = "pickup_cube";
    public static final String CUBE_DROPPED_ID = "cube_dropped";
    public static final String POST_GAME_ID = "post_game";
    public static final String CLIMB_ID = "climb";

    // JSON keys
    public static final String START_TIME = "start_time";
    public static final String EXTRA = "extra";
    public static final String GOAL = "goal";

    // Field Watcher ID's
    public static final String BOOST = "field_watcher_boost";
    public static final String FORCE = "field_watcher_force";
    public static final String LEVITATE = "field_watcher_levitate";
    public static final String SWITCH = "field_watcher_switch";
    public static final String SCALE = "field_watcher_scale";

    public static class Match implements Serializable {

        public TeleopScoutingObject teleopScoutingObject = new TeleopScoutingObject();
        public AutoScoutingObject autoScoutingObject = new AutoScoutingObject();
        public PreGameObject preGameObject = new PreGameObject();
        public FieldWatcherObject fieldWatcherObject = new FieldWatcherObject();

        public Match(PreGameObject preGameObject, AutoScoutingObject autoScoutingObject, TeleopScoutingObject teleopScoutingObject) {
            this.preGameObject = preGameObject;
            this.autoScoutingObject = autoScoutingObject;
            this.teleopScoutingObject = teleopScoutingObject;
        }

        public Match(PreGameObject preGameObject, FieldWatcherObject fieldWatcherObject){
            this.preGameObject = preGameObject;
            this.fieldWatcherObject = fieldWatcherObject;
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
                            case CUBE_DROPPED_ID:
                                event = new CubeDroppedEvent(obj.getInt(START_TIME), CubeDroppedEvent.DropType.valueOf(obj.getString(EXTRA)));
                                teleopScoutingObject.add(event);
                                break;
                            case CLIMB_ID:
                                event = new ClimbEvent(ClimbEvent.ClimbType.valueOf(obj.getString(EXTRA)), obj.getInt("end_time"), obj.getInt(START_TIME));
                                teleopScoutingObject.add(event);
                                break;
                            case POST_GAME_ID:
                                event = new PostGameObject(obj.getInt(EXTRA), obj.getInt("end_time"), obj.getInt(START_TIME));
                                teleopScoutingObject.add(event);
                                break;
                            case SCALE:
                                event = new ScaleEvent(obj.getInt(START_TIME), ScaleEvent.AllianceColour.valueOf(obj.getString(EXTRA)));
                                fieldWatcherObject.add(event);
                                break;
                            case SWITCH:
                                event = new SwitchEvent(obj.getInt(START_TIME), SwitchEvent.AllianceColour.valueOf(obj.getString(EXTRA)));
                                fieldWatcherObject.add(event);
                                break;
                            case LEVITATE:
                                event = new LevitateEvent(obj.getInt(START_TIME), LevitateEvent.AllianceColour.valueOf(obj.getString(EXTRA)));
                                fieldWatcherObject.add(event);
                                break;
                            case FORCE:
                                event = new ForceEvent(obj.getInt(START_TIME), ForceEvent.AllianceColour.valueOf(obj.getString(EXTRA)));
                                fieldWatcherObject.add(event);
                                break;
                            case BOOST:
                                event = new BoostEvent(obj.getInt(START_TIME), BoostEvent.AllianceColour.valueOf(obj.getString(EXTRA)));
                                fieldWatcherObject.add(event);
                                break;
                            default:
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                        // If an illegal argument is found in the JSON file, the for loop will continue to the next item in the array
                        Log.d("Invalid Argument", "INVALID");
                        i++;
                    }

                }

        }

        public void toJson() throws JSONException {

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
                    obj.put(START_TIME, POST_GAME_TIMESTAMP);
                    obj.put(EXTRA, Double.toString(e.time_dead));
                    obj.put("end_time", e.time_defending);
                } if (event instanceof ClimbEvent) {
                    // For this event, end_time is climb_time
                    ClimbEvent e = (ClimbEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(EXTRA, e.climbType.toString());
                    obj.put(START_TIME, POST_GAME_TIMESTAMP);
                    obj.put("end_time", e.climb_time);
                }
                jsonArray.put(obj);
            }

            for (Event event : fieldWatcherObject.getEvents()) {
                JSONObject obj = new JSONObject();
                obj.put(START_TIME, event.timestamp);
                if (event instanceof BoostEvent) {
                    BoostEvent e = (BoostEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.allianceColour.toString());
                } if (event instanceof ForceEvent) {
                    ForceEvent e = (ForceEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.allianceColour.toString());
                } if (event instanceof LevitateEvent) {
                    LevitateEvent e = (LevitateEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.allianceColour.toString());
                } if (event instanceof ScaleEvent) {
                    ScaleEvent e = (ScaleEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.allianceColour.toString());
                } if (event instanceof SwitchEvent) {
                    SwitchEvent e = (SwitchEvent) event;
                    obj.put(GOAL, e.ID);
                    obj.put(START_TIME, e.timestamp);
                    obj.put(EXTRA, e.allianceColour.toString());
                }

                jsonArray.put(obj);

            }

            jsonObject.put("events", jsonArraySelectionSort(jsonArray));
            FileUtils.saveJsonData(jsonObject);

        }
    }

    private static JSONArray jsonArraySelectionSort(JSONArray jsonArray) throws JSONException{
        for (int i = 0; i < jsonArray.length() -1; i++) {
            int index = i;
            for (int j = i + 1; j < jsonArray.length(); j++)
                if (jsonArray.getJSONObject(j).getInt(START_TIME) < jsonArray.getJSONObject(i).getInt(START_TIME)) {
                    index = j;
                }
            int smallerNumber = jsonArray.getJSONObject(index).getInt(START_TIME);
            jsonArray.getJSONObject(index).put(START_TIME, jsonArray.getJSONObject(i).getInt(START_TIME));
            jsonArray.getJSONObject(i).put(START_TIME, smallerNumber);
        }
        return jsonArray;
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

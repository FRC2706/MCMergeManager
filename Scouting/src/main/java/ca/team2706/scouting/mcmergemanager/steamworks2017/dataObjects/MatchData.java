package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MatchData implements Serializable {

    // objective ids
    public static final int startFuelID = 25;
    public static final int startGearID = 26;
    public static final int crossedBaselineID = 27;
    public static final int gearDeliveredID = 28;
    public static final int boilerAttemptedID = 29;
    public static final int openHopperID = 31;
    public static final int climbID = 32;
    public static final int highBoilerID = 33;
    public static final int lowBoilerID = 34;
    public static final int groundFuelPickupID = 35;
    public static final int wallFuelPickupID = 36;
    public static final int hopperFuelPickupID = 37;
    public static final int groundGearPickupID = 38;
    public static final int wallGearPickupID = 39;
    public static final int gearDelivBoilerID = 40;
    public static final int gearDelivFeederID = 41;
    public static final int gearDelivCenterID = 42;
    public static final int noClimbID = 43;


    public static class Match implements Serializable {

        public PreGameObject preGameObject;
        public PostGameObject postGameObject;
        public TeleopScoutingObject teleopScoutingObject;
        public AutoScoutingObject autoScoutingObject;

        public Match(PreGameObject preGameObject, PostGameObject postGameObject, TeleopScoutingObject teleopScoutingObject,
                     AutoScoutingObject autoScoutingObject) {
            this.preGameObject = preGameObject;
            this.autoScoutingObject = autoScoutingObject;
            this.postGameObject = postGameObject;
            this.teleopScoutingObject = teleopScoutingObject;
        }

        // TODO: this needs to be re-worked because MikeO changed around the data

        public Match(JSONObject jsonObject) {
            preGameObject = new PreGameObject();
            postGameObject = new PostGameObject();
            teleopScoutingObject = new TeleopScoutingObject();
            autoScoutingObject = new AutoScoutingObject();

            try {
                // pregame
                preGameObject.teamNumber = jsonObject.getInt("team_number");
                preGameObject.matchNumber = jsonObject.getInt("number");

                // autonomous
                JSONArray autonomies = (JSONArray)jsonObject.get("autonomies");
                for(int i = 0; i < autonomies.length();i++) {
                    JSONObject obj = (JSONObject) autonomies.get(i);
                    switch(obj.getInt("objective_id")) {
                        case startFuelID:
                            autoScoutingObject.start_fuel = obj.getBoolean("success");
                            break;
                        case startGearID:
                            autoScoutingObject.start_gear = obj.getBoolean("success");
                            break;
                        case crossedBaselineID:
                            autoScoutingObject.crossedBaseline = obj.getBoolean("success");
                            break;
                        case openHopperID:
                            autoScoutingObject.open_hopper = obj.getInt("duration");
                            break;
                        case gearDeliveredID:
                            autoScoutingObject.gear_delivered = obj.getInt("duration");
                            break;
                        case boilerAttemptedID:
                            autoScoutingObject.boiler_attempted = obj.getInt("duration");
                            break;
                    }
                }
                // teleop
                JSONArray arrEve = (JSONArray) jsonObject.get("events");
                for(int i = 0; i < arrEve.length(); i++) {
                    JSONObject obj = new JSONObject(arrEve.get(i).toString());
                    Event event;
                    switch ((int) obj.get("objective_id")) {
                        case highBoilerID:
                            event = new FuelShotEvent(obj.getDouble("start_time"), true,
                                    (int) obj.get("position_x"), (int) obj.get("position_y"));
                            break;
                        case lowBoilerID:
                            event = new FuelShotEvent(obj.getDouble("start_time"), false,
                                    (int) obj.get("position_x"), (int) obj.get("position_y"));
                            break;
                        case groundFuelPickupID:
                            event = new FuelPickupEvent(obj.getDouble("start_time"), FuelPickupEvent.FuelPickupType.GROUND,
                                    (int) obj.get("position_x"));
                            break;
                        case hopperFuelPickupID:
                            event = new FuelPickupEvent(obj.getDouble("start_time"), FuelPickupEvent.FuelPickupType.HOPPER,
                                    (int) obj.get("position_x"));
                            break;
                        case wallFuelPickupID:
                            event = new FuelPickupEvent(obj.getDouble("start_time"), FuelPickupEvent.FuelPickupType.WALL,
                                    (int) obj.get("position_x"));
                            break;
                        case gearDelivBoilerID:
                            GearDelivevryEvent.GearDeliveryStatus gearDeliveryStatus = null;
                            switch(obj.getInt("position_x")) {
                                case 0:
                                    gearDeliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;
                                    break;
                                case 1:
                                    gearDeliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DROPPED_DELIVERING;
                                    break;
                                case 2:
                                    gearDeliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DROPPED_MOVING;
                                    break;
                            }
                            event = new GearDelivevryEvent(obj.getDouble("start_time"), GearDelivevryEvent.Lift.BOILER_SIDE,
                                    gearDeliveryStatus);
                            break;
                        case gearDelivCenterID:
                            GearDelivevryEvent.GearDeliveryStatus gearDeliveryStatus1 = null;
                            switch(obj.getInt("position_x")) {
                                case 0:
                                    gearDeliveryStatus1 = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;
                                    break;
                                case 1:
                                    gearDeliveryStatus1 = GearDelivevryEvent.GearDeliveryStatus.DROPPED_DELIVERING;
                                    break;
                                case 2:
                                    gearDeliveryStatus1 = GearDelivevryEvent.GearDeliveryStatus.DROPPED_MOVING;
                                    break;
                            }
                            event = new GearDelivevryEvent(obj.getDouble("start_time"),
                                    GearDelivevryEvent.Lift.CENTRE, gearDeliveryStatus1);
                            break;
                        case gearDelivFeederID:
                            GearDelivevryEvent.GearDeliveryStatus gearDeliveryStatus2 = null;
                            switch(obj.getInt("position_x")) {
                                case 0:
                                    gearDeliveryStatus2 = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;
                                    break;
                                case 1:
                                    gearDeliveryStatus2 = GearDelivevryEvent.GearDeliveryStatus.DROPPED_DELIVERING;
                                    break;
                                case 2:
                                    gearDeliveryStatus2 = GearDelivevryEvent.GearDeliveryStatus.DROPPED_MOVING;
                                    break;
                            }
                            event = new GearDelivevryEvent(obj.getDouble("start_time"), GearDelivevryEvent.Lift.FEEDER_SIDE,
                                    gearDeliveryStatus2);
                            break;
                        case groundGearPickupID:
                            event = new GearPickupEvent(obj.getDouble("start_time"), GearPickupEvent.GearPickupType.GROUND);
                            break;
                        case wallGearPickupID:
                            event = new GearPickupEvent(obj.getDouble("start_time"), GearPickupEvent.GearPickupType.WALL);
                            break;
                        case DefenseEvent.objectiveId:
                            event = new DefenseEvent(obj.getDouble("start_time"), (int) obj.get("defense_skill"));
                            break;
                        case climbID:
                            if (obj.getBoolean("success") == true)
                                postGameObject.climbType = PostGameObject.ClimbType.SUCCESS;
                            else if (obj.getBoolean("success") == false)
                                postGameObject.climbType = PostGameObject.ClimbType.FAIL;
                            event = new Event();
                            break;
                        case noClimbID:
                            postGameObject.climbType = PostGameObject.ClimbType.NO_CLIMB;
                            event = new Event();
                            break;
                        default:
                            event = new Event(DefenseEvent.objectiveId);
                            break;
                        }
                    teleopScoutingObject.add(event);
                }

                postGameObject.notes = jsonObject.getString("general_notes");
                postGameObject.time_dead = jsonObject.getDouble("time_dead");

            } catch(JSONException e) {
                Log.d("Error parsing json", e.toString());
            } catch(IllegalArgumentException e) {
                Log.d("enum failure", e.toString());
            }
        }


        public JSONObject toJson() {
            JSONObject jsonObject = new JSONObject();

            try {
                // pregame
                jsonObject.put("team_number", preGameObject.teamNumber);
                jsonObject.put("number", preGameObject.matchNumber);

                // autonomous
                JSONArray arrAuto = new JSONArray();
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", crossedBaselineID);
                    obj.put("success", autoScoutingObject.crossedBaseline);
                    arrAuto.put(obj);
                }
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", startFuelID);
                    obj.put("success", autoScoutingObject.start_fuel);
                    arrAuto.put(obj);
                }
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", startGearID);
                    obj.put("success", autoScoutingObject.start_gear);
                    arrAuto.put(obj);
                }
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", boilerAttemptedID);
                    obj.put("duration", autoScoutingObject.boiler_attempted);
                    arrAuto.put(obj);
                }
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", gearDeliveredID);
                    obj.put("duration", autoScoutingObject.gear_delivered);
                    arrAuto.put(obj);
                }
                {
                    JSONObject obj = new JSONObject();
                    obj.put("objective_id", openHopperID);
                    // TODO
                    obj.put("duration", autoScoutingObject.open_hopper);
                    arrAuto.put(obj);
                }
                jsonObject.put("autonomies", arrAuto);

                // teleop
                JSONArray arr = new JSONArray();
                for(Event event : teleopScoutingObject.getEvents()) {
                    JSONObject obj = new JSONObject();
                    obj.put("start_time", event.timestamp);
                    if(event instanceof FuelPickupEvent) {
                        FuelPickupEvent e = (FuelPickupEvent) event;
                        obj.put("position_x", e.amount);
                        switch(e.pickupType) {
                            case GROUND:
                                obj.put("objective_id", groundFuelPickupID);
                                break;
                            case HOPPER:
                                obj.put("objective_id", hopperFuelPickupID);
                                break;
                            case WALL:
                                obj.put("objective_id", wallFuelPickupID);
                                break;
                            default:
                                obj.put("objective_id", groundFuelPickupID);
                        }
                    } else if(event instanceof FuelShotEvent) {
                        FuelShotEvent e = (FuelShotEvent) event;
                        obj.put("position_x", e.numScored);
                        obj.put("position_y", e.numMissed);
                        if(e.boiler)
                            obj.put("objective_id", highBoilerID);
                        else
                            obj.put("objective_id", lowBoilerID);
                    } else if(event instanceof GearPickupEvent) {
                        GearPickupEvent e = (GearPickupEvent) event;
                        switch(e.pickupType) {
                            case GROUND:
                                obj.put("objective_id", groundGearPickupID);
                                break;
                            case WALL:
                                obj.put("objective_id", wallGearPickupID);
                                break;
                            default:
                                obj.put("objective_id", groundGearPickupID);
                        }
                    } else if(event instanceof GearDelivevryEvent) {
                        GearDelivevryEvent e = (GearDelivevryEvent) event;
                        switch(e.lift) {
                            case BOILER_SIDE:
                                obj.put("objective_id", gearDelivBoilerID);
                                break;
                            case CENTRE:
                                obj.put("objective_id", gearDelivCenterID);
                                break;
                            case FEEDER_SIDE:
                                obj.put("objective_id", gearDelivFeederID);
                                break;
                            default:
                                obj.put("objective_id", gearDeliveredID);
                        }
                        switch(e.deliveryStatus) {
                            case DELIVERED:
                                obj.put("position_x", 0);
                                break;
                            case DROPPED_DELIVERING:
                                obj.put("position_x", 1);
                                break;
                            case DROPPED_MOVING:
                                obj.put("position_x", 2);
                                break;
                            default:
                                obj.put("position_x", 0);
                        }
                    }
                    arr.put(obj);
                }

                // post game
                JSONObject obj = new JSONObject();
                obj.put("start_time", postGameObject.climb_time);
                // TODO make it so there is an option for no climb
                if (postGameObject.climbType == PostGameObject.ClimbType.SUCCESS) {
                    obj.put("objective_id", climbID);
                    obj.put("success", true);
                } else if (postGameObject.climbType == PostGameObject.ClimbType.FAIL){
                    obj.put("objective_id", climbID);
                    obj.put("success", false);
                } else {
                    obj.put("objective_id", noClimbID);
                    obj.put("success", false);
                }
                arr.put(obj);
                jsonObject.put("events", arr);

                jsonObject.put("general_notes", postGameObject.notes);
                jsonObject.put("time_dead", postGameObject.time_dead);

            } catch (JSONException e) {
                Log.d("JSON error :( - ", e.toString());
            }

            return jsonObject;
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
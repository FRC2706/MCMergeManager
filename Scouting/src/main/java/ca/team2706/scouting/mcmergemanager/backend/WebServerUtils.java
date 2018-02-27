package ca.team2706.scouting.mcmergemanager.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.backend.dataObjects.PostThread;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoLineCrossEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoMalfunctionEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.ClimbEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubeDroppedEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class used to communicate with the webserver
 */

public class WebServerUtils {


    public static final OkHttpClient client = new OkHttpClient();
    public static final String SERVER_URL = "http://beancode.org:9999/";


    // Post a comment to the server, returns true if successful
    public static boolean postCommentToServer(int team_number, String message) {
        Request request = new Request.Builder()
                .url(SERVER_URL + "comments/create?team=" + team_number + "&body=" + message)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // If server returns succes then the comment posted
            if (new String(response.body().string()).equals("success")) {
                return true;
            }
        } catch (IOException e) {
            Log.d("Okhttp3 error", e.toString());
        }

        return false;
    }

    // Post a match event to the server
    public static boolean postMatchEvent(String matchKey, String teamNumber, String goal, String success,
                                         String startTime, String endTime, String extra) {
        Request request = new Request.Builder()
                .url(SERVER_URL + "events/create?match=" + matchKey +
                        "&team=" + teamNumber +
                        "&goal=" + goal +
                        "&success=" + success +
                        "&start=" + startTime +
                        "&end=" + endTime +
                        "&extra=" + extra)
                .build();

        long time = System.currentTimeMillis();
        try {
            Response response = client.newCall(request).execute();

            // If server returns success then the comment posted
            System.out.println(System.currentTimeMillis() - time);
            if(response.body().string().equals("success"))
                return true;
        } catch(IOException e) {
            Log.d("Okhttp3 error", e.toString());
        }

        return false;
    }

    // Returns the contents of a webserver at a given url
    public static String getDataFromServer(String url) {
        Request request = new Request.Builder()
                .url(SERVER_URL + url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            Log.d("Okhttp error", e.toString());
        }

        return "";
    }

    // Get a list of teams on the webserver
    public static JSONArray getTeamList() {
        try {
            return new JSONArray(getDataFromServer("teams/list"));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Get the details of a certain team
    public static JSONObject getTeamFromServer(int team_number) {
        try {
            return new JSONObject(getDataFromServer("teams/show?team=" + team_number));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Get a list of the competitions
    public static JSONArray getCompetitionList() {
        try {
            return new JSONArray(getDataFromServer("competitions/list"));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Get the details of a certain competition
    public static JSONObject getCompetitonFromServer(String competiton_id) {
        try {
            return new JSONObject(getDataFromServer("competitions/show?competition=" + competiton_id));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Get a match from the match number and competition id
    public static JSONObject getMatchFromServer(String competition_id, int match_number) {
        try {
            return new JSONObject(getDataFromServer("matches/show?competition=" + competition_id + "&match=" + match_number));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }


    public static void uploadUnsyncedMatches() {
        // Networking so needs to be in own thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Load the files
                MatchData matchData = new MatchData();


                // Loop through all matches and events
                for(MatchData.Match match : matchData.matches) {
                    // Get the match key
                    String matchKey = getMatchKey(match.preGameObject.matchNumber);

                    // If the match key is null then something didn't work, therefore don't continue in current loop
                    if(matchKey == null)
                        continue;

                    ArrayList<PostThread> threads = new ArrayList<PostThread>();

                    for(Event event : match.teleopScoutingObject.getEvents()) {
                        if(event instanceof AutoCubePickupEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.AUTO_CUBE_PICKUP_ID, null,
                                    Double.toString(event.timestamp), null, ((AutoCubePickupEvent) event).pickupType.toString()));
                        } else if(event instanceof AutoCubePlacementEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.AUTO_CUBE_PLACE_ID, null,
                                    Double.toString(event.timestamp), null, ((AutoCubePlacementEvent) event).placementType.toString()));
                        } else if(event instanceof AutoLineCrossEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.AUTO_LINE_CROSS_ID,
                                    Boolean.toString(((AutoLineCrossEvent) event).crossedAutoLine), Double.toString(event.timestamp), null, null));
                        } else if(event instanceof AutoMalfunctionEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.AUTO_MALFUNCTION_ID,
                                    Boolean.toString(((AutoMalfunctionEvent) event).autoMalfunction), Double.toString(event.timestamp), null, null));
                        } else if(event instanceof ClimbEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.CLIMB_ID, null,
                                    Double.toString(event.timestamp), Double.toString(((ClimbEvent) event).climb_time), ((ClimbEvent) event).climbType.toString()));
                        } else if(event instanceof CubeDroppedEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.CUBE_DROPPED_ID, null,
                                    Double.toString(event.timestamp), null, ((CubeDroppedEvent) event).dropType.toString()));
                        } else if(event instanceof CubePickupEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.CUBE_PICKUP_ID, null,
                                    Double.toString(event.timestamp), null, ((CubePickupEvent) event).pickupType.toString()));
                        } else if(event instanceof CubePlacementEvent) {
                            threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), MatchData.CUBE_PLACE_ID, null,
                                    Double.toString(event.timestamp), null, ((CubePlacementEvent) event).placementType.toString()));
                        }
                    }

                    // Wait for threads
                    try {
                        for (PostThread t : threads) {
                            t.join();

                            // TODO:
                        }
                    } catch (InterruptedException e) {
                        e.toString();
                    }
                }
            }
        }).start();
    }

    public static String getMatchKey(int matchNumber) {
        JSONArray arr = new JSONArray(BlueAllianceUtils.getJsonObject());

        // Find the match number
        for(int i = 0; i < arr.length(); i++) {
            if(arr.getJSONObject(i).getString("match_number").equals(Integer.toString(matchNumber)))
                return arr.getJSONObject(i).getString("key");
        }

        return null;
    }
}

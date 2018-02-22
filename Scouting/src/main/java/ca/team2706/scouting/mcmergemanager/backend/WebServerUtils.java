package ca.team2706.scouting.mcmergemanager.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

        try {
            Response response = client.newCall(request).execute();

            // If server returns success then the comment posted
            String responseString = response.body().string();
            System.out.println(responseString);
            if(responseString.equals("success"))
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
}

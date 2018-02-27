package ca.team2706.scouting.mcmergemanager.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.DataRequester;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by daniel on 15/11/17.
 *
 * Used to get data from the blue alliance servers, uses v3 of the api.
 * All methods are static so you do not need to create an object.
 */

public class BlueAllianceUtils {

    private static final String BASE_URL = "http://www.thebluealliance.com/api/v3/";
    private static final String AUTH_KEY = "8GLetjJXz2pNCZuY0NnwejAw0ULn9TzbsYeLkYyzeKwDeRsK9MiDnxEGgy6UksW1";

    private Activity mActivity;

    private static boolean sPermissionsChecked = false;

    public static boolean checkInternetPermissions(Activity activity) {
        if (activity == null)
            return false;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.INTERNET}, 123);

            // check if they clicked Deny
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED)
                sPermissionsChecked = false;
        }

        // TODO: this doesnt make sense???
        sPermissionsChecked = true;
        return sPermissionsChecked;
    }

    // Gets the list of all teams attending the event
    public static void fetchTeamsRegisteredAtEvent(final DataRequester requester) {
        // Check if device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return;

        new Thread() {
            public void run() {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                String TBA_Event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

                Request request = new Request.Builder()
                        .url(BASE_URL + "event/" + TBA_Event + "/teams/keys")
                        .header("X-TBA-Auth-Key", AUTH_KEY)
                        .build();
                MatchSchedule schedule;

                try {
                    Response response = WebServerUtils.client.newCall(request).execute();

                    schedule = new MatchSchedule();
                    schedule.addToListOfTeamsAtEvent(response.body().string());

                    response.close();
                } catch (IOException e) {
                    Log.d("Error getting teams: ", e.toString());
                    return;
                }

                requester.updateMatchSchedule(schedule);
            }
        }.start();
    }

    // Used to get data to show the upcoming match schedules and the past matches with the score
    public static void fetchMatchScheduleAndResults(final DataRequester dataRequester) {
        // check if we have internet connectivity
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) { // not connected to the internet
            return;
        }

        new Thread() {
            public void run() {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
                String TBA_event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

                Request request = new Request.Builder()
                        .url(BASE_URL + "event/" + TBA_event + "/matches/simple")
                        .header("X-TBA-Auth-Key", AUTH_KEY)
                        .build();
                MatchSchedule schedule;

                try {
                    Response response = WebServerUtils.client.newCall(request).execute();

                    schedule = MatchSchedule.newFromJsonSchedule(response.body().string());
                } catch (IOException e) {
                    Log.d("Error match scedule", e.toString());
                    return;
                }

                dataRequester.updateMatchSchedule(schedule);
            }
        }.start();
    }

    // Returns a string of a piece of data
    // key - is the key of the piece of data being looked for
    // extraurl - the extra part of the string needed to get the response from server
    public static String getBlueAllianceData(String key, String extraUrl) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String TBA_event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

        Request request = new Request.Builder()
                .url(BASE_URL + extraUrl)
                .header("X-TBA-Auth-Key", AUTH_KEY)
                .build();

        try {
            Response response = WebServerUtils.client.newCall(request).execute();

            JSONObject json = new JSONObject(response.body().string());
            return json.getString(key);
        } catch (JSONException e) {
            Log.d("Json error", e.toString());
        } catch (IOException e) {
            Log.d("okhttp error", e.toString());
        }

        return null;
    }

    // Returns the past competition data for a team
    // TODO: There must be an easier way to actually get this data, seems to inefficient to me
    public static String getBlueAllianceDateForTeam(int team_number) {
        // Used to build the final string
        StringBuilder sb = new StringBuilder();

        // check if we have internet connectivity
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) { // not connected to the internet
            return null;
        }

        // First get the keys of every event the team has been to
        Request request = new Request.Builder()
                .url(BASE_URL + "team/frc" + team_number + "/events/keys")
                .header("X-TBA-Auth-Key", AUTH_KEY)
                .build();

        JSONArray keys;
        try {
            Response responseKeys = WebServerUtils.client.newCall(request).execute();

            keys = new JSONArray(responseKeys.body().string());
        } catch (IOException e) {
            Log.d("OKKHTP error", e.toString());
            return "FAiled";
        } catch (JSONException e) {
            Log.d("Json errer", e.toString());
            return "Failed";
        }

        // Loop though every event and add data to string buidler
        for (int i = 0; i < keys.length(); i++) {

            try {
                // Only look for events since 2012
                if (keys.getString(i).charAt(2) != '1' || keys.getString(i).charAt(3) < '4')
                    continue;

                // Get the rank of the team
                request = new Request.Builder()
                        .url(BASE_URL + "team/frc" + team_number + "/event/" + keys.getString(i) + "/status")
                        .header("X-TBA-Auth-Key", AUTH_KEY)
                        .build();

                Response eventStatusResponse = WebServerUtils.client.newCall(request).execute();

                // Find the data in the data gotten from tbav3
                JSONObject jsonEventStatus = new JSONObject(eventStatusResponse.body().string());
                JSONObject qualStats = jsonEventStatus.getJSONObject("qual");
                JSONObject ranking = qualStats.getJSONObject("ranking");

                sb.append(ranking.getString("rank") + "/" + qualStats.getString("num_teams") + " - ");

                // Get the event location
                request = new Request.Builder()
                        .url(BASE_URL + "event/" + keys.getString(i) + "/simple")
                        .header("X-TBA-Auth-Key", AUTH_KEY)
                        .build();

                Response teamNameResponse = WebServerUtils.client.newCall(request).execute();

                JSONObject json = new JSONObject(teamNameResponse.body().string());
                sb.append(json.getString("name") + "\n");
            } catch (IOException e) {
                Log.d("OKKHTP error", e.toString());
            } catch (JSONException e) {
                Log.d("JSON - probably-fine", e.toString());
            }
        }

        return sb.toString();
    }

    // Used to get a list of events for the user to choose which event they are at
    public static String getEventKeysFromYear(int year) {
        StringBuilder sb = new StringBuilder();

        // check if we have internet connectivity
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) { // not connected to the internet
            return null;
        }

        // Get a list of the events for a certain year
        Request request = new Request.Builder()
                .url(BASE_URL + "events/" + year + "/simple")
                .header("X-TBA-Auth-Key", AUTH_KEY)
                .build();

        try {
            Response response = WebServerUtils.client.newCall(request).execute();

            // Add the json file string to be sent back to settings page
            sb.append(response.body().string());
        } catch(IOException e) {
            Log.d("OKHTTP err0r ", e.toString());
        }

        return sb.toString();
    }

    public static String getJsonObject(String url) {
        // check if we have internet connectivity
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) { // not connected to the internet
            return null;
        }

        // Get a list of the events for a certain year
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .header("X-TBA-Auth-Key", AUTH_KEY)
                .build();

        try {
            Response response = WebServerUtils.client.newCall(request).execute();

            // Add the json file string to be sent back to settings page
            return response.body().string();
        } catch(IOException e) {
            Log.d("OKHTTP err0r ", e.toString());
        }

        return "";
    }
}

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
 */

public class BlueAllianceUtilsV3 {

    public static final String BASEURL = "http://www.thebluealliance.com/api/v3/";
    public static final String AUTHKEY = "8GLetjJXz2pNCZuY0NnwejAw0ULn9TzbsYeLkYyzeKwDeRsK9MiDnxEGgy6UksW1";

    private Activity mActivity;

    private static boolean sPermissionsChecked = false;
    private static final OkHttpClient client = new OkHttpClient();

    /* ~~~ Constructor ~~~ */
    public BlueAllianceUtilsV3(Activity activity) {
        mActivity = activity;
//        client = new OkHttpClient();

        checkInternetPermissions(activity);
    }

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
                        .url(BASEURL + "event/" + TBA_Event + "/teams/keys")
                        .header("X-TBA-Auth-Key", AUTHKEY)
                        .build();
                MatchSchedule schedule;

                try {
                    Response response = client.newCall(request).execute();

                    schedule = new MatchSchedule();
                    schedule.addToListOfTeamsAtEvent(response.body().string());

                    response.close();
                } catch (IOException e) {
                    Log.d("Error getting teams: ", e.toString());
                    return;
                }

                System.out.println(schedule.toString());
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
                        .url(BASEURL + "event/" + TBA_event + "/matches/simple")
                        .header("X-TBA-Auth-Key", AUTHKEY)
                        .build();
                MatchSchedule schedule;

                try {
                    Response response = client.newCall(request).execute();

                    schedule = MatchSchedule.newFromJsonSchedule(response.body().string());
                } catch (IOException e) {
                    Log.d("Error match scedule", e.toString());
                    return;
                }

                System.out.println(schedule.toString());
                dataRequester.updateMatchSchedule(schedule);
            }
        }.start();
    }

    // Returns a string of a piece of data
    // key - is the key of the piece of data being looked for
    // extraurl - the extra part of the string needed to get the response from server
    public static String getBlueAllicanceData(String key, String extraUrl) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String TBA_event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

        Request request = new Request.Builder()
                .url(BASEURL + extraUrl)
                .header("X-TBA-Auth-Key", AUTHKEY)
                .build();

        try {
            Response response = client.newCall(request).execute();

            JSONObject json = new JSONObject(response.body().string());
            return json.getString(key);
        } catch (JSONException e) {
            Log.d("Json error", e.toString());
        } catch (IOException e) {
            Log.d("okhttp error", e.toString());
        }

        return null;
    }

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

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String TBA_event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

        // First get the keys of every event the team has been to
        Request request = new Request.Builder()
                .url(BASEURL + "team/frc" + team_number + "/events/keys")
                .header("X-TBA-Auth-Key", AUTHKEY)
                .build();

        JSONArray keys;
        try {
            Response responseKeys = client.newCall(request).execute();

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
                if(keys.getString(i).charAt(2) != '1' || keys.getString(i).charAt(3) < '1')
                    continue;

                // Check if the event is in 2018
                request = new Request.Builder()
                        .url(BASEURL + "team/frc" + team_number + "/event/" + keys.getString(i) + "/status")
                        .header("X-TBA-Auth-Key", AUTHKEY)
                        .build();

                Response response = client.newCall(request).execute();

                // Find the data in the
                JSONObject json = new JSONObject(response.body().string());
                JSONObject qualStats = json.getJSONObject("qual");
                JSONObject ranking = qualStats.getJSONObject("ranking");

                sb.append(ranking.getString("rank") + "/" + qualStats.getString("num_teams") + " - " + keys.getString(i) + "\n");
            } catch(IOException e) {
                Log.d("OKKHTP error", e.toString());
            } catch(JSONException e) {
                Log.d("JSON - probably-fine", e.toString());
            }
        }

        return sb.toString();
    }
}

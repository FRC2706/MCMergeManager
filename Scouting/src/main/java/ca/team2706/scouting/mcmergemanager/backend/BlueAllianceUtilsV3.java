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

    public static void fetchTeamsRegisteredAtEvent(final DataRequester requester) {
        // Check if device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork == null)
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
                } catch(IOException e) {
                    Log.d("Error getting teams: ", e.toString());
                    return;
                }

                System.out.println(schedule.toString());
                requester.updateMatchSchedule(schedule);
            }
        }.start();
    }


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
                        .url(BASEURL + "event/" + TBA_event + "/matches")
                        .header("X-TBA-Auth-Key", AUTHKEY)
                        .build();
                MatchSchedule schedule;

                try {
                    Response response = client.newCall(request).execute();

                    schedule = MatchSchedule.newFromJsonSchedule(response.body().string());
                } catch(IOException e) {
                    Log.d("Error match scedule", e.toString());
                    return;
                }

                System.out.println(schedule.toString());
                dataRequester.updateMatchSchedule(schedule);
            }
        }.start();
    }

    public static void test() {
        new Thread() {
            public void run() {

                OkHttpClient client = new OkHttpClient();

                // Build the request for the file
                Request request = new Request.Builder()
                        .url(BASEURL + "/status")
                        .header("X-TBA-Auth-Key", AUTHKEY)
                        .build();

                try {
                    // Send request
                    Response response = client.newCall(request).execute();

                    System.out.println(response.body().string());

                    response.close();
                } catch(IOException e) {
                    Log.d("Error getting tbaV3", e.toString());
                }
            }
        }.start();
    }

}

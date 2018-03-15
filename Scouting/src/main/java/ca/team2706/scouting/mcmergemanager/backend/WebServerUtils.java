package ca.team2706.scouting.mcmergemanager.backend;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.PostThread;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.PullThread;
import ca.team2706.scouting.mcmergemanager.gui.MainActivity;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject;
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
                .url(SERVER_URL + "comments/create.json?team=" + team_number + "&body=" + message)
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

//        System.out.println(request.url());    <-- Used for testing

        try {
            Response response = client.newCall(request).execute();

            // If server returns success then the comment posted
            if (response.body().string().equals("success"))
                return true;
        } catch (IOException e) {
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
            return new JSONObject(getDataFromServer("competitions/show.json?competition=" + competiton_id));
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    // Get a match from the match number and competition id
    public static JSONObject getMatchFromServer(String competition_id, String match_number) {
        try {
            return new JSONObject(getDataFromServer("matches/show.json?competition=" + competition_id + "&match=" + match_number));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void uploadMatch(final MatchData.Match match) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the match key
                String matchKey = getMatchKey(match.preGameObject.matchNumber);

                // If the match key doesnt get anything that just set the match to be reuploaded later, and go to next match
                if (matchKey == null) {
                    FileUtils.saveUnpostedMatch(match.toJsonObject());
                    return;
                }

                // JSONObject
                ArrayList<Event> unpostedEvents = new ArrayList<>();

                // If the match key is null then something didn't work, therefore don't continue in current loop
                ArrayList<PostThread> threads = new ArrayList<PostThread>();

                // If -1 then it is a field watcher match
                if(match.preGameObject.teamNumber != -1) {
                    for (Event event : match.autoScoutingObject.getEvents()) {
                        threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), event));
                    }

                    for (Event event : match.teleopScoutingObject.getEvents()) {
                        threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), event));
                    }
                } else {
                    // Use the blue1 team to upload the match. Will be filtered out once downloaded
                    String tempTeamNumber = BlueAllianceUtils.getBlueOneTeamForMatch(matchKey);

                    for(Event event : match.autoScoutingObject.getEvents()) {
                        threads.add(new PostThread(matchKey, tempTeamNumber, event));
                    }

                    for(Event event : match.fieldWatcherObject.getEvents()) {
                        threads.add(new PostThread(matchKey, tempTeamNumber, event));
                    }
                }

                // Run all the threads
                for (PostThread t : threads) {
                    t.run();
                }

                // Wait for threads
                try {
                    for (PostThread t : threads) {
                        t.join();

                        // If not posted then add to stuff that needs to be reposted
                        if (!t.getPosted()) {
                            unpostedEvents.add(t.getEvent());
                        }
                    }
                } catch (InterruptedException e) {
                    e.toString();
                }


                // If there are any events in the aray that some stuff failed, save in order to retry later
                if (unpostedEvents.size() > 0) {
                    TeleopScoutingObject unpostedTeleopObject = new TeleopScoutingObject();
                    for (Event e : unpostedEvents)
                        unpostedTeleopObject.add(e);

                    // Autoscouting is null because I can just save all events in teleop array

                    FileUtils.saveUnpostedMatch(new MatchData.Match(match.preGameObject, new AutoScoutingObject(), unpostedTeleopObject).toJsonObject());
                }
            }
        }).start();
    }

    public static void uploadUnsyncedMatches() {
        if(!BlueAllianceUtils.isConnected(MainActivity.me))
            return;

        // Used for saving matches that failed to post
        JSONArray unpostedMatches = new JSONArray();

        // Load the files
        JSONArray matchData = FileUtils.readUnpostedMatches();

        // Loop through all matches and events
        for (int i = 0; i < matchData.length(); i++) {
            MatchData.Match match = null;
            try {
                match = new MatchData.Match(matchData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (match == null) {
//                unpostedMatches.put(match.toJsonObject());
                continue;
            }

            // JSONObject
            ArrayList<Event> unpostedEvents = new ArrayList<>();

            // Get the match key which is required for posting
            String matchKey = getMatchKey(match.preGameObject.matchNumber);

            // If the match key doesnt get anything that just set the match to be reuploaded later, and go to next match
            if (matchKey == null) {
                unpostedMatches.put(match.toJsonObject());
                continue;
            } else if(matchKey.equals("WrongMatchNumber")) {
                FileUtils.saveWrongMatchNumberMatch(match.toJsonObject());
                continue;
            }

            ArrayList<PostThread> threads = new ArrayList<PostThread>();

            // If -1 then it is a field watcher match
            if(match.preGameObject.teamNumber != -1) {
                for (Event event : match.autoScoutingObject.getEvents()) {
                    threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), event));
                }

                for (Event event : match.teleopScoutingObject.getEvents()) {
                    threads.add(new PostThread(matchKey, Integer.toString(match.preGameObject.teamNumber), event));
                }
            } else {
                // Use the blue1 team to upload the match. Will be filtered out once downloaded
                String tempTeamNumber = BlueAllianceUtils.getBlueOneTeamForMatch(matchKey);

                for(Event event : match.autoScoutingObject.getEvents()) {
                    threads.add(new PostThread(matchKey, tempTeamNumber, event));
                }

                for(Event event : match.fieldWatcherObject.getEvents()) {
                    threads.add(new PostThread(matchKey, tempTeamNumber, event));
                }
            }

            // Run all the threads
            for (PostThread t : threads) {
                t.run();
            }

            // Wait for threads
            try {
                for (PostThread t : threads) {
                    t.join();

                    // If not posted then add to stuff that needs to be reposted
                    if (!t.getPosted()) {
                        unpostedEvents.add(t.getEvent());
                    }
                }
            } catch (InterruptedException e) {
                e.toString();
            }

            // If there are any events in the aray that some stuff failed, save in order to retry later
            if (unpostedEvents.size() > 0) {
                TeleopScoutingObject unpostedTeleopObject = new TeleopScoutingObject();
                for (Event e : unpostedEvents)
                    unpostedTeleopObject.add(e);

                // Autoscouting is null because I can just save all events in teleop array
                unpostedMatches.put(new MatchData.Match(match.preGameObject, new AutoScoutingObject(), unpostedTeleopObject).toJsonObject());
            }
        }

        // Delete the file so we don't get duplicates
        FileUtils.deleteUnsyncedMatches();

        // Check if there are any failed matches, then save
        if (unpostedMatches.length() > 0) {
            FileUtils.saveUnpostedMatches(unpostedMatches);
        }

        System.out.println("Done posting matches.");

        // Scan the directory tree
        FileUtils.checkLocalFileStructure(MainActivity.me);
    }

    public static String getMatchKey(int matchNumber) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String TBA_Event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");
//
//        try {
//            String s = BlueAllianceUtils.getObject("team/frc" + teamNumber + "/event/" + TBA_Event + "/matches/simple");
//            if (s.length() == 0)
//                return null;
//            JSONArray arr = new JSONArray(s);
//
//            // Find the match number
//            for (int i = 0; i < arr.length(); i++) {
//                if (arr.getJSONObject(i).getString("match_number").equals(Integer.toString(matchNumber))) {
//                    return arr.getJSONObject(i).getString("key");
//                }
//            }
//
//            // If didn't find the match key then person entered wrong match number
//            return "WrongMatchNumber";
//        } catch (JSONException e) {
//            Log.d("Err match key", e.toString());
//        }

        return TBA_Event + "_qm" + matchNumber;
    }

    public static void syncMatchData() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String TBA_Event = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");

        // Get the competition form the server
        JSONObject competition = getCompetitonFromServer(TBA_Event);
        if(competition == null)
            return;
        JSONArray matches = new JSONArray();

        ArrayList<PullThread> threads = new ArrayList<>();
        try {
            // Create threads and run them
            for (int i = 0; i < competition.getJSONArray("matches").length(); i++) {
                threads.add(new PullThread(TBA_Event, ((JSONObject) competition.getJSONArray("matches").get(i)).getString("key")));
                threads.get(i).run();
            }

            // Wait for threads to stop
            for (PullThread t : threads) {
                t.join();

                // Save the match data
                FileUtils.saveServerData(new JSONObject(t.getResponse()));
            }

            Log.d("Syncing match data:", "done");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done syncing matches");
    }

    public static void syncComments() {
        if(BlueAllianceUtils.isConnected(MainActivity.me))
            return;
    }
}

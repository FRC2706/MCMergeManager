package ca.team2706.scouting.mcmergemanager.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentList;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.NoteObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.RepairTimeObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.PhotoRequester;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelPickupEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.MatchData;

/**
 * This is a helper class to hold common code for accessing shared scouting data files.
 * This class takes care of keeping a local cache, syncing to the server, and (eventually) sharing with other bluetooth-connected devices also running the app.
 * <p/>
 * Created by Mike Ounsworth
 */
public class FileUtils {

    public static String sLocalToplevelFilePath;
    public static String sLocalEventFilePath;
    public static String sLocalTeamPhotosFilePath;
    public static String sLocalCommentFilePath;

    public static String sRemoteTeamPhotosFilePath;

    /* Static initializer */
    static {
        updateFilePathStrings();
    }

    public static void updateFilePathStrings() {
        // store string constants and preferences in member variables just for cleanliness
        // (since the strings are `static`, when any instances of FileUtils update these, all instances will get the updates)
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        sLocalToplevelFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + App.getContext().getString(R.string.FILE_TOPLEVEL_DIR);
//        sLocalToplevelFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.getContext().getString(R.string.FILE_TOPLEVEL_DIR);
        sLocalEventFilePath = sLocalToplevelFilePath + "/" + SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");
        sLocalTeamPhotosFilePath = sLocalToplevelFilePath + "/" + "Team Photos";
        sLocalCommentFilePath = sLocalToplevelFilePath + "/" + "TeamComments";

        sRemoteTeamPhotosFilePath = "/" + App.getContext().getString(R.string.FILE_TOPLEVEL_DIR) + "/" + "Team Photos";
    }

    public enum FileType {
        SYNCHED, UNSYNCHED
    }

    /**
     * private constructor -- this is a static class, it should not be instantiated
     **/
    private FileUtils() {
        // empty
    }

    /**
     * Checks if we have the permission read / write to the internal USB STORAGE,
     * requesting that permission if we do not have it.
     *
     * @param activity The activity on which to pop up permission request dialogs. May be null, in
     *                 which case nothing is done and false is returned.
     * @return whether or not we have the STORAGE permission.
     */
    @Nullable
    public static boolean checkFileReadWritePermissions(Activity activity) {
        if (activity == null)
            return false;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

            // check if they clicked Deny
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                return false;
        }


        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);

            // check if they clicked Deny
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    /**
     * This checks the local file system for the appropriate files and folders, creating them if they
     * are missing.
     */
    public static void checkLocalFileStructure(Activity activity) {
        if (activity == null)
            return;

        // check for STORAGE permission
        if (!checkFileReadWritePermissions(activity))
            return;

        makeDirectory(sLocalToplevelFilePath);
        makeDirectory(sLocalEventFilePath);
        makeDirectory(sLocalTeamPhotosFilePath);
        makeDirectory(sLocalCommentFilePath);

        new Thread() {
            public void run() {
                if (!directoryTreeScanRunning) {
                    directoryTreeScanRunning = true;
                    scanDirectoryTree(sLocalToplevelFilePath);
                    directoryTreeScanRunning = false;
                }
            }
        }.start();
    }


    /**
     * A flag to make sure only one directory tree scan runs at a time.
     * Note that the 'volatile' keyword is a cheap hack to make sure the value is sync'ed across
     * multiple threads.
     */
    private static volatile boolean directoryTreeScanRunning = false;

    /*
     *  This scans the photos dir and adds them to the OS photos gallery.
     *  This is needed to get the photos to show up over a USB connection
     *
     *  Also downsizes photos if they are over 640 pixels on the width
     *  or the height.
     */
    private static void scanDirectoryTree(String directoryPath) {

//        Log.d(App.getContext().getResources().getString(R.string.app_name), "Scanning directory: " + directoryPath);


        File file = new File(directoryPath);
        if (file.isDirectory()) {

            // Call the system media scanner on each file inside
            File[] files = file.listFiles();

            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    // Recurse!
                    scanDirectoryTree(subFile.getAbsolutePath());
                } else {
                    try {
                        // TODO: 18/03/17 downsize images if too large
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        //Returns null, sizes are in the options variable
                        options.inJustDecodeBounds = true;  // only load image metadata, not the image itself
                        BitmapFactory.decodeFile(subFile.getAbsolutePath(), options);

                        final int height = options.outHeight;
                        final int width = options.outWidth;

                        // if the size of the photo is too large, then downsize
                        if (options != null && height > 640 && width > 640) {
                            System.out.println("foto is too large" + subFile.getAbsolutePath());
                            int inSampleSize = 1; // divides image resolution by this

                            // increases the samplesize until the future resolution is under 640 pixels
                            while ((height / inSampleSize) > 640 &&
                                    (width / inSampleSize) > 640) {
                                inSampleSize *= 2;
                            }

                            options.inJustDecodeBounds = false;

                            // tries to save the image that is downsized
                            Bitmap fullImg = BitmapFactory.decodeFile(subFile.getAbsolutePath());
                            Bitmap out = Bitmap.createScaledBitmap(fullImg, fullImg.getWidth() / inSampleSize,
                                    fullImg.getHeight() / inSampleSize, false);

                            File newFile = new File(subFile.getAbsolutePath());
                            FileOutputStream fOut = new FileOutputStream(newFile);
                            out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.flush();
                            fOut.close();
                            fullImg.recycle();
                            out.recycle();

                        }
                    } catch (Exception e) {
                        Log.e("MCMergeManager", "Error parsing or downsizing image:", e);
                    }


                    // Force the midea scanner to scan this file so it shows up from a PC over USB.
                    App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(subFile)));
                }
            }
        } else {
            // in case there's a regular file there with the same name
            file.delete();
            // create it
            file.mkdirs();
        }
    }

    private static void makeDirectory(String directoryName) {

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Making directory: " + directoryName);


        File file = new File(directoryName);
        if (!file.isDirectory()) {
            // in case there's a regular file there with the same name
            file.delete();
            // create it
            file.mkdir();
        }
    }

    /**
     * Take one match of data and stick it at the end of the match data file.
     * <p>
     * Data format:
     * "matchNo<int>,teamNo<int>,isSpyBot<boolean>,reached<boolean>,{autoDefenseBreached<int>;...},{{autoBallShot_X<int>;autoBallShot_Y<int>;autoBallShot_time<.2double>;autoBallshot_which<int>}:...},{teleopDefenseBreached<int>;...},{{teleopBallShot_X<int>;teleopBallShot_Y<int>;teleopBallShot_time<.2double>;teleopBallshot_which<int>}:...},timeDefending<,2double>,{{ballPickup_selection<int>;ballPickup_time<,2double>}:...},{{scaling_time<.2double>;scaling_comelpted<int>}:...},notes<String>,challenged<boolean>,timeDead<int>"
     * <p>
     * Or, in printf / format strings:
     * "%d,%d,%b,%b,{%d;...},{{%d:%d:%.2f:%d};...},{%d;...},{{%d:%d:%.2f:%d};...},%,2f,{{%d;%,2f}:...},{{%.2f;%d}:...},%s,%b,%d"
     */
    public static void appendToMatchDataFile(MatchData.Match match, FileType fileType) {

        //TODO: #76, make sure this actually works

        String outFileName;
        File outfile;
        if (fileType == FileType.SYNCHED) {
            outFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileName);

            Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving match data to file: " + outFileName);

            outfile = new File(outFileName);
            try {
                // converts match to json, and then uses json.toString method to save in file
                // create the file path, if it doesn't exist already.
                (new File(outfile.getParent())).mkdirs();

                BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
                bw.append(match.toJson().toString() + "\n");
                bw.flush();
                bw.close();

                // Force the midea scanner to scan this file so it shows up from a PC over USB.
                App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outfile)));
            } catch (IOException e) {
                Log.d("synced file", e.toString());
            }
        } else if (fileType == FileType.UNSYNCHED) {
            outFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED);

            Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving match data to file: " + outFileName);

            outfile = new File(outFileName);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
                bw.append(match.toJson().toString() + "\n");
                bw.flush();
                bw.close();
            } catch (IOException e) {
                Log.d("unsynced file", e.toString());
            }
        }

    }


    /**
     * Clears the file containing unsynched Team Data.
     * Call this after a successful sync with the db server.
     *
     * @return whether or not the delete was successful.
     */
    public boolean clearUnsyncedMatchScoutingDataFile() {
        File file = new File(App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED));
        return file.delete();
    }

    /**
     * Load the entire file of match data into Objects.
     */
    public static MatchData loadMatchDataFile() {
        return loadMatchDataFile(FileType.SYNCHED);
    }


    public static MatchData loadMatchDataFile(FileType fileType) {

        MatchData matchData = new MatchData();
        List<JSONObject> matchJson = new ArrayList<>();

        String inFileName;
        switch (fileType) {
            case UNSYNCHED:
                inFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED);
                break;
            case SYNCHED:
            default:
                inFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileName);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line = br.readLine();

            while (line != null) {
                // braces are for human readibility, but make parsing harder
                matchJson.add(new JSONObject(line));
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: " + e.toString());
            return null;
        }

        // parse all the matches into the MatchData object
        boolean parseFailure = false;
        for (JSONObject obj : matchJson) {

            try {
                MatchData.Match match = new MatchData.Match(obj);
                matchData.addMatch(match);
            } catch (Exception e) {
                Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: ", e);
                parseFailure = true;
                continue;
            }
        }
        if (parseFailure) {
            Toast.makeText(App.getContext(), "Warning: match data may be corrupted or malformed.", Toast.LENGTH_SHORT).show();
        }

        return matchData;
    }

    /**
     * Add a Note for a particular team.
     * <p/>
     * The intention of Notes is for the drive team to be able to read them quickly.
     * They should be short and fit on one line, so they will be truncated to 80 characters.
     */
    public static void addNote(int teamNumber, String note) {
        // TODO #41
    }

    /**
     * Retrieves all the notes for a particular team.
     *
     * @param teamNumber the team number you want notes for.
     * @return All the notes for this team concatenated into a single string, with each note beginning with a bullet "-",
     * and ending with a newline (except for the last one).
     */
    public static String getNotesForTeam(int teamNumber) {
        // TODO #41

        return "";
    }


    public static void appendToTeamDataFile(TeamDataObject teamDataObject) {

        String outFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.teamDataFileName);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team data to file: " + outFileName);

        File outfile = new File(outFileName);
        try {
            // create the file path, if it doesn't exist already.
            (new File(outfile.getParent())).mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append(teamDataObject.toString() + "\n");
            bw.flush();
            bw.close();

            // Force the midea scanner to scan this file so it shows up from a PC over USB.
            App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outfile)));
        } catch (IOException e) {
            Log.e("MCMergeManager", "appendToTeamDataFile could not save file.", e);
        }


        outFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.teamDataFileNameUNSYNCHED);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team data to file: " + outFileName);

        outfile = new File(outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append(teamDataObject.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {

        }
    }

    public static List<TeamDataObject> getRepairTimeObjects() {
        List<TeamDataObject> teamDataObjects = loadTeamDataFile();

        List<TeamDataObject> repairTimeObjectList = new ArrayList<>();

        if (teamDataObjects != null) {
            for (TeamDataObject teamDataObject : teamDataObjects) {
                if (teamDataObject instanceof RepairTimeObject)
                    repairTimeObjectList.add(teamDataObject);
            }
        }


        return repairTimeObjectList;
    }

    /**
     * Load data from the teamDataFile.
     */
    public static List<TeamDataObject> loadTeamDataFile() {
        return loadTeamDataFile(FileType.SYNCHED);
    }

    /**
     * Load data from the teamDataFile.
     */
    public static List<TeamDataObject> loadTeamDataFile(FileType fileType) {

        List<TeamDataObject> teamDataObjects = new ArrayList<>();

        // read the file
        String inFileName;
        switch (fileType) {
            case UNSYNCHED:
                inFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.teamDataFileNameUNSYNCHED);
                break;
            case SYNCHED:
            default:
                inFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.teamDataFileName);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line = br.readLine();

            while (line != null) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(line);
                } catch (JSONException e) {
                    continue;
                }

                TeamDataObject teamDataObject;
                TeamDataObject.TeamDataType teamDataType = TeamDataObject.TeamDataType.valueOf(jsonObject.getString(TeamDataObject.JSONKEY_TYPE));
                switch (teamDataType) {
                    case NOTE:
                        teamDataObjects.add(new NoteObject(jsonObject));
                        break;

                    case REPAIR_TIME:
                        teamDataObjects.add(new RepairTimeObject(jsonObject));
                        break;
                }

                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            Log.e(App.getContext().getResources().getString(R.string.app_name), "loadTeamDataFile:: " + e.toString());
            return teamDataObjects;
        }

        return teamDataObjects;
    }


    /**
     * This will reload the entire TeamDataFile from disk. If you already have a List<TeamDataObject>, then
     * it would be more efficient to pass it to filterTeamDataByTeam().
     */
    public static List<TeamDataObject> loadTeamDataForTeam(int teamNo) {

        return filterTeamDataByTeam(teamNo, loadTeamDataFile());
    }

    public static List<TeamDataObject> filterTeamDataByTeam(int teamNo, List<TeamDataObject> teamDataObjects) {

        List<TeamDataObject> toRet = new ArrayList<>();

        if (teamDataObjects == null)
            return toRet;

        for (TeamDataObject teamDataObject : teamDataObjects)
            if (teamDataObject.getTeamNo() == teamNo)
                toRet.add(teamDataObject);

        return toRet;
    }


    /**
     * We take photos by calling the system camera app and telling it where to save the photo.
     * This function will provide a file name in the correct location in the Team Photos/teamNumber directory.
     * <p/>
     * If a photos directory does not already exist for this team, this function will create one.
     *
     * @param teamNumber
     * @return Can return NULL if we do not have permission to write to STORAGE.
     */
    public static Uri getNameForNewPhoto(int teamNumber) {
        // check if a photo folder exists for this team, and create it if it does not.
        String dir = sLocalTeamPhotosFilePath + "/" + teamNumber + "/";
        scanDirectoryTree(dir);

        String fileName = dir + teamNumber + "_" + (new Date().getTime()) + ".jpg";
        File file = new File(fileName);

        return Uri.fromFile(file);
    }


    public static String getTeamPhotoPath(int teamNumber) {
        return sLocalTeamPhotosFilePath + "/" + teamNumber;
    }


    /**
     * This method will return you all locally-cached photos for the requested team.
     * It will then spawn a new background thread, and if <photo server> is available, it will sync the photos
     * for the requested team only and then notify the requesting activity that it has new photos.
     * <p/>
     * Since syncing photos with the server can take a few seconds, FileUtils.loadTeamPhotos() will immediately call
     * the PhotoRequester's updatePhotos(Bitmap[]) with whatever photos are locally cached for that team,
     * and if FileUtils is able to connect to the server then it will call it again after performing the sync.
     *
     * @param teamNumber The team whos photos we want to load.
     * @param requester  The activity that is requesting the photos. This activity's .updatePhotos(Bitmap[])
     *                   will be called with the loaded photos.
     * @return It will call requester.updatePhotos(Bitmap[]) with an array of Bitmaps containing all
     * photos for that team, or a zero-length array if no photos were found for that team.
     */
    public static void getTeamPhotos(int teamNumber, PhotoRequester requester) {

        /* First, return the requester any photos we have on the local drive */

        File photosDir = new File(sLocalTeamPhotosFilePath + "/" + teamNumber);

        // check if that folder exists
        if (!photosDir.isDirectory()) {
            // we have no photos for this team
            requester.updatePhotos(new Bitmap[0]);
            return;
        }


        File[] listOfFiles = photosDir.listFiles();
        ArrayList<Bitmap> arrBitmaps = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                // BitmapFactory will return `null` if the file cannot be parsed as an image, so no error-checking needed.
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                if (bitmap != null)
                    arrBitmaps.add(bitmap);
            }
            // else: if it's not a file, then what is it???? .... skip I guess
        }
        requester.updatePhotos(arrBitmaps.toArray(new Bitmap[arrBitmaps.size()]));

    }


    /*
        gets a competition data from the swagger server
        if compID is 0 will take from current event, if other number will get that competition
     */
    public static void getMatchesFromServer(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        final String url = "http://ftp.team2706.ca:3000/competitions/" + SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>") + "/matches.json";

        System.out.println(url);

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        saveJsonFile(response);
                        System.out.println(response.toString() + "\nWriting should have gone well");

                        loadMatchDataFile();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        error.printStackTrace();
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }


    /*
    @param CommentList Class
    Saves the JSON to a file
     */

    private static final String COMMENT_FILE_PATH= "comments.json";

    public static void saveTeamComments(CommentList commentList){

        JSONObject jsonObject = getTeamComments(commentList.getTeamNumber());

        if(jsonObject == null) {

            try {
                jsonObject = commentList.getJson();

            } catch (JSONException e) {
                Log.d("JSON Error", e.getMessage());
                Log.d("JSON might be null", "");

            }
        } else {
            try {
                CommentList cl = new CommentList(jsonObject);
                for (int i = 0; i < cl.getComments().size(); i++) {
                    commentList.addComment(cl.getComments().get(i));
                }
                jsonObject = commentList.getJson();

            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }


        }

        String outFileName = sLocalCommentFilePath + "/"  + commentList.getTeamNumber() + COMMENT_FILE_PATH;

        File file = new File(outFileName);

        try {
            file.delete();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            bw.append(jsonObject.toString());

            bw.flush();
            bw.close();
        } catch (IOException e) {
            Log.d("File writing error ", e.toString());
        } catch (NullPointerException e){
            // This should never be triggered, but is here for testing purposes
            Log.d("Json is null.", e.toString());

        }

        scanDirectoryTree(sLocalCommentFilePath);

    }


    public static JSONObject getTeamComments(int teamNumber){
        String json = null;
        JSONObject jsonObject = null;

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(sLocalCommentFilePath + "/"  + teamNumber + COMMENT_FILE_PATH));

            while ((json = bufferedReader.readLine()) != null) {
                stringBuilder.append(json);
            }

            jsonObject = new JSONObject(stringBuilder.toString());


        } catch (IOException e) {
            Log.d("IO Error", e.getMessage());
            return null;
        }
        catch (JSONException e){
            Log.d("JSON Error", e.getMessage());
            return null;

        }
        return jsonObject;

    }


    /* This returns the local match data for a given match and a given team
    @param teamNumber the given team number
    @param matchNumber the given matchNumber
     */
    public static JSONObject getJsonData(int teamNumber, int matchNumber){
        String json = null;
        JSONObject jsonObject = null;

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(sLocalEventFilePath + "/" +  teamNumber + "/" + "match" + matchNumber));

            while ((json = bufferedReader.readLine()) != null) {
                stringBuilder.append(json);
            }

            jsonObject = new JSONObject(stringBuilder.toString());

        } catch (IOException e) {
            Log.d("IO Error", e.getMessage());
            return null;
        }
        catch (JSONException e){
            Log.d("JSON Error", e.getMessage());
            return null;

        }
        return jsonObject;

    }


    public static void saveJsonData(JSONObject obj) {
        try {
            String filePath = sLocalEventFilePath + "/" + obj.getInt("team") + "/" + "match" +obj.getInt("match_number") + ".json";

            File file = new File(filePath);

            (new File(file.getParent())).mkdirs();

            file.delete();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            scanDirectoryTree(sLocalEventFilePath);

            bw.write(obj.toString());
            bw.flush();
            bw.close();

        } catch (JSONException  e) {
            Log.d("JSON error", e.getMessage());
        } catch (IOException e){
            Log.d("IOException", e.getMessage());

        }
    }




    private static void saveJsonFile(JSONArray jsonArray) {
        if (!clearTeamDataFile(FileType.SYNCHED)) {
            Log.d("Deleting file failed", "something probably went wrong");
        }

        String outFileName = sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileName);
        File file = new File(outFileName);


        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            for (int i = 0; i < jsonArray.length(); i++) {
                bw.append(jsonArray.get(i).toString() + "\n");
            }

            bw.flush();
            bw.close();
        } catch (JSONException e) {
            Log.d("JSON Exception: ", e.toString());
        } catch (IOException e) {
            Log.d("File writing error ", e.toString());
        }
    }

    private static boolean clearTeamDataFile(FileType fileType) {
        File file;
        switch (fileType) {
            case UNSYNCHED:
                file = new File(sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED));
                try {
                    file.delete();
                    file.createNewFile();
                    return true;
                } catch (IOException e) {
                    Log.d("Creating new file err", e.toString());
                }
                break;
            case SYNCHED:
                file = new File(sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileName));
                try {
                    file.delete();
                    file.createNewFile();
                    return true;
                } catch (IOException e) {
                    Log.d("Creating new file err", e.toString());
                }
                break;
        }
        return false;
    }

    /*
        Takes the selected event that you are at
     */
    public static void postMatchToServer(final Context context, final JSONObject jsonBody) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());

        final String url = "http://ftp.team2706.ca:3000/competitions/" + SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>") + "/matches.json";
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            // Prepares POST data...
            jsonBody.put("competition_id", SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set"));
            final String mRequestBody = jsonBody.toString();
            System.out.println(jsonBody.toString());
            // Volley request...
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY error from: " + url + " - ", error.toString());
                    FileUtils.appendToMatchDataFile(new MatchData.Match(jsonBody), FileType.UNSYNCHED);
                    error.printStackTrace();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                mRequestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(request);
        } catch (Exception e) {
            Log.d("Somethin interesting", e.toString());
        }
    }

    public static void postMatchToServer(final Context context, int compID) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        final String url = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_FTPHostname), "<Not Set>") + "/competitions/" + compID + "/matches.json";
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            // Prepares POST data...
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("competition_id", compID);
            jsonBody.put("number", 1);
            jsonBody.put("team_number", 1);
            JSONArray arr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("objective_id", FuelPickupEvent.objectiveId);
            arr.put(obj);
            jsonBody.put("events", arr);
            System.out.println(jsonBody.toString());
            final String mRequestBody = jsonBody.toString();
            // Volley request...
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY error from: " + url + " - ", error.toString());
                    error.printStackTrace();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                mRequestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void syncFiles(Context context) {

        MatchData matchData = loadMatchDataFile(FileType.UNSYNCHED);
        clearTeamDataFile(FileType.UNSYNCHED);

        // probably need to throw some sort of error catching magic
        if (matchData.matches != null)
            for (MatchData.Match match : matchData.matches) {
                postMatchToServer(context, match.toJson());
            }

        // delete file on phone and redownload
        getMatchesFromServer(context);
    }

    // Checks to see if a file with a certain filename exists
    public static boolean fileExists(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);

        if (file == null || !file.exists())
            return false;
        return true;
    }

    // Update to 2018
    public static final String EVENT_KEYS_FILENAME = "EventKeys2017.json";

    // Saves a list of all the events for a certain year,
    // User then chooses what event that they are at
    public static void getEventListAndSave(final int year, final Context context) {
        // Runs on separate thread because it is downloading and saving to file
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the object from the server
                JSONArray eventKeys;
                try {
                    // TODO: Update the year to 2018
                    eventKeys = new JSONArray(BlueAllianceUtils.getEventKeysFromYear(year));
                } catch (JSONException e) {
                    Log.d("JSON exception", e.toString());
                    return;
                }

                // Create a new array to only save the important values, which is the key and name values
                JSONArray arr = new JSONArray();

                try {
                    for (int i = 0; i < eventKeys.length(); i++) {
                        JSONObject obj = new JSONObject();

                        // Add the key and name
                        obj.put("key", eventKeys.getJSONObject(i).getString("key"));
                        obj.put("name", eventKeys.getJSONObject(i).getString("name"));

                        // Only in Canada
                        if(eventKeys.getJSONObject(i).getString("country").equals("Canada"))
                            arr.put(obj);
                    }
                } catch (JSONException e) {
                    Log.d("JSON error, ", e.toString());
                    return;
                }

                // TODO: Sort the array by event, right now not needed because only showing events in Canada

                // Save the file to the internal storage
                try {
                    writeFile(context, arr.toString(), EVENT_KEYS_FILENAME);
                } catch(IOException e) {
                    Log.d("Error writing file", e.toString());
                }

                Log.d("Event Keys file - ", arr.toString());
            }
        }).start();
    }

    public static JSONArray getArrayOfEvents(Context context) throws JSONException {
        return new JSONArray(readFile(EVENT_KEYS_FILENAME, context));
    }

    // Returns the string content of a file
    public static String readFile(String filename, Context context) {
        String fileContents;

        if(fileExists(context, filename)) {
            File file;

            try {
                // Find the file
                file = new File(context.getFilesDir(), filename);

                // Create and read the file
                BufferedReader br = new BufferedReader(new FileReader(file));
                fileContents = br.readLine();
                br.close();

                return fileContents;
            } catch(IOException e) {
                Log.d("Error reading file", e.toString());
                return null;
            }
        }

        return null;
    }

    // Writes a string to a file, throws an IOException if something goes wrong
    public static void writeFile(Context context, String s, String filename) throws IOException {
        // Create the file and directories
        File file = new File(context.getFilesDir(), filename);
        file.createNewFile();

        // Create the file writer and write to file
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.append(s);

        // Close the file
        bw.flush();
        bw.close();
    }

}
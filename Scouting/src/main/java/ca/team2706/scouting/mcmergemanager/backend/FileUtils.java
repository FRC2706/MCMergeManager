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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public static final OkHttpClient client = new OkHttpClient();
    public static final String SERVER_URL = "http://beancode.org:9999/";

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

        sRemoteTeamPhotosFilePath = "/" + App.getContext().getString(R.string.FILE_TOPLEVEL_DIR) + "/"  + "Team Photos";
    }

    public enum FileType {
        SYNCHED, UNSYNCHED;
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

        new Thread()
        {
            public void run() {
                if(!directoryTreeScanRunning) {
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

            for(File subFile : files) {
                if(subFile.isDirectory()) {
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
                        if(options != null && height > 640 && width > 640) {
                            System.out.println("foto is too large" + subFile.getAbsolutePath());
                            int inSampleSize = 1; // divides image resolution by this

                            // increases the samplesize until the future resolution is under 640 pixels
                            while((height / inSampleSize) > 640 &&
                                    (width / inSampleSize) > 640){
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
                    } catch(Exception e) {
                        Log.e("MCMergeManager","Error parsing or downsizing image:", e);
                    }


                    // Force the midea scanner to scan this file so it shows up from a PC over USB.
                    App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(subFile)));
                }
            }
        }
        else {
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
     *
     * Data format:
     * "matchNo<int>,teamNo<int>,isSpyBot<boolean>,reached<boolean>,{autoDefenseBreached<int>;...},{{autoBallShot_X<int>;autoBallShot_Y<int>;autoBallShot_time<.2double>;autoBallshot_which<int>}:...},{teleopDefenseBreached<int>;...},{{teleopBallShot_X<int>;teleopBallShot_Y<int>;teleopBallShot_time<.2double>;teleopBallshot_which<int>}:...},timeDefending<,2double>,{{ballPickup_selection<int>;ballPickup_time<,2double>}:...},{{scaling_time<.2double>;scaling_comelpted<int>}:...},notes<String>,challenged<boolean>,timeDead<int>"
     *
     * Or, in printf / format strings:
     * "%d,%d,%b,%b,{%d;...},{{%d:%d:%.2f:%d};...},{%d;...},{{%d:%d:%.2f:%d};...},%,2f,{{%d;%,2f}:...},{{%.2f;%d}:...},%s,%b,%d"
     */
    public static void appendToMatchDataFile(MatchData.Match match, FileType fileType) {

        //TODO: #76, make sure this actually works

        String outFileName;
        File outfile;
        if(fileType == FileType.SYNCHED) {
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
        } else if(fileType == FileType.UNSYNCHED) {
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

     * @return whether or not the delete was successful.
     */
    public boolean clearUnsyncedMatchScoutingDataFile() {
        File file = new File( App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED));
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
                Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: ",e);
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

        String outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.teamDataFileName);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team data to file: "+outFileName);

        File outfile = new File(outFileName);
        try {
            // create the file path, if it doesn't exist already.
            (new File(outfile.getParent())).mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append( teamDataObject.toString() + "\n");
            bw.flush();
            bw.close();

            // Force the midea scanner to scan this file so it shows up from a PC over USB.
            App.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(outfile)));
        } catch (IOException e) {
            Log.e("MCMergeManager", "appendToTeamDataFile could not save file.", e);
        }


        outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.teamDataFileNameUNSYNCHED);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team data to file: "+outFileName);

        outfile = new File(outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append( teamDataObject.toString() );
            bw.flush();
            bw.close();
        } catch (IOException e) {

        }
    }

    public static List<TeamDataObject> getRepairTimeObjects() {
        List<TeamDataObject> teamDataObjects = loadTeamDataFile();

        List<TeamDataObject> repairTimeObjectList = new ArrayList<>();

        if(teamDataObjects != null) {
            for(TeamDataObject teamDataObject: teamDataObjects) {
                if(teamDataObject instanceof RepairTimeObject)
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
            return  toRet;

        for(TeamDataObject teamDataObject : teamDataObjects)
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
    @param CommentList Class
    Saves the JSON to a file
     */

    public static final String COMMENT_FILE_PATH= "comments.json";

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

    private static void saveJsonFile(JSONArray jsonArray) {
        if(!clearTeamDataFile(FileType.SYNCHED)) {
            Log.d("Deleting file failed", "something probably went wrong");
        }

        String outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.matchScoutingDataFileName);
        File file = new File(outFileName);




        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            for (int i = 0; i < jsonArray.length(); i++){
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
        switch(fileType) {
            case UNSYNCHED:
                file = new File(sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED));
                try {
                    file.delete();
                    file.createNewFile();
                    return true;
                } catch(IOException e) {
                    Log.d("Creating new file err", e.toString());
                }
                break;
            case SYNCHED:
                file = new File(sLocalEventFilePath + "/" + App.getContext().getResources().getString(R.string.matchScoutingDataFileName));
                try {
                    file.delete();
                    file.createNewFile();
                    return true;
                } catch(IOException e) {
                    Log.d("Creating new file err", e.toString());
                }
                break;
        }
        return false;
    }

    public static final String TEAMS_LIST = "teams/list";
    public static final String TEAM_STATS = "teams/show?team="; // Add the team number at the end
    public static final String COMPETITION_LIST = "competitions/list";
    public static final String COMPETITON_STAT = "competitions/show?competition=";  // Add the competition code after
    public static final String GET_MATCH_A = "matches/show?competition=";  // Requires part b afterwards to choose which match
    public static final String GET_MATCH_B = "&match=";

    // Gets a json array from the server
    public static JSONArray getJsonArrayFromServer(String url) {
        Request request = new Request.Builder()
                .url(SERVER_URL + url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return new JSONArray(response.body().string());
        } catch(JSONException e) {
            Log.d("JSON error", e.toString());
        } catch(IOException e) {
            Log.d("Okhttp error", e.toString());
        }

        // If their was an error earlier return nothing
        return null;
    }

    // Gets a json object from the server
    public static JSONObject getJsonObjectFromServer(String url) {
        Request request = new Request.Builder()
                .url(SERVER_URL + url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return new JSONObject(response.body().string());
        } catch(JSONException e) {
            Log.d("JSON error", e.toString());
        } catch(IOException e) {
            Log.d("Okhttp error", e.toString());
        }

        return null;
    }

    // Post a comment to the server, returns true if successful
    public static boolean postCommentToServer(int team_number, String message) {
        Request request = new Request.Builder()
                .url(SERVER_URL + "comments/create?team=" + team_number + "&body=" + message)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // If server returns succes then the comment posted
            if(new String(response.body().string()).equals("success")) {
                return true;
            }
        } catch(IOException e) {
            Log.d("Okhttp3 error", e.toString());
        }

        return false;
    }
}
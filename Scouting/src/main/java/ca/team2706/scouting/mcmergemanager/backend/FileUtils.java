package ca.team2706.scouting.mcmergemanager.backend;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.NoteObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.RepairTimeObject;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.PhotoRequester;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.MatchData;

/**
 * This is a helper class to hold common code for accessing shared scouting gearDeliveryData files.
 * This class takes care of keeping a local cache, syncing to the server, and (eventually) sharing with other bluetooth-connected devices also running the app.
 * <p/>
 * Created by Mike Ounsworth
 */
public class FileUtils {

    public static String sLocalToplevelFilePath;
    public static String sLocalTeamFilePath;
    public static String sLocalEventFilePath;
    public static String sLocalTeamPhotosFilePath;

    /* Static initializer */
    static {
        // store string constants and preferences in member variables just for cleanliness
        // (since the strings are `static`, when any instances of FileUtils update these, all instances will get the updates)
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        sLocalToplevelFilePath = "/sdcard/"+ App.getContext().getString(R.string.FILE_TOPLEVEL_DIR);
        sLocalTeamFilePath = sLocalToplevelFilePath + "/" + SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_teamname), "<Not Set>");
        sLocalEventFilePath = sLocalTeamFilePath + "/" + SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");
        sLocalTeamPhotosFilePath = sLocalTeamFilePath + "/" + "Team Photos";
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
     * <p/>
     * The file structure is:
     * MCMergeManager/
     *  - team_name/
     *      - Team Photos/
     *      - event/
     *          - matchScoutingData.csv
     */
    //TODO Update ^^^
    public static void checkLocalFileStructure(Activity activity) {
        if (activity == null)
            return;

        // check for STORAGE permission
        if (!checkFileReadWritePermissions(activity))
            return;

        makeDirectory(sLocalToplevelFilePath);
        makeDirectory(sLocalTeamFilePath);
        makeDirectory(sLocalEventFilePath);
        makeDirectory(sLocalTeamPhotosFilePath);
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
     * Take one match of gearDeliveryData and stick it at the end of the match gearDeliveryData file.
     *
     * Data format:
     * "matchNo<int>,teamNo<int>,isSpyBot<boolean>,reached<boolean>,{autoDefenseBreached<int>;...},{{autoBallShot_X<int>;autoBallShot_Y<int>;autoBallShot_time<.2double>;autoBallshot_which<int>}:...},{teleopDefenseBreached<int>;...},{{teleopBallShot_X<int>;teleopBallShot_Y<int>;teleopBallShot_time<.2double>;teleopBallshot_which<int>}:...},timeDefending<,2double>,{{ballPickup_selection<int>;ballPickup_time<,2double>}:...},{{scaling_time<.2double>;scaling_comelpted<int>}:...},notes<String>,challenged<boolean>,timeDead<int>"
     *
     * Or, in printf / format strings:
     * "%d,%d,%b,%b,{%d;...},{{%d:%d:%.2f:%d};...},{%d;...},{{%d:%d:%.2f:%d};...},%,2f,{{%d;%,2f}:...},{{%.2f;%d}:...},%s,%b,%d"
     */
    public static void appendToMatchDataFile(MatchData.Match match) {

        //TODO: #76
        //TODO: Redo this to write the file in JSON format


        String outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.matchScoutingDataFileName);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving match gearDeliveryData to file: "+outFileName);

        File outfile = new File(outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append( match.toString() );
            bw.flush();
            bw.close();
        } catch (IOException e) {

        }


        outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.matchScoutingDataFileNameUNSYNCHED);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving match gearDeliveryData to file: "+outFileName);

        outfile = new File(outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append( match.toString() );
            bw.flush();
            bw.close();
        } catch (IOException e) {

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
     * Load the entire file of match gearDeliveryData into Objects.
     *
     * Data format:
     * "matchNo<int>,teamNo<int>,isSpyBot<boolean>,reached<boolean>,{autoDefenseBreached<int>;...},{{autoBallShot_X<int>;autoBallShot_Y<int>;autoBallShot_time<.2double>;autoBallshot_which<int>}:...},{teleopDefenseBreached<int>;...},{{teleopBallShot_X<int>;teleopBallShot_Y<int>;teleopBallShot_time<.2double>;teleopBallshot_which<int>}:...},timeDefending<,2double>,{{ballPickup_selection<int>;ballPickup_time<,2double>}:...},{{scaling_time<.2double>;scaling_comelpted<int>}:...},notes<String>,challenged<boolean>,timeDead<int>"
     *
     * Or, in printf / format strings:
     * "%d,%d,%b,%b,{%d;...},{{%d:%d:%.2f:%d};...},{%d;...},{{%d:%d:%.2f:%d};...},%,2f,{{%d;%,2f}:...},{{%.2f;%d}:...},%s,%b,%d"
     */
    public static MatchData loadMatchDataFile() {
        return loadMatchDataFile(FileType.SYNCHED);
    }


    public static MatchData loadMatchDataFile(FileType fileType) {

        //TODO: #76
        //TODO: Redo this to write the file in JSON format


        MatchData matchData = new MatchData();
        List<String> matchStrs = new ArrayList<>();

        // read the file
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
                line = line.replace("{","").replace("}","");
                matchStrs.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: " + e.toString());
            return null;
        }

        // parse all the matches into the MatchData object
        boolean parseFailure = false;
        for (String matchStr : matchStrs) {

            try {
                MatchData.Match match = new MatchData.Match(matchStr);
                matchData.addMatch(match);
            } catch (Exception e) {
                Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: "+e.toString());
                parseFailure = true;
                continue;
            }
        }
        if (parseFailure) {
            Toast.makeText(App.getContext(), "Warning: match gearDeliveryData may be corrupted or malformed.", Toast.LENGTH_SHORT).show();
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
        // TODO #90

        String outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.teamDataFileName);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team gearDeliveryData to file: "+outFileName);

        File outfile = new File(outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true));
            bw.append( teamDataObject.toString() );
            bw.flush();
            bw.close();
        } catch (IOException e) {

        }


        outFileName = sLocalEventFilePath +"/"+ App.getContext().getResources().getString(R.string.teamDataFileNameUNSYNCHED);

        Log.d(App.getContext().getResources().getString(R.string.app_name), "Saving team gearDeliveryData to file: "+outFileName);

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
     * Load gearDeliveryData from the teamDataFile.
     */
    public static List<TeamDataObject> loadTeamDataFile() {
        return loadTeamDataFile(FileType.SYNCHED);
    }

        /**
         * Load gearDeliveryData from the teamDataFile.
         */
    public static List<TeamDataObject> loadTeamDataFile(FileType fileType) {
        // TODO #90

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
            Log.e(App.getContext().getResources().getString(R.string.app_name), "loadMatchDataFile:: " + e.toString());
            return null;
        }

        return teamDataObjects;
    }


    /**
     * This will reload the entire TeamDataFile from disk. If you already have a List<TeamDataObject>, then
     * it would be more efficient to pass it to filterTeamDataByTeam().
     */
    public static List<TeamDataObject> loadTeamDataForTeam(int teamNo) {
        // TODO #90

        return filterTeamDataByTeam(teamNo, loadTeamDataFile());
    }

    public static List<TeamDataObject> filterTeamDataByTeam(int teamNo, List<TeamDataObject> teamDataObjects) {
        // TODO #90

        List<TeamDataObject> toRet = new ArrayList<>();

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
        File file = new File(dir);

        if (!file.isDirectory()) {
            // in case there's a regular file there with the same name
            file.delete();

            // create it
            file.mkdir();
        }

        String fileName = dir + teamNumber + "_" + (new Date().getTime()) + ".jpg";
        return Uri.fromFile(new File(fileName));
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
                Bitmap bitmap = loadScaledDownImage(file.getPath());
                if (bitmap != null)
                    arrBitmaps.add(bitmap);
            }
            // else: if it's not a file, then what is it???? .... skip I guess
        }
        requester.updatePhotos(arrBitmaps.toArray(new Bitmap[arrBitmaps.size()]));


        /* This used to sync with Google Drive, now we need something different */
        // TODO

    }

    /**
     * Calculates how much to scale the image based on the size of the screen and then loads
     * a scaled down version into memory.
     */
    private static Bitmap loadScaledDownImage(String imagePath) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath);

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int reqSize = displayMetrics.widthPixels / 4;

        if (height > reqSize || width > reqSize) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps
            // height or width larger than the requested size.
            while ((halfHeight / inSampleSize) > reqSize
            || (halfWidth / inSampleSize) > reqSize) {
            inSampleSize *= 2;
            }
        }

        // Decode bitmap with inSampleSize set
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath);
    }
}
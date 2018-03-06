package ca.team2706.scouting.mcmergemanager.gui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.net.ftp.FTPFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.App;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtils;
import ca.team2706.scouting.mcmergemanager.backend.FTPClient;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.TakePicture;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentList;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.TeamDataObject;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.DataRequester;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.FTPRequester;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeleopScoutingObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;

@TargetApi(21)
public class MainActivity extends AppCompatActivity
        implements DataRequester, PreMatchReportFragment.OnFragmentInteractionListener {

    public Context context;

    public int teamColour = Color.rgb(102, 51, 153);


    Intent globalIntent;
    static MainActivity me;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    public static MatchData sMatchData = new MatchData();
    public static MatchSchedule sMatchSchedule = new MatchSchedule();
    public static List<TeamDataObject> sRepairTimeObjects = new ArrayList<>();
    public static TeamInfoTab mTeamInfoTab;
    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    public static FTPClient sFtpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FileUtils.checkLocalFileStructure(this);
        context = this;
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        setNavDrawer();

        context = this;

        globalIntent = new Intent();

        me = this;

        // tell the user where they are syncing their gearDeliveryData to
        updateDataSyncLabel();

        FileUtils.checkFileReadWritePermissions(this);

        getEventKeys();

    }

    public void generateThreatList(View view){
        Intent intent = new Intent(this, ThreatListGenerator.class);
        startActivity(intent);
    }


    // Check to see if the event keys has been downloaded yet, if not yet downloaded for this year then download
    private void getEventKeys() {
        if (!FileUtils.fileExists(this, FileUtils.EVENT_KEYS_FILENAME)) {
            FileUtils.getEventListAndSave(2017, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // tell the user where they are syncing their gearDeliveryData to
        updateDataSyncLabel();

        // fetch the match gearDeliveryData from TheBlueAlliance to update the scores.
        BlueAllianceUtils.checkInternetPermissions(this);
        BlueAllianceUtils.fetchTeamsRegisteredAtEvent(this);
        BlueAllianceUtils.fetchMatchScheduleAndResults(this);

        // Make sure all files are there, and visible to the USB Media Scanner.
        FileUtils.checkLocalFileStructure(this);

        // In case the schedule is empty, make sure we pass along the list of teams registered at event
        // that we fetched at the beginning.
       // sMatchData = FileUtils.loadMatchDataFile();
        if (sMatchData == null) sMatchData = new MatchData();

        sRepairTimeObjects = FileUtils.getRepairTimeObjects();

        // syncs unposted matches and downloads matchdata for current competition
        if (false)
            FileUtils.syncFiles(this);
    }

    /**
     * Called when activity gets invisible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Called when the user clicks the Scout Match button
     */
    public void scout(View view) {

        // if they've entered a Match Number in the box, pass that along.
        EditText matchNoET = (EditText) findViewById(R.id.matchNoET);
        int matchNo;
        try {
            matchNo = Integer.parseInt(matchNoET.getText().toString());
        } catch (NumberFormatException e) {
            matchNo = -1;
        }

        Intent intent = new Intent(this, PreGameActivity.class);
        intent.putExtra(getString(R.string.EXTRA_MATCH_NO), matchNo);
        intent.putExtra(getString(R.string.EXTRA_MATCH_SCHEDULE), sMatchSchedule);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.F
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://team2706.ca"));
                startActivity(launchBrowser);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Button handler for the Show [Match] Schedule button.
     */
    public void onShowScheduleClicked(View view) {

        if (sMatchSchedule != null) {
            // bundle the match gearDeliveryData into an intent
            Intent intent = new Intent(this, MatchScheduleActivity.class);
            intent.putExtra(getResources().getString(R.string.EXTRA_MATCH_SCHEDULE), sMatchSchedule.toString());
            startActivity(intent);
        } else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork == null) { // not connected to the internet
                Toast.makeText(this, "No Schedule Data to show. No Internet?", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onShowTeamScheduleClicked(View view) {
        if (sMatchSchedule == null) {
            Toast.makeText(this, "No Schedule Data to show. No Internet?", Toast.LENGTH_LONG).show();
            return;
        }

        enterATeamNumberPopup = new GetTeamNumberDialog("View the Schedule for one Team", "Team Number", 1, this);
        enterATeamNumberPopup.displayAlertDialog();

        (new Timer()).schedule(new CheckSchedulePopupHasExited(), 250);
    }



    public CommentTextEditor commentTextEditor;

    public void onRepairTimeRecordClicked(View view) {
        Intent intent = new Intent(this, RepairTimeCollection.class);
        intent.putExtra(getResources().getString(R.string.EXTRA_MATCH_SCHEDULE), sMatchSchedule.toString());
        startActivity(intent);
    }

    private void setNavDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(teamColour);

        setSupportActionBar(toolbar);

        //Set up drawer layout and navigation view
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
    }

    public GetTeamNumberDialog enterATeamNumberPopup;

    public void takePicture(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 123);
            return; // the popup is non-blocking, so they'll have to click "Take Picture" again.
        }

        enterATeamNumberPopup = new GetTeamNumberDialog("Team Number", "Team Number", 1, this);
        enterATeamNumberPopup.displayAlertDialog();

        (new Timer()).schedule(new CheckPicturePopupHasExited(), 250);
    }


    @Override
    public void updateData(String[] matchResultsDataCSV, String[] matchScoutingDataCSV) {

    }

    @Override
    public void updateMatchSchedule(MatchSchedule matchSchedule) {

        // In the case that the schedule is not published yet,
        // make sure we preserve the list of teams registered at this event.
        matchSchedule.addToListOfTeamsAtEvent(sMatchSchedule.getTeamNumsAtEvent());
        sMatchSchedule = matchSchedule;

        // Notify the TeamInfoTab that the list of teams at this event has updated.
        if (mTeamInfoTab != null)
            mTeamInfoTab.rebuildAutocompleteList();

    }

    /**
     * Set the text for the label "Syncing Data To:" according to the saved preferences.
     */
    public void updateDataSyncLabel() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String event_code = SP.getString(getResources().getString(R.string.PROPERTY_event), "<Not Set>");

        // look up the human-readable event_name that matches this event_code.
        String event_name = "";
        String[] event_codes = getString(R.string.TBA_EVENT_CODES).split(":");
        for (int i = 0; i < event_codes.length; i++)
            if (event_codes[i].equals(event_code))
                event_name = getString(R.string.TBA_EVENT_NAMES).split(":")[i];

        String label = "Event: " + event_name + " [" + event_code + "]";

        TextView tv = (TextView) findViewById(R.id.sync_settings_tv);
        tv.setText(label);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // empty?
    }


    class CheckPicturePopupHasExited extends TimerTask {
        public void run() {
            if (enterATeamNumberPopup.accepted) {
                int teamNumber;
                try {
                    teamNumber = Integer.parseInt(enterATeamNumberPopup.inputResult);
                } catch (NumberFormatException e) {

                    Log.e(getResources().getString(R.string.app_name), e.toString());
                    return;
                }

                Uri teamPhotoUri = FileUtils.getNameForNewPhoto(teamNumber);
                String teamPhotoPath = teamPhotoUri.getPath();
                Log.i(getResources().getString(R.string.app_name), "Saving to \"" + teamPhotoPath + "\"");

                TakePicture pic = new TakePicture(teamPhotoPath, me);
                pic.capturePicture();
                DisplayAlertDialog.accepted = false;
            } else if (!enterATeamNumberPopup.canceled) {
                // schedule me to run again
                (new Timer()).schedule(new CheckPicturePopupHasExited(), 250);
            }
        }
    }

    class CheckSchedulePopupHasExited extends TimerTask {
        public void run() {
            if (enterATeamNumberPopup.accepted) {
                int teamNumber;
                try {
                    teamNumber = Integer.parseInt(enterATeamNumberPopup.inputResult);
                } catch (NumberFormatException e) {
                    Log.d(me.getResources().getString(R.string.app_name),
                            e.toString());
                    return;
                }

                // bundle the match gearDeliveryData into an intent and launch the schedule activity
                Intent intent = new Intent(me, MatchScheduleActivity.class);
                intent.putExtra(getResources().getString(R.string.EXTRA_MATCH_SCHEDULE), sMatchSchedule.toString());
                intent.putExtra(getResources().getString(R.string.EXTRA_TEAM_NO), teamNumber);
                startActivity(intent);

                DisplayAlertDialog.accepted = false;
            } else if (!enterATeamNumberPopup.canceled) {
                // schedule me to run again
                (new Timer()).schedule(new CheckSchedulePopupHasExited(), 250);
            }
        }
    }


    public void downloadFileCallback(String localFilename, String remoteFilename) {
        //Here in case we need it later
    }

    public void uploadFileCallback(String localFilename, String remoteFilename) {
        //Here in case we need it later
    }

    public void syncCallback(int changedFiles) {
        //Here in case we need it later
    }


    public void updateSyncBar(String Caption, int Progress, Activity activity, boolean isRunning) {
        final String caption = Caption;
        final int progress = Progress;
        final Activity activ = activity;
        final boolean isRunningf = isRunning;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                TextView tv = (TextView) activ.findViewById(R.id.syncCaption);
                ProgressBar pb = (ProgressBar) activ.findViewById(R.id.syncBar);
                Button bu = (Button) activ.findViewById(R.id.syncButon);
                if (isRunningf) {
                    bu.setText("syncing...");
                } else {
                    bu.setText("Sync Photos");
                }
                if (caption.startsWith("^")) {
                    pb.setIndeterminate(true);
                    tv.setText("Getting File Listing...");
                } else {
                    pb.setIndeterminate(false);
                    tv.setText(caption);
                }
                pb.setProgress(progress);
            }
        });
    }



    public void onClick(View v) {
        FileUtils.checkLocalFileStructure(this);
        FileUtils.syncFiles(this);
    }


}
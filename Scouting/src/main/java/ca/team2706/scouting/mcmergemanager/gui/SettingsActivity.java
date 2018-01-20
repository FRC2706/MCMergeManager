package ca.team2706.scouting.mcmergemanager.gui;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.App;
import ca.team2706.scouting.mcmergemanager.backend.FTPClient;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static JSONArray eventKeys;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        try {
            eventKeys = FileUtils.getArrayOfEvents(this);
        } catch(JSONException e) {
            Log.d("JSON error", e.toString());
        }
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            final FTPClient temp = new FTPClient(FileUtils.sLocalTeamPhotosFilePath);
            if(preference.getSharedPreferences().getString(App.getContext().getString(R.string.PROPERTY_FTPNukeLocalFile), "no").toLowerCase().equals("yes")){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        temp.nukeLocalFiles(true, true, true);
                    }
                });

            }
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof CheckBoxPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        }
        else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || TeamSettingsPreferenceFragment.class.getName().equals(fragmentName);
    }

    // Returns the list of the events
    private static CharSequence[] getEvents(JSONArray arr) {
        // The length of the charsequence array should be the same as the length of the json array
        CharSequence[] events = new CharSequence[arr.length()];

        try {
            for (int i = 0; i < arr.length(); i++) {
                events[i] = arr.getJSONObject(i).getString("name");
            }
        } catch(JSONException e) {
            Log.d("Error parsing json", e.toString());
            return null;
        }

        return events;
    }

    // Returns the list of the keys
    private static CharSequence[] getKeys(JSONArray arr) {
        // The length of the charsequence array should be the same as the length of the json array
        CharSequence[] keys = new CharSequence[arr.length()];

        try {
            for (int i = 0; i < arr.length(); i++) {
                keys[i] = arr.getJSONObject(i).getString("name");
            }
        } catch(JSONException e) {
            Log.d("Error parsing json", e.toString());
            return null;
        }

        return keys;
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TeamSettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_team_settings);
            setHasOptionsMenu(true);

            // populate the list of event codes from TheBlueAlliance
            ListPreference eventsLP = (ListPreference) findPreference(getResources().getString(R.string.PROPERTY_event));
            eventsLP.setEntries(getKeys(eventKeys));
            eventsLP.setEntryValues(getEvents(eventKeys));
//            eventsLP.setEntries(getResources().getString(R.string.TBA_EVENT_NAMES).split(":") );
//            eventsLP.setEntryValues(getResources().getString(R.string.TBA_EVENT_CODES).split(":"));



            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_DBHostname)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_FTPHostname)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_FTPUsername)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_FTPPassword)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_FTPSyncOnlyWifi)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_event)));
            bindPreferenceSummaryToValue(findPreference(getResources().getString(R.string.PROPERTY_FTPNukeLocalFile)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

package ca.team2706.scouting.mcmergemanager.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.App;
import ca.team2706.scouting.mcmergemanager.backend.FTPClient;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.steamworks2017.StatsEngine;


public class PrimaryTab extends Fragment {

    private View v;
    private Bundle m_savedInstanceState;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.primary_fragment_tab, null);
        m_savedInstanceState = savedInstanceState;
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean ftpSyncOnlyWifi = SP.getBoolean(App.getContext().getResources().getString(R.string.PROPERTY_FTPSyncOnlyWifi), false);

        // check if we have internet connectivity, and are on WiFi
        ConnectivityManager cm = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork == null ) {
            // not connected to the internet
            return;
        }
        else if (ftpSyncOnlyWifi && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
            // Settings require FTP sync only over WiFi
            // and we not connected over WiFi.
            return;
        }
    }
}

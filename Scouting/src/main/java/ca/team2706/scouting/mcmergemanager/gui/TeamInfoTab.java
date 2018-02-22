package ca.team2706.scouting.mcmergemanager.gui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by cnnr2 on 2015-10-31.
 */
public class TeamInfoTab extends Fragment {

    private Bundle m_savedInstanceState = null;
    public View view;
    public TeamInfoFragment m_teamInfoFragment;
    AutoCompleteTextView mSearchBox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_savedInstanceState = savedInstanceState;

        view = inflater.inflate(R.layout.team_info_tab, container, false);

        bindSearchBoxAndButton();

        // So, uhh, this is hacky as shit, but lets MainActivity call us when the teams list at this event
        // has loaded
        MainActivity.mTeamInfoTab = this;

        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public EditText editText;
    public  static boolean accepted = false;
    public  static boolean canceled = false;
    public  static String inputResult;
    public View edit;
    public AlertDialog.Builder alert;


    public void bindSearchBoxAndButton() {

        mSearchBox = (AutoCompleteTextView) view.findViewById(R.id.searchView);

        // Bind the enter key action for the EditText
        mSearchBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loadTeamInfoFrag();

                    // true means we dealt with (consumed) this keypress
                    return true;
                }

                // false means we did not deal with this keypress, and it should hand this keypress
                // event to the next registered handler.
                return false;
            }
        });


        ImageButton button = (ImageButton) view.findViewById(R.id.team_info_searchBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTeamInfoFrag();
            }
        });
    }


    void rebuildAutocompleteList() {

        // This stuff changes the GUI, so it needs to be run on the UI thread

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Build the autocomplete list
                List<String> teamsAtEventList = MainActivity.sMatchSchedule.getTeamNumsAtEvent();
                String[] autocompleteList = new String[teamsAtEventList.size()];
                for (int i = 0; i < teamsAtEventList.size(); i++)
                    autocompleteList[i] = teamsAtEventList.get(i);


                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.me, android.R.layout.simple_dropdown_item_1line,
                        autocompleteList);

                mSearchBox.setAdapter(adapter);
                mSearchBox.setThreshold(0);
            }
        });
    }

    private void loadTeamInfoFrag() {
        // load the team stats fragment

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (m_savedInstanceState != null) {
            return;
        }

        int teamNumber;
        try {
            teamNumber = Integer.parseInt(mSearchBox.getText().toString());
        } catch (NumberFormatException e) {
            // they didn't type in a valid number. Too bad for them, we're not going any further!
            return;
        }

        // Create a new Fragment to be placed in the activity layout
        m_teamInfoFragment = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putInt("teamNumber", teamNumber);
        // StatsEngine statsEngine = new StatsEngine(MainActivity.sMatchData, MainActivity.sMatchSchedule, MainActivity.sRepairTimeObjects);

       // TeamStatsReport teamStatsReport = statsEngine.getTeamStatsReport(teamNumber);  // just so I can look at it in bebug
       // args.putSerializable(getString(R.string.EXTRA_TEAM_STATS_REPORT), teamStatsReport);
        m_teamInfoFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, m_teamInfoFragment).commit();


        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}

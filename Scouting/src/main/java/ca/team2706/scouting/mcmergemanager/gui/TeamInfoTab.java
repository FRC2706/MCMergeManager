package ca.team2706.scouting.mcmergemanager.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.StatsEngine;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;

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

        bindImageButton();
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


    public void bindImageButton() {
        // Inflate the menu; this adds items to the action bar if it is present.
        ImageButton button = (ImageButton) view.findViewById(R.id.imageButton);

        button.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View view) {

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.layout_download_alert, null);
                alert = new AlertDialog.Builder(getActivity());
                Log.e("this far", "so close");
                alert.setTitle("Download Competition");
                alert.setView(alertLayout);
                alert.setCancelable(false);
                final Bundle args = new Bundle();
                Spinner spinner = (Spinner) alertLayout.findViewById(R.id.year_spinner);

                // Create an ArrayAdapter using the string array and a default spinner layout
                int selectedCurrent = spinner.getSelectedItemPosition();
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.year_array, android.R.layout.simple_spinner_item);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                args.putString("selectedYear", "2016");
                                break;
                            case 1:
                                args.putString("selectedYear", "2015");
                                break;
                            case 2:
                                args.putString("selectedYear", "2014");
                                break;
                            case 3:
                                args.putString("selectedYear", "2013");
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                //this stuff gets the edittext from the view and sets the hint and the inputtype
                edit = alertLayout.findViewById(R.id.inputHint);
                if (edit instanceof EditText) {
                    editText = (EditText) edit;
                    editText.setHint("Competition ID - See Help for more");

                }


                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        canceled = true;
                    }
                });

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                accepted = true;
                                Log.e("accepted", "" + accepted);
                                inputResult = editText.getText().toString();
                                TeamInfoFragment fragment1 = new TeamInfoFragment();

                                args.putString("inputResult", inputResult);
                                args.putBoolean("accepted", accepted);
                                fragment1.setArguments(args);

                                // Add the fragment to the 'fragment_container' FrameLayout
                                getActivity().getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment1).commit();
                            }
                        }
                );
                AlertDialog dialog = alert.create();
                dialog.show();
            }


        });
    }

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
        StatsEngine statsEngine = new StatsEngine(MainActivity.sMatchData, MainActivity.sMatchSchedule, MainActivity.sRepairTimeObjects);

        TeamStatsReport teamStatsReport = statsEngine.getTeamStatsReport(teamNumber);  // just so I can look at it in bebug
        args.putSerializable(getString(R.string.EXTRA_TEAM_STATS_REPORT), teamStatsReport);
        m_teamInfoFragment.setArguments(args);

        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, m_teamInfoFragment).commit();
    }
}

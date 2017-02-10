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
public class TeamInfoTab extends Fragment
                        implements View.OnKeyListener {

    private static boolean accepted = false;
    private static boolean canceled = false;

    private Bundle mSavedInstanceState = null;

    private View view;
    private TeamInfoFragment mTeamInfoFragment;
    private AutoCompleteTextView mAutoCompleteTextView;

    private EditText editText;
    private static String inputResult;
    private View edit;
    private AlertDialog.Builder alert;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;

        view = inflater.inflate(R.layout.team_info_tab, container, false);

        launchImageButton();
        mAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.teamNumberAutoCompleteTV);

//        mAutoCompleteTextView.setOnKeyListener(this);

        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Build the autocomplete list
        List<String> teamsAtEventList = MainActivity.sMatchSchedule.getTeamNumsAtEvent();
        String[] autocompleteList = new String[teamsAtEventList.size()];
        for (int i = 0; i < teamsAtEventList.size(); i++)
            autocompleteList[i] = teamsAtEventList.get(i);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.me, android.R.layout.simple_dropdown_item_1line,
                autocompleteList);

        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(0);
    }
    

    public boolean launchImageButton() {
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


                Log.d("editing hint", "you got that");


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

        return true;

    }


//    @Override
//    public boolean onQueryTextChange(String newText) {
//        // TODO: show a filtered autocomplete list of teams at this event
//
//        // Calculate the list of team numbers to show
//        //if list.length > 0
//
//
//        return true;
//        // else
////        return false;
//    }

    /**
     * From interface SearchView.OnSuggestionListener
     */
//    @Override
//    public boolean onSuggestionSelect(int position) {
//
//        return false;
//    }

    /**
     * From interface SearchView.OnSuggestionListener
     */
//    @Override
//    public boolean onSuggestionClick(int position) {
//
////        SQLiteCursor cursor = (SQLiteCursor) mAutoCompleteTextView.getSuggestionsAdapter().getItem(position);
////        int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);
////
////        mAutoCompleteTextView.setQuery(cursor.getString(indexColumnSuggestion), false);
//
//        return true;
//    }

    /**
     * From interface SearchView.OnQueryTextListener.
     * <p>
     * Handles them pressing Enter on the search bar.
     */
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press

            // load the team stats fragment

            // If we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if(mSavedInstanceState!=null) {
                return false;
            }

            int teamNumber;
            try {
                teamNumber = Integer.valueOf(mAutoCompleteTextView.getText().toString());
            }

            catch(NumberFormatException e) {
                // they didn't type in a valid number. Too bad for them, we're not going any further!
                return false;
            }

            // Create a new Fragment to be placed in the activity layout
            mTeamInfoFragment=new

                    TeamInfoFragment();

            Bundle args = new Bundle();
            args.putInt("teamNumber",teamNumber);
            StatsEngine statsEngine = new StatsEngine(MainActivity.sMatchData, MainActivity.sMatchSchedule);

            TeamStatsReport teamStatsReport = statsEngine.getTeamStatsReport(teamNumber);  // just so I can look at it in bebug
            args.putSerializable( getString(R.string.EXTRA_TEAM_STATS_REPORT),teamStatsReport );
            mTeamInfoFragment.setArguments(args);

            // Add the fragment to the 'fragment_container' FrameLayout
            getActivity()
                    .getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mTeamInfoFragment)
                    .commit();

            return true;
        }

        return false;
    }


}

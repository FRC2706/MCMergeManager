package ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.FuelShotEvent;

import static ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeleopScouting.FUEL_SHOT_EVENT_STRING;

/**
 * Created by Merge on 2017-02-09.
 */

public class BallShootingFragment extends DialogFragment{

    SeekBar ballShootingSeekBar;
    public int pointsScored;
    private int ballsHeld;
    public Bundle fuelShootingData = new Bundle();

    // These are too assemble a string for the text view, not the most elegant solution but it works. -JustinT
    public String endingText = " balls were scored";
    public String pointsScoredString;
    public String textViewDisplayString;


    private FuelShotEvent ballsScored = new FuelShotEvent();
    private FragmentListener listener;


    public BallShootingFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static BallShootingFragment newInstance(String title, FragmentListener listener, int ballsHeld) {
        BallShootingFragment frag = new BallShootingFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.listener = listener;
        frag.ballsHeld = ballsHeld;
        return frag;
    }

    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ball_scoring, container);

        SeekBar seekBar = (SeekBar) v.findViewById(R.id.teleopBallsScoredSeekBar);
        seekBar.setMax(ballsHeld);
        seekBar.setProgress(ballsHeld);
        pointsScored = ballsHeld;

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;

        view.findViewById(R.id.ballScoringCancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

// ballsScored.numScored
        // initiate  views
        ballShootingSeekBar=(SeekBar)view.findViewById(R.id.teleopBallsScoredSeekBar);
        // perform seek bar change listener event used for getting the progress value
        ballShootingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = ballsHeld;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used by anything, just need to override it in the thing
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = (TextView) getView().findViewById(R.id.teleopBallScoredTextView);
                pointsScored = progressChangedValue;
                pointsScoredString = String.valueOf(pointsScored);
                //textViewDisplayString;
                textViewDisplayString = pointsScoredString + endingText;
                tv.setText(textViewDisplayString);
//              Toast.makeText(getActivity(), "Data saved!", Toast.LENGTH_LONG).show();
            }

        });
        view.findViewById(R.id.lowGoalButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ballsScored.boiler = false;
                        ballsScored.numScored = pointsScored;
                        fuelShootingData.putSerializable(FUEL_SHOT_EVENT_STRING, ballsScored);
                        listener.editNameDialogComplete(me, fuelShootingData);
//                        teleopScoutingObject.add(ballsScored);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);


                    }
                }
        );

        view.findViewById(R.id.highGoalButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ballsScored.boiler = true;
                        ballsScored.numScored = pointsScored;
                        fuelShootingData.putSerializable(FUEL_SHOT_EVENT_STRING, ballsScored);
                        listener.editNameDialogComplete(me, fuelShootingData);
//                        teleopScoutingObject.add(ballsScored);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);


                    }
                }
        );
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
//        listener.editNameDialogCancel(this);
    }


}



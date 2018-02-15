package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by awesomedana on 2018-02-12.
 */

public class AutoPitScoutFragment extends DialogFragment {

    public AutoPitScoutFragment(){}

    private PitScoutingFragment listener;

    public static AutoPitScoutFragment newInstance(String title, PitScoutingFragment listener){

        AutoPitScoutFragment autoPitScoutFragment = new AutoPitScoutFragment();
        Bundle argument = new Bundle();

        argument.putString("title", title);

        autoPitScoutFragment.setArguments(argument);

        autoPitScoutFragment.listener = listener;

        return autoPitScoutFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pit_scouting_auto, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;
    }
}
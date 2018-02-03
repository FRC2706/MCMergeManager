package ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by Merge on 2018-01-31.
 */

public class TestFragment extends DialogFragment {

    private FragmentListener listener;

    public TestFragment () {
        // Empty Constructor
    }

    public static TestFragment newInstance(String title, FragmentListener listener) {
        TestFragment frag = new TestFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.listener = listener;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;

    }

}

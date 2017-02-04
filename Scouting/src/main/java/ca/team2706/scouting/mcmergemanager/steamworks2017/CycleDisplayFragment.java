package ca.team2706.scouting.mcmergemanager.steamworks2017;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.gui.CyclesDisplayView;

/**
 * A GUI fragment that holds a list of actions, and time-graph bars visualizing cycles for each
 * action during a match.
 *
 * Created by mike on 23/01/17.
 */

public class CycleDisplayFragment extends Fragment {

    public static final int GEAR_CYCLE      = 0;
    public static final int HIGH_FUEL_CYCLE = 1;
    public static final int LOW_FUEL_CYCLE  = 2;
    public static final int DEFENSE_CYCLE   = 3;
    public static final int CLIMB_CYCLE     = 4;


    private View m_view;
    private int m_matchNo = 0;
    private CyclesDisplayView[] cyclesDisplayViews = new CyclesDisplayView[5];

    private boolean m_created = false;



    public CycleDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        m_view =  inflater.inflate(R.layout.steamworks2017_fragment_cycles_display, null);

        cyclesDisplayViews[GEAR_CYCLE] = (CyclesDisplayView) m_view.findViewById(R.id.gears_cyclesDisplayView);
        cyclesDisplayViews[HIGH_FUEL_CYCLE] = (CyclesDisplayView) m_view.findViewById(R.id.highFuel_cyclesDisplayView);
        cyclesDisplayViews[LOW_FUEL_CYCLE] = (CyclesDisplayView) m_view.findViewById(R.id.lowFuel_cyclesDisplayView);
        cyclesDisplayViews[DEFENSE_CYCLE] = (CyclesDisplayView) m_view.findViewById(R.id.defense_cyclesDisplayView);
        cyclesDisplayViews[CLIMB_CYCLE] = (CyclesDisplayView) m_view.findViewById(R.id.climb_cyclesDisplayView);
        ((TextView) m_view.findViewById(R.id.cyclesDisplayMatchNo)).setText("Match "+m_matchNo);

        m_created = true;
        return m_view;
    }

    public void setMatchNo(int matchNo) {
        if (!m_created) throw new IllegalStateException("Views have not been created yet. Wait for the end of onCreate()");

        ((TextView) getView().findViewById(R.id.cyclesDisplayMatchNo)).setText("Match "+matchNo);
    }

    public void addCycle(int cycleType, double startTime, double endTime, boolean success) {
        if (!m_created) throw new IllegalStateException("Views have not been created yet. Wait for the end of onCreate()");

        cyclesDisplayViews[cycleType].addCycle(startTime, endTime, success);
    }



}
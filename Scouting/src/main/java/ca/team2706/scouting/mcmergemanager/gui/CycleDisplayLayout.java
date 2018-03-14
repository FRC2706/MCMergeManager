package ca.team2706.scouting.mcmergemanager.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Cycle;

/**
 * Created by mike on 23/01/17.
 */


public class CycleDisplayLayout extends LinearLayout {

    public static final int GEAR_CYCLE      = 0;
    public static final int HIGH_FUEL_CYCLE = 1;
    public static final int LOW_FUEL_CYCLE  = 2;
    public static final int DEFENSE_CYCLE   = 3;
    public static final int CLIMB_CYCLE     = 4;

    private CyclesDisplayView[] cyclesDisplayViews = new CyclesDisplayView[5];
    private TextView matchNoTV;


    public CycleDisplayLayout(Context context) {
        super(context);
    }

    public CycleDisplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CycleDisplayLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        cyclesDisplayViews[GEAR_CYCLE] = (CyclesDisplayView) findViewById(R.id.gears_cyclesDisplayView);
        cyclesDisplayViews[HIGH_FUEL_CYCLE] = (CyclesDisplayView) findViewById(R.id.highFuel_cyclesDisplayView);
        cyclesDisplayViews[LOW_FUEL_CYCLE] = (CyclesDisplayView) findViewById(R.id.lowFuel_cyclesDisplayView);
        cyclesDisplayViews[DEFENSE_CYCLE] = (CyclesDisplayView) findViewById(R.id.defense_cyclesDisplayView);
        cyclesDisplayViews[CLIMB_CYCLE] = (CyclesDisplayView) findViewById(R.id.climb_cyclesDisplayView);

        matchNoTV = (TextView) findViewById(R.id.cyclesDisplayMatchNo);
    }


    public void setMatchNo(int matchNo) {
        matchNoTV.setText("Match "+matchNo);
    }

    public void addCycle(int cycleType, double startTime, double endTime, boolean success) {
        cyclesDisplayViews[cycleType].addCycle(startTime, endTime, success);
    }


    public void addCycle(Cycle cycle) {
//        switch(cycle.cycleType) {
//            case GEAR:
//                addCycle(GEAR_CYCLE, cycle.startTime, cycle.endTime, cycle.success);
//                break;
//            case LOW_GOAL:
//                addCycle(LOW_FUEL_CYCLE, cycle.startTime, cycle.endTime, cycle.success);
//                break;
//            case HIGH_GOAL:
//                addCycle(HIGH_FUEL_CYCLE, cycle.startTime, cycle.endTime, cycle.success);
//                break;
//            case CLIMB:
//                addCycle(CLIMB_CYCLE, cycle.startTime, cycle.endTime, cycle.success);
//                break;
//            case DEFENSE:
//                addCycle(DEFENSE_CYCLE, cycle.startTime, cycle.endTime, cycle.success);
//                break;
//        }

    }

}

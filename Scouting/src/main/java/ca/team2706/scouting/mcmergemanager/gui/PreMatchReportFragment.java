package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.StatsEngine;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.TeamStatsReport;

public class PreMatchReportFragment extends Fragment {
    // the fragment initialization parameters
    public static final String ARG_MATCH = "Match";
    public static final String ARG_STATS = "STATS_ENGINE";

    private MatchSchedule.Match m_match;
    private StatsEngine m_statsEngine;

    private View m_view;

    private OnFragmentInteractionListener mListener;

    public static PreMatchReportFragment newInstance(MatchSchedule.Match match, StatsEngine statsEngine) {
        PreMatchReportFragment fragment = new PreMatchReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MATCH, match.toString());
        args.putSerializable(ARG_STATS, statsEngine);
        fragment.setArguments(args);
        return fragment;
    }
    public PreMatchReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_match = new MatchSchedule.Match( getArguments().getString(ARG_MATCH) );
            m_statsEngine = (StatsEngine) getArguments().getSerializable(ARG_STATS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_view =  inflater.inflate(R.layout.fragment_pre_match_report, container, false);

        // stick all the gearDeliveryData on the screen

        // by the way PrimaryTab is written, m_match.matchNo will always be populated
        ((TextView) m_view.findViewById(R.id.titleTV) ).setText("Pre-Match Report for Match #" + m_match.getMatchNo());

        if (m_match.getBlue1() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.blue1TV) ).setText(m_match.getBlue1()+"");

        if (m_match.getBlue2() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.blue2TV)).setText(m_match.getBlue2()+"");

        if (m_match.getBlue3() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.blue3TV)).setText(m_match.getBlue3()+"");

        if (m_match.getRed1() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.red1TV)).setText(m_match.getRed1()+"");

        if (m_match.getRed2() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.red2TV)).setText(m_match.getRed2()+"");

        if (m_match.getRed3() != 0) // else leave the default text
            ((TextView) m_view.findViewById(R.id.red3TV)).setText(m_match.getRed3()+"");


        // if the match has been played yet
        if (m_match.getBlueScore() >= 0 && m_match.getRedScore() >= 0) {
            ((TextView) m_view.findViewById(R.id.blueScoreTV)).setText(m_match.getBlueScore()+"");
            ((TextView) m_view.findViewById(R.id.redScoreTV)).setText(m_match.getRedScore()+"");
            ((TextView) m_view.findViewById(R.id.predicted_finalTV)).setText("(final)");
            ((TextView) m_view.findViewById(R.id.averagePerTeam)).setText("(post match average)");
        } else {
            // display the OPR-predicted score instead
            displayOprPredection();
            ((TextView) m_view.findViewById(R.id.averagePerTeam)).setText("(pre match average)");
        }


        TeamStatsReport blue1TSR = m_statsEngine.getTeamStatsReport(m_match.getBlue1());
        TeamStatsReport blue2TSR = m_statsEngine.getTeamStatsReport(m_match.getBlue2());
        TeamStatsReport blue3TSR = m_statsEngine.getTeamStatsReport(m_match.getBlue3());
        TeamStatsReport red1TSR = m_statsEngine.getTeamStatsReport(m_match.getRed1());
        TeamStatsReport red2TSR = m_statsEngine.getTeamStatsReport(m_match.getRed2());
        TeamStatsReport red3TSR = m_statsEngine.getTeamStatsReport(m_match.getRed3());


        // TODO: TODO:
//        double bluegearsavg = blue1TSR.teleop_gearsDelivered_avgPerMatch +
//                blue2TSR.teleop_gearsDelivered_avgPerMatch + blue3TSR.teleop_gearsDelivered_avgPerMatch;
//        double redgearsavg = red1TSR.teleop_gearsDelivered_avgPerMatch +
//                red2TSR.teleop_gearsDelivered_avgPerMatch + red3TSR.teleop_gearsDelivered_avgPerMatch;
//        double bluefuelavg = blue1TSR.teleop_fuelScoredHigh_avgPerMatch + blue1TSR.teleop_fuelScoredLow_avgPerMatch +
//                blue2TSR.teleop_fuelScoredHigh_avgPerMatch + blue2TSR.teleop_fuelScoredLow_avgPerMatch +
//                blue3TSR.teleop_fuelScoredHigh_avgPerMatch + blue3TSR.teleop_fuelScoredLow_avgPerMatch;
//        double redfuelavg = red1TSR.teleop_fuelScoredHigh_avgPerMatch + red1TSR.teleop_fuelScoredLow_avgPerMatch +
//                red2TSR.teleop_fuelScoredHigh_avgPerMatch + red3TSR.teleop_fuelScoredLow_avgPerMatch +
//                red3TSR.teleop_fuelScoredHigh_avgPerMatch + red3TSR.teleop_fuelScoredLow_avgPerMatch;
//        double blueclimbsavg = blue1TSR.climbSuccesses + blue2TSR.climbSuccesses + blue3TSR.climbSuccesses;
//        double redclimbsavg = red1TSR.climbSuccesses + red2TSR.climbSuccesses + red3TSR.climbSuccesses;
//
//        bluegearsavg/= 3;
//        redgearsavg/= 3;
//        bluefuelavg/= 3;
//        redfuelavg/= 3;
//        blueclimbsavg/= 3;
//        redclimbsavg/= 3;
//
//        ((TextView) m_view.findViewById(R.id.gearsBlueTV)).setText(Math.round(bluegearsavg) + "");
//        ((TextView) m_view.findViewById(R.id.gearsRedTV)).setText(Math.round(redgearsavg) + "");
//        ((TextView) m_view.findViewById(R.id.fuelBlueTV)).setText(Math.round(bluefuelavg) + "");
//        ((TextView) m_view.findViewById(R.id.fuelRedTV)).setText(Math.round(redfuelavg) + "");
//        ((TextView) m_view.findViewById(R.id.climbsBlueTV)).setText(Math.round(blueclimbsavg) + "");
//        ((TextView) m_view.findViewById(R.id.climbsRedTV)).setText(Math.round(redclimbsavg) + "");

        return m_view;
    }

    /**
     * If this match has not yet been played, display a prediction based on OPRs
     */
    private void displayOprPredection() {

        // TODO:
//        Map<Integer, Double> oprs = m_statsEngine.getOPRs();
//
//        float bluePredScore=0, redPredScore=0;
//
//        bluePredScore += oprs.get( m_match.getBlue1() );
//
//        bluePredScore += oprs.get( m_match.getBlue2() );
//
//        bluePredScore += oprs.get( m_match.getBlue3() );
//
//        redPredScore += oprs.get( m_match.getRed1() );
//
//        redPredScore += oprs.get( m_match.getRed2() );
//
//        redPredScore += oprs.get(m_match.getRed3());
//
//
//        ((TextView) m_view.findViewById(R.id.blueScoreTV)).setText( (int)bluePredScore +"");
//        ((TextView) m_view.findViewById(R.id.redScoreTV)).setText((int)redPredScore +"");
//        ((TextView) m_view.findViewById(R.id.predicted_finalTV)).setText("(predicted)");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

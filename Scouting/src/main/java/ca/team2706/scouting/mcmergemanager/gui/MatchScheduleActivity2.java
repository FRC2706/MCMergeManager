package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.MatchSchedule;

/**
 * Created by mike on 31/01/16.
 */
public class MatchScheduleActivity2 extends ListActivity {

    Activity activity;
    MatchSchedule matchSchedule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        // Unbundle the match scedule from intent
        Intent intent = getIntent();
        String matchScheduleStr = intent.getStringExtra("ca.team2706.scouting.matchSchedule");
        int teamNo = intent.getIntExtra("ca.team2706.scouting.teamNo", - 1);

        TextView titleView = new TextView(activity);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);

        matchSchedule = new MatchSchedule(matchScheduleStr);
        if(teamNo == -1)
            titleView.setText("Full Scehdule");
        else {
            matchSchedule = matchSchedule.filterByTeam(teamNo);
            titleView.setText("Scehdule for team: " + teamNo);
        }


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // do nothing -- not clickable
    }

    private class MatchesArrayAdapter extends ArrayAdapter<MatchSchedule.Match> {

        private MatchSchedule matchSchedule;

        MatchesArrayAdapter(Context context,
                            int textViewResourceId,
                            MatchSchedule matchSchedule) {
            super(context, textViewResourceId, matchSchedule.getMatches());
            this.matchSchedule = matchSchedule;
        }

        public MatchSchedule.Match getItem(int i) {
            return matchSchedule.getMatchNo(i);
        }

        @Override
        @NonNull
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = activity.getLayoutInflater();
            if(convertView == null)
                convertView = inflater.inflate(R.layout.schedule_row_layout, null);

            final MatchSchedule.Match match = matchSchedule.getMatchNo(position);
            if(match != null) {
                TextView matchNoTV=(TextView) convertView.findViewById(R.id.schedule_matchNoTV);
                matchNoTV.setText( String.format("%3s  |", match.getMatchNo()) );

                TextView blueAllianceTV=(TextView) convertView.findViewById(R.id.schedule_blueAllianceTV);
                blueAllianceTV.setText( String.format("%4s, %4s, %4s", match.getBlue1(), match.getBlue2(),match.getBlue3()) );

                TextView redAllianceTV=(TextView) convertView.findViewById(R.id.schedule_redAllianceTV);
                redAllianceTV.setText( String.format("%4s, %4s, %4s",match.getRed1(), match.getRed2(), match.getRed3()) );

                TextView blueScoreTV=(TextView) convertView.findViewById(R.id.schedule_blueScoreTV);
                TextView redScoreTV=(TextView) convertView.findViewById(R.id.schedule_redScoreTV);
                if (match.getBlueScore() >= 0 && match.getRedScore() >= 0) {
                    blueScoreTV.setText(String.format("%4d", match.getBlueScore()));
                    redScoreTV.setText( String.format("%4d", match.getRedScore()) );
                } else {
                    blueScoreTV.setText("    ");
                    redScoreTV.setText ("    ");
                }
            }

            return convertView;
        }
    }
}



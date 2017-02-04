package ca.team2706.scouting.mcmergemanager.backend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mike on 31/01/16.
 */
public class MatchSchedule implements Serializable {

    public static class Match implements Comparable<Match>, Serializable {
        private int matchNo;

        private int blue1;
        private int blue2;
        private int blue3;
        private int red1;
        private int red2;
        private int red3;

        // = -1 if the match has not been played yet.
        private int blueScore;
        private int redScore;


        // getters - this is how other classes will access the data (they can read it, but not change it)
        public int getMatchNo() { return matchNo; }
        public int getBlue1() { return blue1; }
        public int getBlue2() { return blue2; }
        public int getBlue3() { return blue3; }
        public int getRed1() { return red1; }
        public int getRed2() { return red2; }
        public int getRed3() { return red3; }
        public int getBlueScore() { return blueScore; }
        public int getRedScore() { return redScore; }

        // setters - this is how other classes will update data, or make a Match if they only know a few fields
        public void setMatchNo(int matchNo) { this.matchNo = matchNo; }
        public void setRed1(int red1) { this.red1 = red1; }
        public void setRed2(int red2) { this.red2 = red2; }
        public void setRed3(int red3) { this.red3 = red3; }
        public void setBlue1(int blue1) { this.blue1 = blue1; }
        public void setBlue2(int blue2) { this.blue2 = blue2; }
        public void setBlue3(int blue3) { this.blue3 = blue3; }
        public void setRedScore(int redScore) { this.redScore = redScore; }
        public void setBlueScore(int blueScore) { this.blueScore = blueScore; }


        /** Default empty constructor **/
        public Match() {

        }

        /** De-serializing constructor **/
        public Match(String serializedMatch) {
            try {
                String[] tokens = serializedMatch.split(",");
                matchNo = Integer.parseInt(tokens[0]);
                blue1 = Integer.parseInt(tokens[1]);
                blue2 = Integer.parseInt(tokens[2]);
                blue3 = Integer.parseInt(tokens[3]);
                red1 = Integer.parseInt(tokens[4]);
                red2 = Integer.parseInt(tokens[5]);
                red3 = Integer.parseInt(tokens[6]);
                blueScore = Integer.parseInt(tokens[7]);
                redScore = Integer.parseInt(tokens[8]);

            } catch (Exception e) {
                // the data was in the wrong format, empty everything out
                matchNo = blue1 = blue2 = blue3 = red1 = red2 = red3 = 0;
                blueScore = redScore = -1;
            }
        }

        @Override
        public int compareTo(Match another) {
            return this.getMatchNo() - another.getMatchNo();
        }

        @Override
        public String toString(){
            return matchNo+","+blue1+","+blue2+","+blue3+","+red1+","+red2+","+red3+","+blueScore+","+redScore;
        }
    }

    private String matchScheduleJSONstr;

    private List<Match> matches = new ArrayList<Match>();

    public List<Match> getMatches(){ return matches; }


    public List<String> getTeamNumsAtEvent() {

        // TODO placeholder

        ArrayList<String> list = new ArrayList<>();
        list.add("2706");
        return list;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public Match getMatchNo(int i){ return matches.get(i); }


    /** Constructors **/
    public MatchSchedule() {
        super();
    }

    /**
     * A costructor that parses the schedule as deliveryStatus by TheBlueAlliance.
     * @param jsonSchedule the match schedule data as returned by thebluealliance.com
     */
    public MatchSchedule(String jsonSchedule) {
        super();
        parseTBAData(jsonSchedule);
    }

    /**
     * Return a new MatchSchedule object only containing matches that the given team is in,
     */
    public MatchSchedule filterByTeam(int teamNo) {
        MatchSchedule filteredSchedule = new MatchSchedule();

        for(Match m : matches) {
            if (m.getRed1() == teamNo || m.getRed2() == teamNo || m.getRed3() == teamNo
                    || m.getBlue1() == teamNo || m.getBlue2() == teamNo || m.getBlue3() == teamNo )
                filteredSchedule.addMatch(m);
        }

        return filteredSchedule;
    }

    public String toString() { return matchScheduleJSONstr; }

    public void parseTBAData(String jsonSchedule) {

        matchScheduleJSONstr = jsonSchedule;

        JSONArray jsonArr;
        try {
            jsonArr = new JSONArray(jsonSchedule);

            // loop over individual matches
            for(int i=0;i<(jsonArr.length());i++)
            {
                JSONObject jsonMatch=jsonArr.getJSONObject(i);

                // I'm only interested in qualification matches
                String comp_level = jsonMatch.getString("comp_level");
                if (!comp_level.equals("qm")) {
                    continue;
                }

                Match match = new Match();

                match.setMatchNo(jsonMatch.getInt("match_number"));

                JSONObject jsonBlueAlliance = jsonMatch.getJSONObject("alliances").getJSONObject("blue");
                JSONObject jsonRedAlliance = jsonMatch.getJSONObject("alliances").getJSONObject("red");

                try {
                    match.setBlueScore(jsonBlueAlliance.getInt("score"));
                    match.setRedScore(jsonRedAlliance.getInt("score"));
                } catch (Exception e) {
                    match.setBlueScore(-1);
                    match.setRedScore(-1);
                }

                // TODO: Just for testing
                if (match.getMatchNo() == 3) {
                    match.setBlueScore(-1);
                    match.setRedScore(-1);
                }


                JSONArray blueTeams = jsonBlueAlliance.getJSONArray("teams");
                match.setBlue1(Integer.parseInt(blueTeams.getString(0).substring(3))); // TBA gives it to us as "frc2706", so skip the first 3 characters
                match.setBlue2(Integer.parseInt(blueTeams.getString(1).substring(3)));
                match.setBlue3(Integer.parseInt(blueTeams.getString(2).substring(3)));

                JSONArray redTeams = jsonRedAlliance.getJSONArray("teams");
                match.setRed1(Integer.parseInt(redTeams.getString(0).substring(3)));
                match.setRed2(Integer.parseInt(redTeams.getString(1).substring(3)));
                match.setRed3(Integer.parseInt(redTeams.getString(2).substring(3)));

                matches.add(match);
            }

            Collections.sort(matches);

        } catch(JSONException e) {
            // something went wrong
            Log.e("MCMergeManager", "Failed to parse the match schedule from thebluealliance. Maybe the data is not valid json?");
            return;
        }
    }

}

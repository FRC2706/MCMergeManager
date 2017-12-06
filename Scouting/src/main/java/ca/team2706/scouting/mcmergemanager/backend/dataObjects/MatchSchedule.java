package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.App;

/**
 * Created by mike on 31/01/16.
 */
public class MatchSchedule implements Serializable {

    private static final String JSONKEY_matchScheduleJSONstr = "matchScheduleJSONstr";
    private static final String JSONKEP_teamListInt = "teamListInt";

    private String matchScheduleJSONstr = "";

    private List<Match> matches = new ArrayList<>();

    /** List of teams at this event
     * doing it as a list of Integers so we can sort it numerically, rather than alphabetically
     * before turning it into Strings (ie we want order {47, 256, 2706}, not {256, 2706, 47}).
     **/
    private ArrayList<Integer> teamListInt = new ArrayList<>();

    /** Constructors **/
    public MatchSchedule() {
        super();
    }


    public MatchSchedule(String serializedMatchSchedule) {
        super();

        try {
            JSONObject jsonObj = new JSONObject(serializedMatchSchedule);

            parseTBASchedule(jsonObj.getString(JSONKEY_matchScheduleJSONstr));

            String[] teamNos = jsonObj.getString(JSONKEP_teamListInt).split(",");
            for(String teamNoStr : teamNos) {
                try {
                    if(!teamListInt.contains(Integer.valueOf(teamNoStr)))
                        teamListInt.add(Integer.valueOf(teamNoStr));
                } catch (NumberFormatException e) {
                    // just pass
                }
            }

        } catch (JSONException e) {
            Log.e("MCMergeManager", "Failed to deserialize MatchSchedule.",e);
        }
    }

    public static MatchSchedule newFromJsonSchedule(String jsonSchedule) {
        MatchSchedule matchSchedule = new MatchSchedule();
        matchSchedule.parseTBASchedule(jsonSchedule);
        return matchSchedule;
    }

    public List<Match> getMatches(){ return matches; }


    public void addMatch(Match match) {
        matches.add(match);
    }

    public Match getMatchNo(int i){ return matches.get(i); }


    /**
     * Takes a json string containing a TBA Event:TeamsList and combines it with the list of teams from the schedule.
     */
    public void addToListOfTeamsAtEvent(String jsonTeamsList) {
        parseTBATeamsList(jsonTeamsList);
    }


    /**
     * Takes a list of team numbers and combines it with the list of teams from the schedule.
     */
    public void addToListOfTeamsAtEvent(List<String> teamsList) {
        for(String teamNoStr : teamsList) {
            int teamNo = Integer.valueOf(teamNoStr);
            if (!teamListInt.contains(teamNo)) {
                teamListInt.add(teamNo);
            }
        }
    }

    public List<String> getTeamNumsAtEvent() {
        Collections.sort(teamListInt);

        List<String> teamListStr = new ArrayList<>();
        for(Integer i: teamListInt)
        teamListStr.add(i.toString());

        return teamListStr;
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

    public String toString() {
        // There's gotta be a simpler way to serialize this...
        JSONObject jsonObject = new JSONObject();

        StringBuilder teamListSerializedStrBldr = new StringBuilder();
        for(int teamNo : teamListInt)
            teamListSerializedStrBldr.append(teamNo + ",");

        try {
            jsonObject.put(JSONKEY_matchScheduleJSONstr, matchScheduleJSONstr);
            jsonObject.put(JSONKEP_teamListInt, teamListSerializedStrBldr.toString());
        } catch (JSONException e) {
            Log.e("MCMergeManager", "Failed to serialize MatchSchedule.",e);
        }

        return jsonObject.toString();
    }


    private void parseTBASchedule(String jsonSchedule) {

        if(jsonSchedule == null)
            return;

        matchScheduleJSONstr = jsonSchedule;

        JSONArray jsonArr;
        try {
            jsonArr = new JSONArray(jsonSchedule);

            // Just for testing the Match Prediction, match #3 does not have a score
            // if we're in the test event from the previous year.
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
            String eventName = SP.getString(App.getContext().getResources().getString(R.string.PROPERTY_event), "<Not Set>");
            boolean testEvent = eventName.equals(App.getContext().getResources().getString(R.string.TBA_TEST_EVENT));

            // loop over individual matches
            for(int i=0;i<(jsonArr.length( ));i++)
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

                // Just for testing the Match Prediction, match #3 does not have a score
                // if we're in the test event from the previous year.
                if (testEvent && match.getMatchNo() == 3) {
                    match.setBlueScore(-1);
                    match.setRedScore(-1);
                }


                JSONArray blueTeams = jsonBlueAlliance.getJSONArray("team_keys");
                match.setBlue1(Integer.parseInt(blueTeams.getString(0).substring(3))); // TBA gives it to us as "frc2706", so skip the first 3 characters
                match.setBlue2(Integer.parseInt(blueTeams.getString(1).substring(3)));
                match.setBlue3(Integer.parseInt(blueTeams.getString(2).substring(3)));

                JSONArray redTeams = jsonRedAlliance.getJSONArray("team_keys");
                match.setRed1(Integer.parseInt(redTeams.getString(0).substring(3)));
                match.setRed2(Integer.parseInt(redTeams.getString(1).substring(3)));
                match.setRed3(Integer.parseInt(redTeams.getString(2).substring(3)));


                // Fill in the list of teams at this event
                for(int teamNo : match.getTeamNos()) {
                    if (!teamListInt.contains(teamNo)) {
                        teamListInt.add(teamNo);
                    }
                }

                matches.add(match);
            }

            Collections.sort(matches);

        } catch(JSONException e) {
            // something went wrong
            Log.e("MCMergeManager", "Failed to parse the match schedule from thebluealliance. Maybe the gearDeliveryData is not valid json?");
        }
    }


    private void parseTBATeamsList(String jsonTeamsList) {
        if(jsonTeamsList == null)
            return;

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonTeamsList);

            // Loop over each team
            for(int i = 0; i < jsonArray.length(); i++) {
                teamListInt.add(Integer.valueOf(jsonArray.get(i).toString().substring(3)));
            }

            Collections.sort(teamListInt);
        } catch(JSONException e) {
            Log.d("Error parsing json: ", e.toString());
        }
    }


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


        // getters - this is how other classes will access the gearDeliveryData (they can read it, but not change it)
        public int getMatchNo() { return matchNo; }
        public int getBlue1() { return blue1; }
        public int getBlue2() { return blue2; }
        public int getBlue3() { return blue3; }
        public int getRed1() { return red1; }
        public int getRed2() { return red2; }
        public int getRed3() { return red3; }
        public int getBlueScore() { return blueScore; }
        public int getRedScore() { return redScore; }

        int[] getTeamNos() {
            int[] arr = new int[6];
            arr[0] = blue1;
            arr[1] = blue2;
            arr[2] = blue3;
            arr[3] = red1;
            arr[4] = red2;
            arr[5] = red3;

            return arr;
        }

        // setters - this is how other classes will update gearDeliveryData, or make a Match if they only know a few fields
        void setMatchNo(int matchNo) { this.matchNo = matchNo; }
        void setRed1(int red1) { this.red1 = red1; }
        void setRed2(int red2) { this.red2 = red2; }
        void setRed3(int red3) { this.red3 = red3; }
        void setBlue1(int blue1) { this.blue1 = blue1; }
        void setBlue2(int blue2) { this.blue2 = blue2; }
        void setBlue3(int blue3) { this.blue3 = blue3; }
        void setRedScore(int redScore) { this.redScore = redScore; }
        void setBlueScore(int blueScore) { this.blueScore = blueScore; }


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
                // the gearDeliveryData was in the wrong format, empty everything out
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


}

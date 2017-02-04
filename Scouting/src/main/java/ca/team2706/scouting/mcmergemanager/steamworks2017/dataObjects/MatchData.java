package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.util.ArrayList;

public class MatchData {

    // Member Variables
    public ArrayList<Match> matches;

    /** Empty constructor **/
    public MatchData() {
        matches = new ArrayList<>();
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public MatchData filterByTeam(int teamNo) {

        MatchData matchData = new MatchData();

        for(Match match : matches) {
            if (match.preGameObject.teamNumber == teamNo)
                matchData.addMatch(match);
        }
        return matchData;
    }
}
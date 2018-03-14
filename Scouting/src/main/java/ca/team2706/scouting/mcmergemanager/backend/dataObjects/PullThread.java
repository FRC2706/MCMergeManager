package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import ca.team2706.scouting.mcmergemanager.backend.WebServerUtils;

/**
 * Created by Daniel Wall on 2018-03-05.
 */

public class PullThread extends Thread {

    private String matchKey;
    private String competitoin;

    private String response;

    public PullThread(String competition, String matchKey) {
        this.matchKey = matchKey;
        this.competitoin = competition;
    }

    @Override
    public void run() {
        response = WebServerUtils.getDataFromServer("matches/show.json?competition=" + competitoin + "&match=" + matchKey);
    }

    public String getResponse() {
        return response;
    }
}

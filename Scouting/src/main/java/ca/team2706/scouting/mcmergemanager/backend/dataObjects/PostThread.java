package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.backend.WebServerUtils;

/**
 * Created by Daniel Wall on 2018-02-26.
 */

public class PostThread extends Thread {

    private boolean posted;

    private String matchKey;
    private String teamNumber;
    private String goal;
    private String success;
    private String startTime;
    private String endTime;
    private String extra;


    public PostThread(String matchKey, String teamNumber, String extra, String success,
                      String startTime, String endTime, String goal) {
        this.matchKey = matchKey;
        this.teamNumber = teamNumber;
        this.goal = goal;
        this.success = success;
        this.startTime = startTime;
        this.endTime = endTime;
        this.extra = extra;
    }

    @Override
    public void run() {
        if(WebServerUtils.postMatchEvent(matchKey, teamNumber, goal, success, startTime, endTime, extra)) {
            posted = true;
        } else {
            posted = false;
        }
    }

    public boolean getPosted() {
        return posted;
    }
}

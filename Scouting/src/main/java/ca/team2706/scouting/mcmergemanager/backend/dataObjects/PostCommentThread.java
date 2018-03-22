package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.team2706.scouting.mcmergemanager.backend.WebServerUtils;

/**
 * Created by Daniel Wall on 2018-03-15.
 */

public class PostCommentThread extends Thread {

    public boolean success;

    int teamNumber;
    String message;

    public PostCommentThread(int teamNumber, String message) {
        this.teamNumber = teamNumber;
        this.message = message;
    }

    public void run() {
        if(!WebServerUtils.postCommentToServer(teamNumber, message))
            success = false;
        else
            success = true;
    }

    public JSONObject getComment() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("number", teamNumber);
        obj.put("comment", message);

        return obj;
    }
}

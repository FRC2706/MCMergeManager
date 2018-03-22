package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import android.util.Log;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Merge on 2017-12-06.
 */

public class CommentList {

    /*Example JSON file
    "{
    "name":"Merge Robotics",
    "number":2706,
    "opr":0,
    "dpr":0,
    "comments":[
        {
            "body":"Test text",
        },
        {
            "body":"Test text2",
        }
    ],
    "pictures":[
        {
            "picture_type":"test",
            "taken_at":null,
            "link":"http://beancode.org/eastereggs/help.php"
        }
    ]
}"
    */

    private ArrayList<String> comments;
    private int teamNumber;

    public static final String JSONKEY_TEAMNO = "number";
    public static final String JSONKEY_COMMENT = "comments";

    /**
     * Constructor
     * Create new comment list for @param teamNumber
     */

    public CommentList(int teamNumber) {
        this.teamNumber = teamNumber;
        this.comments = new ArrayList<>();
    }

    /**
     * Constructor
     * Create this object from some JSON data
     */
    public CommentList(JSONObject jsonData) throws JSONException {
        // Construct a JSONOBJ
        comments = new ArrayList<>();

        if(jsonData != null) {
            this.teamNumber = (int) jsonData.get(JSONKEY_TEAMNO);
            JSONArray jsonArray = jsonData.getJSONArray(JSONKEY_COMMENT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                comments.add(jsonObject.get("body").toString());
            }
        }
    }

    public CommentList(JSONArray arr) {
        comments = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++) {
            try {
                comments.add(arr.getJSONObject(i).getString("body"));
            } catch(JSONException e) {
                Log.d("Err saving comments", e.toString());
            }
        }
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    // Pack the comments and team number into JSON and return them
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < comments.size(); i++) {
            JSONObject commentHolder = new JSONObject();
            commentHolder.put("body", comments.get(i));
            jsonArray.put(commentHolder);
        }

        jsonObject.put(JSONKEY_TEAMNO, teamNumber);
        jsonObject.put(JSONKEY_COMMENT, jsonArray);

        return jsonObject;
    }
}

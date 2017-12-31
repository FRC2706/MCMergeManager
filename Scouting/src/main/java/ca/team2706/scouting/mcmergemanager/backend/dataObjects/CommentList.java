package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import android.util.Log;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Comment;
import 	org.json.JSONArray;
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

    private ArrayList<String> comments = new ArrayList<>();
    private int teamNumber;

    public static final String JSONKEY_TEAMNO = "number";
    public static final String JSONKEY_COMMENT = "comments";

    /** Constructor
     *  Create new comment list for @param teamNumber
     */

    public CommentList(int teamNumber) {
        this.teamNumber = teamNumber;
        this.comments = new ArrayList<>();
    }

    /** Constructor
     *  Create this object from some JSON data */
    public CommentList(JSONObject jsonData) {
        // Construct a JSONOBJ
        try {
            this.teamNumber = (int) jsonData.get(JSONKEY_TEAMNO);
            JSONArray jsonArray = jsonData.getJSONArray(JSONKEY_COMMENT);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                comments.add(jsonObject.get("body").toString());
            }

        } catch(Exception e){
            Log.d("JSON Parsing error: ", e.getMessage());
        }

    }



    public ArrayList<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    // Pack the comments and team number into JSON and return them
    public JSONObject getJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        JSONObject commentHolder = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < comments.size(); i++) {
            commentHolder.put("body", comments.get(i));

            jsonArray.put(commentHolder);
        }

        jsonObject.put(JSONKEY_TEAMNO, teamNumber);
        jsonObject.put(JSONKEY_COMMENT, jsonArray);

        return jsonObject;


    }
}

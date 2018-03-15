package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import android.content.Context;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.WebServerUtils;

/**
 * Created by Merge on 2018-01-26.
 */

public class CommentListener {


    private CommentListener() {
        // This is a static class that should never be instantiated
    }

    public static void saveComment(int keyCode, KeyEvent event, final EditText comment, int teamNum, EditText teamNumber, View view, Context context) {
        if (teamNum == -1) {
            try {
                teamNum = Integer.parseInt(teamNumber.getText().toString());
                Selection.setSelection(teamNumber.getText(), teamNumber.getSelectionStart());
                teamNumber.requestFocus();
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

        }

        if (teamNum == -1) {
            Toast.makeText(context, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
        } else {
            final CommentList commentList = new CommentList(teamNum);

            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && comment.getId() == R.id.comment) {
                commentList.addComment(comment.getText().toString());

                // Save comment and try to post
                FileUtils.saveTeamCommentsAtEndOfFile(commentList);

                final int postTeamNumber = teamNum; // <--- Used because of how threads work and finals and gahh!!
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!WebServerUtils.postCommentToServer(postTeamNumber, comment.getText().toString()))
                                FileUtils.saveUnpostedComment(commentList.getJson());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

            comment.setText("");
            teamNumber.setText("");
        }

    }

    // This method returns the teamNum and moves the cursor
    public static int getTeamNum(int keyCode, KeyEvent event, EditText teamNumber, EditText comment) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && comment.getId() == R.id.teamNumber) {
            Integer teamNumInt = null;
            try {
                teamNumInt = Integer.parseInt(teamNumber.getText().toString());

                return teamNumInt;
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }
        return -1;
    }
}

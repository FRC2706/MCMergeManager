package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Comment;

/**
 * Created by Merge on 2018-01-26.
 */

public class CommentListener {
    private CommentListener(){
        // This is a static class that should never be instantiated
    }

    public static CommentList getComment(View v, int keyCode, KeyEvent event, int teamNum, EditText comment, Context c, CommentList commentList) {

//        EditText teamNumber = (EditText)v.findViewById(R.id.teamNumber);
//        EditText teamText = (EditText)v.findViewById(R.id.comment);


//        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER &&  teamNum.getId() == R.id.teamNumber)
//        {
//            Integer teamNumInt = null;
//
//
//            try {
//                teamNumInt = Integer.parseInt(teamNum.getText().toString());
//            } catch(NumberFormatException nfe) {
//                System.out.println("Could not parse " + nfe);
//            }
//            if(teamNumInt != null) {
//                CommentList cl = new CommentList(teamNumInt);
//            } else {
//                Toast.makeText(c, "Please enter a team number.", Toast.LENGTH_SHORT).show();
//            }
//            return cl;
//        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && comment.getId() == R.id.comment) {


        }

        return null;
    }
}

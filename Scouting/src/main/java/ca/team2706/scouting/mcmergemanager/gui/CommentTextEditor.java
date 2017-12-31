package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by Merge on 2017-11-15.
 * Mostly copied over from GetTeamNumberDialog.java
 */

public class CommentTextEditor {
    private Activity launchActivity;
    private EditText editText;
    private String title;
    private String inputHint;
    private String inputResult;
    public boolean accepted = false;
    public boolean canceled = false;

    public CommentTextEditor(String title, String inputHint, Activity launchActivity) {
        this.title = title;
        this.inputHint = inputHint;
        this.launchActivity = launchActivity;
    }

    public String getComment(){
        return inputHint;
    }

    public void displayAlertDialog() {
        LayoutInflater inflater = launchActivity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_write_comment, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(launchActivity);

        alert.setTitle(title);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        //this stuff gets the edittext from the view and sets the hint and the inputtype
        editText =  (EditText) alertLayout.findViewById(R.id.inputHint);
        editText.setHint(inputHint);


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canceled = true;
            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accepted = true;
                inputResult = editText.getText().toString();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }
}




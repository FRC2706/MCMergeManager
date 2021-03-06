package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.team2706.scouting.mcmergemanager.R;

// TODO: Should this be removed as it has references
public class DisplayAlertDialog {
    public static int inputType;
    public static boolean accepted = false;
    public static String inputResult;
    public Context launchActivity;
    public String title;
    public String inputHint;
    public View edit;
    public EditText editText;

    // INPUT TYPE: 0 = STRING, 1 = NUMBERS, 2 = PASSWORD note: currently busted lol
    public DisplayAlertDialog(String title, String inputHint, int inputType, Context launchActivity) {
        this.title = title;
        this.inputHint = inputHint;
        DisplayAlertDialog.inputType = inputType;
        this.launchActivity = launchActivity;

    }

    public void displayAlertDialog() {
        Activity activity = (Activity) launchActivity;
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(launchActivity);

        alert.setTitle(title);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        //this stuff gets the edittext from the view and sets the hint and the inputtype
        edit = alertLayout.findViewById(R.id.inputHint);
        if (edit instanceof EditText) {
            editText = (EditText) edit;
            editText.setHint(inputHint);
            Log.d("inputType", Integer.toString(inputType));
            switch (inputType) {
                case 0:
                    return;
                case 1:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    Log.d("kk", "here");

            }


            Log.d("editing hint", "you got that");

        }


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("boo", "boo");
                        accepted = true;
                        inputResult = editText.getText().toString();
                    }
                }

        );
        AlertDialog dialog = alert.create();
        dialog.show();
    }

}


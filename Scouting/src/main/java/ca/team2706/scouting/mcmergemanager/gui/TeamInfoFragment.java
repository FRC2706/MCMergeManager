package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtils;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentList;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.PhotoRequester;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeamStatsActivity;


public class TeamInfoFragment extends Fragment
        implements PhotoRequester {

    private String comments;
    private int m_teamNumber;
    private View m_view;
    private String textViewPerformanceString;
    private String nicknameString;
    public String name;
    public AlertDialog.Builder alert;

    public TeamStatsReport mTeamStatsReport;

    public TeamInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        m_view = inflater.inflate(R.layout.fragment_team_info, null);

        final Bundle args = getArguments();

        if(args.get("teamNumber") != null) {
            m_teamNumber = (int) args.get("teamNumber");
            FileUtils.getTeamPhotos(m_teamNumber, this);

            Runnable getStuff = new Runnable() {
                public void run() {
                    textViewPerformanceString = BlueAllianceUtils.getBlueAllianceDateForTeam(m_teamNumber);

                    Activity activity = getActivity();
                    if (activity != null) {
                        // Get the nickname of the team
                        if (BlueAllianceUtils.checkInternetPermissions(activity)) {
                            nicknameString = BlueAllianceUtils.getBlueAllianceData("nickname", "team/frc" + m_teamNumber);
                        }

                        // Update the ui text
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textViewPerformance = (TextView) m_view.findViewById(R.id.textViewPerformance);
                                textViewPerformance.setText(textViewPerformanceString);

                                TextView nicknameTV = (TextView) m_view.findViewById(R.id.nicknameTV);
                                nicknameTV.setText(nicknameString);
                            }
                        });
                    }
                }
            };
            Thread getStuffThread = new Thread(getStuff);
            getStuffThread.start();

            mTeamStatsReport = (TeamStatsReport) args.getSerializable(getString(R.string.EXTRA_TEAM_STATS_REPORT));
            if (mTeamStatsReport != null) {
                fillStatsData();
                m_view.findViewById(R.id.viewCyclesBtn).setEnabled(true);
            }


            // Set up the fullStatsBtn and viewStatsBtn

            Button fullStatsBtn = (Button) m_view.findViewById(R.id.fullStatsBtn);
            fullStatsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                //On click function
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), TeamStatsActivity.class);
                    intent.putExtra(getString(R.string.EXTRA_TEAM_STATS_REPORT), mTeamStatsReport);
                    startActivity(intent);
                }
            });

            Button viewCyclesBtn = (Button) m_view.findViewById(R.id.viewCyclesBtn);
            viewCyclesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                //On click function
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CyclesDisplayActivity.class);
                    intent.putExtra(getString(R.string.EXTRA_TEAM_STATS_REPORT), mTeamStatsReport);
                    startActivity(intent);
                }
            });


            try {
                JSONObject jsonObject = FileUtils.getTeamComments(m_teamNumber);
                if (jsonObject != null) {
                    CommentList commentList = new CommentList(jsonObject);
                    ArrayList<String> arrayList = commentList.getComments();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < arrayList.size(); i++) {
                        stringBuilder.append(arrayList.get(i) + "\n");
                    }
                    comments = stringBuilder.toString();
                    TextView notesTV = (TextView) m_view.findViewById(R.id.textViewNotes);
                    notesTV.setText(comments);

                }

            } catch (JSONException e) {
                comments = "";
            }




        }


        return m_view;
    }

    private void fillStatsData() {
        String statsText = "";

        statsText += "W/L/T:\t\t " + mTeamStatsReport.wins + "/" + mTeamStatsReport.losses + "/" + mTeamStatsReport.ties + "\n";
        statsText += "OPR:\t\t " + String.format("%.2f", mTeamStatsReport.OPR) + "\n";
        statsText += "Fav. cycle type: " + mTeamStatsReport.favouriteCycleType + "\n";
        statsText += "Fav. pickup location: " + mTeamStatsReport.favouritePickupLocation + "\n";

        TextView statsTV = (TextView) m_view.findViewById(R.id.statsTV);
        statsTV.setText(statsText);
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }



    public void updatePhotos(Bitmap[] photos) {
        if (photos.length == 0) {
            // there are no photos to display
            Toast.makeText(getActivity(), "No Images to display", Toast.LENGTH_SHORT).show();

            // we should probably like hide the whole photos bar or something.
            return;
        }

        //Crazy amount of work for single variable
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) m_view.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth,screenWidth);
        LinearLayout linearLayout = (LinearLayout) m_view.findViewById(R.id.teamPhotosLinearLayout);

        for (int i = 0; i < photos.length; i++) {
            ImageView imageView = new ImageView(m_view.getContext());
            imageView.setImageBitmap(photos[i]);
            imageView.setLayoutParams(layoutParams);
            linearLayout.addView(imageView);
        }
    }

}

package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtils;
import ca.team2706.scouting.mcmergemanager.backend.BlueAllianceUtilsV3;
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.PhotoRequester;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.Cycle;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.TeamStatsReport;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeamStatsActivity;


public class TeamInfoFragment extends Fragment
        implements PhotoRequester {

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
                    textViewPerformanceString = BlueAllianceUtilsV3.getBlueAllianceDateForTeam(m_teamNumber);
                }
//                public void run() {
//                    BlueAllianceUtils blueAllianceUtils = new BlueAllianceUtils(getActivity());
//                    textViewPerformanceString =  blueAllianceUtils.getBlueAllianceDataForTeam(m_teamNumber);
//                    Activity activity = getActivity();
//                    if(activity != null) {
//                        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                        if (activeNetwork != null) { // not connected to the internet
//                            nicknameString = BlueAllianceUtils.getBlueAllianceData("nickname", "https://www.thebluealliance.com/api/v2/team/frc" + m_teamNumber + "?X-TBA-App-Id=frc2706:mergemanager:v01/");
//                        }
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //stuff that updates ui
//                                TextView textViewPerformance = (TextView) m_view.findViewById(R.id.textViewPerformance);
//                                textViewPerformance.setText(textViewPerformanceString);
//
//                                TextView nicknameTV = (TextView) m_view.findViewById(R.id.nicknameTV);
//                                nicknameTV.setText(nicknameString);
//
//                            }
//                        });
//                    }
//
//                }
            };
            Thread getStuffThread = new Thread(getStuff);
            getStuffThread.start();

            mTeamStatsReport = (TeamStatsReport) args.getSerializable(getString(R.string.EXTRA_TEAM_STATS_REPORT));
            if (mTeamStatsReport != null) {
                fillStatsData();
                fillNotes();
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

        }
        if(args.getBoolean("accepted")) {

            Toast.makeText(getActivity(),"Downloading... Please Wait",Toast.LENGTH_LONG).show();

            LayoutInflater myinflater = getActivity().getLayoutInflater();
            View alertLayout = myinflater.inflate(R.layout.progressbar, null);
            alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Downloading...");
            alert.setView(alertLayout);
            alert.setCancelable(false);
            final AlertDialog dialog = alert.create();
            dialog.show();
            final ProgressBar progress = (ProgressBar) alertLayout.findViewById(R.id.progressBar);
            final TextView team = (TextView) alertLayout.findViewById(R.id.textViewTeam);
            progress.setMax(100);
            progress.setProgress(0);


            Runnable downloadStuff = new Runnable() {
                public void run() {
                    BlueAllianceUtils blueAllianceUtils = new BlueAllianceUtils(getActivity());
                    for(int i = 0; i < 3; i++)
                        switch(i) {

                            case 0:
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        team.setText("2015");
                                    }
                                });

                                blueAllianceUtils.downloadBlueAllianceDataForEvent(args.getString("inputResult"), args.getString("selectedYear"), progress, dialog, 2015);

                                break;
                            case 1:
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        team.setText("2014");
                                        progress.setProgress(0);
                                    }
                                });
                                blueAllianceUtils.downloadBlueAllianceDataForEvent(args.getString("inputResult"), args.getString("selectedYear"), progress,dialog,2014);

                                break;
                            case 2:
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        team.setText("2013");
                                        progress.setProgress(0);
                                    }
                                });

                                blueAllianceUtils.downloadBlueAllianceDataForEvent(args.getString("inputResult"), args.getString("selectedYear"), progress, dialog,2013);


                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });


                                break;
                        }

                }
            };
            Thread downloadStuffThread = new Thread(downloadStuff);
            downloadStuffThread.start();
            args.putBoolean("accepted", false);
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

    private void fillNotes() {
        TextView notesTV = (TextView) m_view.findViewById(R.id.textViewNotes);
        notesTV.setText(mTeamStatsReport.notes);
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

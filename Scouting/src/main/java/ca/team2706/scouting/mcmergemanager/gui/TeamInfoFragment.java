package ca.team2706.scouting.mcmergemanager.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
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
import ca.team2706.scouting.mcmergemanager.backend.FileUtils;
import ca.team2706.scouting.mcmergemanager.backend.interfaces.PhotoRequester;
import ca.team2706.scouting.mcmergemanager.stronghold2016.StatsEngine;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.TeleopScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeamStatsActivity;


public class TeamInfoFragment extends Fragment
        implements PhotoRequester {

    private int m_teamNumber;
    private View m_view;
    public FileUtils fileUtils;
    private String textViewPerformanceString;
    private String nicknameString;
    public String name;
    public AlertDialog.Builder alert;

    public StatsEngine.TeamStatsReport m_teamStatsReport;

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


        fileUtils = new FileUtils(getActivity());
        final Bundle args = getArguments();

        if(args.get("teamNumber") !=null ) {
            m_teamNumber = (int) args.get("teamNumber");
            fileUtils.getTeamPhotos(m_teamNumber, this);

            Runnable getStuff = new Runnable() {
                public void run() {
                    BlueAllianceUtils blueAllianceUtils = new BlueAllianceUtils(getActivity());
                    textViewPerformanceString =  blueAllianceUtils.getBlueAllianceDataForTeam(m_teamNumber);
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null) { // not connected to the internet
                        nicknameString = BlueAllianceUtils.getBlueAllianceData("nickname", "http://www.thebluealliance.com/api/v2/team/frc" + m_teamNumber + "?X-TBA-App-Id=frc2706:mergemanager:v01/");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //stuff that updates ui
                            TextView textViewPerformance = (TextView) m_view.findViewById(R.id.textViewPerformance);
                            textViewPerformance.setText(textViewPerformanceString);

                            TextView nicknameTV = (TextView) m_view.findViewById(R.id.nicknameTV);
                            nicknameTV.setText(nicknameString);

                        }
                    });

                }
            };
            Thread getStuffThread = new Thread(getStuff);
            getStuffThread.start();

            m_teamStatsReport = (StatsEngine.TeamStatsReport) args.getSerializable(getString(R.string.EXTRA_TEAM_STATS_REPORT));
            if (m_teamStatsReport != null) {
                fillStatsData();
                fillNotes();
                m_view.findViewById(R.id.fullStatsBtn).setEnabled(true);
            }




        }
        Log.d("accepted", "" + args.getBoolean("accepted"));
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

        Button fullStatsBtn = (Button) m_view.findViewById(R.id.fullStatsBtn);
        fullStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TeamStatsActivity.class);
                intent.putExtra(getString(R.string.EXTRA_TEAM_STATS_REPORT), m_teamStatsReport);
                startActivity(intent);
            }
        });




        return m_view;
    }

    private void fillStatsData() {
        String statsText = "";

        statsText += "W/L/T:\t\t " + m_teamStatsReport.wins + "/" + m_teamStatsReport.losses + "/" + m_teamStatsReport.ties + "\n";
        statsText += "OPR:\t\t " + String.format("%.2f",m_teamStatsReport.OPR) + "\n";
        statsText += "High Goals:\t\t " + m_teamStatsReport.numSuccHighShotsInTeleop + "\n";
        statsText += "Low Goals:\t\t " + m_teamStatsReport.numSuccLowShotsInTeleop + "\n";
        statsText += "Missed shots:\t\t " + m_teamStatsReport.missedTeleopShots.size() + "\n";
        statsText += "Scales:\t\t " + m_teamStatsReport.numSuccessfulScales + "\n";

        // comupte their favourite defense and ave breaches per match
        int max=0, fav=0, num=0;
        for(int i=1; i< TeleopScoutingObject.NUM_DEFENSES; i++) {
            if (m_teamStatsReport.defensesBreached[i] > max) {
                max = m_teamStatsReport.defensesBreached[i];
                fav = i;
            }
            num += m_teamStatsReport.defensesBreached[i];
        }
        statsText += "Fav Defense:\t\t " + TeleopScoutingObject.getDefenseName(fav) + "\n";

        if (m_teamStatsReport.numMatchesPlayed != 0)
            statsText += "Breaches per match:\t\t " + String.format("%.1f", (double) num / m_teamStatsReport.numMatchesPlayed);
        else
            statsText += "Breaches per match:\t\t 0";

        TextView statsTV = (TextView) m_view.findViewById(R.id.statsTV);
        statsTV.setText(statsText);
    }

    private void fillNotes() {
        TextView notesTV = (TextView) m_view.findViewById(R.id.textViewNotes);
        notesTV.setText(m_teamStatsReport.notes);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {

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


//    public void updateData(String[] matchResultsDataCSV, String[] matchScoutingDataCSV) {
//
//    }
//
//    public void updateMatchSchedule(MatchSchedule matchSchedule) {
//
//    }

}

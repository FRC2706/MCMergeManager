package ca.team2706.scouting.mcmergemanager.stronghold2016.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.BallPickup;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.BallShot;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.PreGameObject;
import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.TeleopScoutingObject;

public class TeleopScouting extends AppCompatActivity {
    Handler m_handler;
    Runnable m_handlerTask;
    Handler m_handlerDefending;
    Runnable m_handlerTaskDefending;
    private int remainTime = 135;

    public ArrayList<Integer> defensesBreached;
    public ArrayList<BallShot> ballsShot;
    public ArrayList<BallPickup> ballPickups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stronghold2016_activity_teleop_scouting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Spinner spinner = (Spinner) findViewById(R.id.defense_spinner);
        final TextView tvGameTime = (TextView) findViewById(R.id.textViewGameTime);

        defensesBreached = new ArrayList<>();
        ballsShot = new ArrayList<>();
        ballPickups = new ArrayList<>();
        m_handler = new Handler();

        m_handlerTask = new Runnable() {
            @Override
            public void run() {
                if (remainTime == 0) {
                    tvGameTime.setText("Game Over! Please Save and Return");
                } else {
                    remainTime--;
                    int minuets = remainTime / 60;
                    int remainSec = remainTime - minuets * 60;
                    String remainSecString;
                    if (remainSec < 10)
                        remainSecString = "0" + remainSec;
                    else
                        remainSecString = remainSec + "";

                    tvGameTime.setText(minuets + ":" + remainSecString);
                    m_handler.postDelayed(m_handlerTask, 1000);  // 1 second delay
                }
            }
        };
        m_handlerTask.run();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.defense_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0 )
                    defensesBreached.add(position);

                spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ImageView imageViewMap = (ImageView) findViewById(R.id.map);
        imageViewMap.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    RelativeLayout imgHolder = (RelativeLayout) findViewById(R.id.relativeLayoutMap);

                    ImageView pointerImageView = new ImageView(TeleopScouting.this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
                    params.leftMargin = (int) event.getX() + (int) imageViewMap.getX() - 25;
                    params.topMargin = (int) event.getY() + (int) imageViewMap.getY() - 26;

                    pointerImageView.setImageResource(R.drawable.pinicon);
                    pointerImageView.setLayoutParams(params);
                /*    pointerImageView.setX(event.getX());
                    pointerImageView.setY(event.getY());*/

                    imgHolder.addView(pointerImageView);
                    Timer timer = new Timer();
                    CheckVarShot checkVar = new CheckVarShot();
                    checkVar.x = (int) event.getX();
                    checkVar.y = (int) event.getY();
                    checkVar.t = new TeleopTimerAlertDialog("Shooting...", TeleopScouting.this, "High Goal", BallShot.HIGH_GOAL, "Low Goal", BallShot.LOW_GOAL, "Cancel", BallShot.MISS);
                    timer.schedule(checkVar, 0, 1000);
                }
                return true;
            }


        });
    }

    //Button - Called in XML's onClick
    public void postGame(View view) {
        Intent intent = new Intent(this, PostGameActivity.class);
        Intent thisIntent = getIntent();
        intent.putExtra("PreGameData", (PreGameObject) thisIntent.getSerializableExtra("PreGameData"));
        intent.putExtra("AutoScoutingData", (AutoScoutingObject) thisIntent.getSerializableExtra("AutoScoutingData"));
        intent.putExtra("TeleopScoutingData", new TeleopScoutingObject(ballsShot, defensesBreached, defendingTime,ballPickups));
        startActivity(intent);
    }

    public void ballPickup(View view) {
        Timer timer = new Timer();
        CheckVarPickup checkVar = new CheckVarPickup();
        checkVar.t = new TeleopTimerAlertDialog("Picking up ball", TeleopScouting.this, "Ground", BallPickup.GROUND, "Wall", BallPickup.WALL, "Cancel", BallPickup.FAIL);
        timer.schedule(checkVar, 0, 1000);

    }

    public boolean isRunning = false;
    public double defendingTime = 0.0;

    public void defendingStart(View view) {
        isRunning = !isRunning;

        final TextView textViewDefending = (TextView) findViewById(R.id.textViewDefending);
        final TeleopTimerAlertDialog.UpTimer upTimer = new TeleopTimerAlertDialog.UpTimer();

        upTimer.startTime(150, 100, TeleopScouting.this);
        m_handlerDefending = new Handler();

        m_handlerTaskDefending = new Runnable() {
            @Override
            public void run() {
                NumberFormat formatter = new DecimalFormat("#0.0");
                textViewDefending.setText("" + formatter.format(upTimer.currentTime()));

                if (upTimer.currentTime() >= 150 || !isRunning) {
                    defendingTime += upTimer.currentTime();
                    upTimer.cancel();
                    textViewDefending.setText("Not Currently Defending");
                } else {
                    m_handlerDefending.postDelayed(m_handlerTaskDefending, 100);  // 1 second delay
                }

            }
        };
        m_handlerTaskDefending.run();
    }


    class CheckVarShot extends TimerTask {
        public int x;
        public int y;
        public TeleopTimerAlertDialog t;

        public void run() {

            if (t.canceled >= 0) {
                if (t.canceled == BallShot.MISS) // I'm using this as a cancel
                    this.cancel();

                ballsShot.add(new BallShot(x, y, t.upTimer.currentTime(), t.canceled));
                this.cancel();
            }

        }
    }


    class CheckVarPickup extends TimerTask {
        public TeleopTimerAlertDialog t;

        public void run() {

            if (t.canceled >= 0) {
                if (t.canceled == BallPickup.FAIL) // I'm using this as a cancel button
                    this.cancel();

                ballPickups.add(new BallPickup(t.canceled,t.upTimer.currentTime()));
                this.cancel();
            }

        }
    }
}

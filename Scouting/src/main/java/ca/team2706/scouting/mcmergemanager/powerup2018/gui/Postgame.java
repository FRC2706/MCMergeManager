package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.view.KeyEvent;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.backend.dataObjects.CommentListener;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.PostGameObject;
import android.view.View.OnKeyListener;

import static ca.team2706.scouting.mcmergemanager.backend.App.getContext;

/**
 * Created by LaraLim on 2018-02-12.
 */

public class Postgame extends AppCompatActivity {

    private PostGameObject postGameObject;
    public static int teamNum = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steamworks2017_activity_post_game);
    }

}

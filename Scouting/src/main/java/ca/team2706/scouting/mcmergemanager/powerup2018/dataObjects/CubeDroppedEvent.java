package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import android.app.DialogFragment;

/**
 * Created by Merge on 2018-02-10.
 */

public class CubeDroppedEvent extends Event {
    public static final String ID = "cube_dropped";

    public enum DropType{
        FUMBLE, EASY_PICKUP, LEFT_IT
    }

    public DropType dropType;

    public CubeDroppedEvent(double timestamp, DropType dropType) {
        super(timestamp);
        this.dropType = dropType;

    }

}

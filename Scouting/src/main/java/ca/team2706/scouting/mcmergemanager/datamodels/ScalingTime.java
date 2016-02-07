package ca.team2706.scouting.mcmergemanager.datamodels;

import java.io.Serializable;

/**
 * Created by MCSoftware on 2016-01-23.
 */
public class ScalingTime implements Serializable {
    public double time;
    public int completed;
    // 0 = completed, 1 = failed

    public ScalingTime() {

    }

    public ScalingTime(double time, int completed) {
        this.time = time;
        this.completed = completed;
    }
}
 

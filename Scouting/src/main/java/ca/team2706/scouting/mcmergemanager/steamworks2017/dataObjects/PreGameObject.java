package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.io.Serializable;

/**
 * Created by MCSoftware on 2016-01-22.
 */
//The Pre Game Object
public class PreGameObject implements Serializable {
    public int teamNumber;
    public int matchNumber;

    public PreGameObject() {

    }

    public PreGameObject(int teamNumber, int matchNumber) {
        this.teamNumber = teamNumber;
        this.matchNumber = matchNumber;
    }
}

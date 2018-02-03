package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

/**
 * Created by Merge on 2018-01-20.
 */

public class HumanPlayerScoutingObject {

    // Enum's for the Switches and the Scale
    // One switch, two switch, red switch, blue switch
    public enum redSwitch{BLUE, NEUTRAL, RED }
    public enum blueSwitch{BLUE, NEUTRAL, RED }
    public enum scale{BLUE, NEUTRAL, RED }


    // PowerUp bool's for both alliances.
    private boolean redLevitate;
    public boolean getRedLevitate() {
        return redLevitate;
    }
    public void setRedLevitate(boolean redLevitate) {
        this.redLevitate = redLevitate;
    }

    private boolean redBoost;
    public boolean isRedBoost() {
        return redBoost;
    }
    public void setRedBoost(boolean redBoost) {
        this.redBoost = redBoost;
    }

    private boolean redForce;
    public boolean isRedForce() {
        return redForce;
    }
    public void setRedForce(boolean redForce) {
        this.redForce = redForce;
    }

    private boolean blueLevitate;
    public boolean isBlueLevitate() {
        return blueLevitate;
    }
    public void setBlueLevitate(boolean blueLevitate) {
        this.blueLevitate = blueLevitate;
    }

    private boolean blueBoost;
    public boolean isBlueBoost() {
        return blueBoost;
    }
    public void setBlueBoost(boolean blueBoost) {
        this.blueBoost = blueBoost;
    }

    private boolean blueForce;
    public boolean isBlueForce() {
        return blueForce;
    }
    public void setBlueForce(boolean blueForce) {
        this.blueForce = blueForce;
    }

}

package ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects;

import java.io.Serializable;

/**
 * Created by Merge on 2018-01-20.
 */

public class AutoScoutingObject implements Serializable {

    private boolean crossedAutoLine;

    public boolean getCrossedAutoLine() {
        return crossedAutoLine;
    }

    public void setCrossedAutoLine(boolean crossedAutoLine) {
        this.crossedAutoLine = crossedAutoLine;
    }

    private boolean noAuto;

    public boolean getNoAuto() {
        return noAuto;
    }

    public void setNoAuto(boolean noAuto) {
        this.noAuto = noAuto;
    }

    private boolean autoMalfunction;

    public boolean getAutoMalfunction() {
        return autoMalfunction;
    }

    public void setAutoMalfunction(boolean autoMalfunction) {
        this.autoMalfunction = autoMalfunction;
    }

    public enum deliveryType {
        SWITCH, SCALE, EXCHANGE, NO_DELIVERY, FAILURE
    }

    private deliveryType deliveryType;

    public deliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(deliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public enum pickupType {
        STARTED_CUBE, GROUND, NO_PICKUP
    }

    private pickupType pickupType;

    public AutoScoutingObject.pickupType getPickupType() {
        return pickupType;
    }

    public void setPickupType(AutoScoutingObject.pickupType pickupType) {
        this.pickupType = pickupType;
    }





}

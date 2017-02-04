package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

/**
 * Created by dwall on 23/01/17.
 */

public class GearDelivevryEvent extends Event {

    public enum Lift {
        FEEDER_SIDE, CENTRE, BOILER_SIDE, NONE
    }

    public enum GearDeliveryStatus {
        DELIVERED, DROPPED_MOVING, DROPPED_DELIVERING;
    }

    public Lift lift;
    public GearDeliveryStatus deliveryStatus;


    public GearDelivevryEvent(double timestamp, GearDeliveryStatus deliveryStatus, Lift lift) {
        super(timestamp);

        this.deliveryStatus = deliveryStatus;
        this.lift = lift;
    }
}

package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TeleopScoutingObject {

    public static double MATCH_TIME = 0; // TODO: get match time in milliseconds?

//    public double fuelCycleTime; // TODO: [MikeO] ??
//    public double gearCycleTime; // TODO: [MikeO] ??


    private ArrayList<Event> events = new ArrayList<>();

    public void add(Event e) {
        events.add(e);
    }

    /**
     * Puts all events during teleop mode into a list, sorted by timestamp.
     * Useful for doing cycle analysis in StatsEngine.
     */
    public ArrayList<Event> getEvents() {
        Collections.sort(events);

        return events;
    }


    public TeleopScoutingObject() {

    }

    // TODO probably not going to be moved to stat engine
    // [MikeO] Yeah, I ended up doing something complicated in StatsEngine that includes this :'(
//    public void cycleTime() {
//
//        double fuelPickupCycleTime = 0;
//        for(FuelPickupEvent f : fuelPickups) {
//            fuelPickupCycleTime += f.endTime - f.timestamp;
//        }
//        // TODO: [MikeO] What if fuelPickups.size() == 0? divByZeroException?
//        fuelPickupCycleTime /= fuelPickups.size();
//
//        double fuelShotCycleTime = 0;
//        for(FuelShotEvent f : fuelShots) {
//            fuelShotCycleTime += f.endTime - f.timestamp;
//        }
//        fuelShotCycleTime /= fuelShots.size();
//
//        double gearPickupCycleTime = 0;
//        for(GearPickupEvent g : gearPickups) {
//            gearPickupCycleTime += g.endTime - g.endTime;
//        }
//        gearPickupCycleTime /= gearPickups.size();
//
//        double gearDeliveryCycleTime = 0;
//        for(GearDelivevryEvent g : gearDelivevries) {
//            gearDeliveryCycleTime += g.endTime - g.timestamp;
//        }
//        gearDeliveryCycleTime /= gearDelivevries.size();
//
//        // assuming that each cycle contains one pickup and one shot/delivery
//        gearCycleTime = gearPickupCycleTime + gearDeliveryCycleTime;
//        fuelCycleTime = fuelPickupCycleTime + fuelShotCycleTime;
//    }

}

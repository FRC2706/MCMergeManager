package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwall on 16/01/17.
 */

public class TeleopScoutingObject {

    public static double MATCH_TIME = 0; // TODO: get match time in milliseconds?

    public List<FuelPickup> fuelPickups;
    public List<FuelShot> fuelShots;
    public List<GearPickup> gearPickups;
    public List<GearDelivevry> gearDelivevries;

    public double fuelCycleTime;
    public double gearCycleTime;

    public TeleopScoutingObject() {
        fuelPickups = new ArrayList<FuelPickup>();
        fuelShots = new ArrayList<FuelShot>();
        gearPickups = new ArrayList<GearPickup>();
        gearDelivevries = new ArrayList<GearDelivevry>();
    }

    public TeleopScoutingObject(List<FuelPickup> fuelPickups, List<FuelShot> fuelShots, List<GearPickup> gearPickups,
                                List<GearDelivevry> gearDelivevries) {
        this.fuelPickups = fuelPickups;
        this.fuelShots = fuelShots;
        this.gearPickups = gearPickups;
        this.gearDelivevries = gearDelivevries;

        cycleTime();
    }

    // TODO probably not going to be moved to stat engine
    public void cycleTime() {

        double fuelPickupCycleTime = 0;
        for(FuelPickup f : fuelPickups) {
            fuelPickupCycleTime += f.endTime - f.startTime;
        }
        fuelPickupCycleTime /= fuelPickups.size();

        double fuelShotCycleTime = 0;
        for(FuelShot f : fuelShots) {
            fuelShotCycleTime += f.endTime - f.startTime;
        }
        fuelShotCycleTime /= fuelShots.size();

        double gearPickupCycleTime = 0;
        for(GearPickup g : gearPickups) {
            gearPickupCycleTime += g.endTime - g.endTime;
        }
        gearPickupCycleTime /= gearPickups.size();

        double gearDeliveryCycleTime = 0;
        for(GearDelivevry g : gearDelivevries) {
            gearDeliveryCycleTime += g.endTime - g.startTime;
        }
        gearDeliveryCycleTime /= gearDelivevries.size();

        // assuming that each cycle contains one pickup and one shot/delivery
        gearCycleTime = gearPickupCycleTime + gearDeliveryCycleTime;
        fuelCycleTime = fuelPickupCycleTime + fuelShotCycleTime;
    }
}

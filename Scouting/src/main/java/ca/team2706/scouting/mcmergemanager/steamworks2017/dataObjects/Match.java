package ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects;

import java.io.Serializable;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.*;

/**
 * toString() turns it into a string
 */
public class Match implements Serializable{

    public PreGameObject preGameObject;
    public PostGameObject postGameObject;
    public TeleopScoutingObject teleopScoutingObject;
    public AutoScoutingObject autoScoutingObject;

    public Match(PreGameObject preGameObject, PostGameObject postGameObject, TeleopScoutingObject teleopScoutingObject,
                 AutoScoutingObject autoScoutingObject) {
        this.preGameObject = preGameObject;
        this.autoScoutingObject = autoScoutingObject;
        this.postGameObject = postGameObject;
        this.teleopScoutingObject = teleopScoutingObject;
    }

    // TODO: this needs to be re-worked because MikeO changed around the data
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//        // pre game
//        sb.append( String.format("%d,%d,", preGameObject.matchNumber, preGameObject.teamNumber));
//
//        // autonomous mode
//        sb.append( String.format("%b,%b,%d,%b,%d,%d,", autoScoutingObject.start_gear, autoScoutingObject.start_fuel,
//                autoScoutingObject.gear_delivered, autoScoutingObject.boiler, autoScoutingObject.numScored,
//                autoScoutingObject.open_hopper));
//
//        // teleop mode
//
//        // fuel
//        //cycle time
//        sb.append(teleopScoutingObject.fuelCycleTime + ",");
//
//        // fuel-pickup cycles
//        sb.append("{");
//        for(int i = 0; i < teleopScoutingObject.fuelPickups.size(); i++) {
//            FuelPickupEvent fuelPickup = teleopScoutingObject.fuelPickups.get(i);
//
//            sb.append(String.format("{%.2f;%.2f;%d;%d}", fuelPickup.timestamp, fuelPickup.endTime,
//                    fuelPickup.pickupType, fuelPickup.amount));
//
//            if(i < teleopScoutingObject.fuelPickups.size())
//                sb.append(":");
//        }
//        sb.append("},");
//
//        // fuel-shot cycles
//        sb.append("{");
//        for(int i = 0; i < teleopScoutingObject.fuelShots.size(); i++) {
//            FuelShotEvent fuelShot = teleopScoutingObject.fuelShots.get(i);
//
//            sb.append(String.format("{%.2f;%.2f;%b;%d;%d;%d}", fuelShot.timestamp, fuelShot.endTime,
//                    fuelShot.boiler, fuelShot.accuracy, fuelShot.x, fuelShot.y));
//
//            if(i < teleopScoutingObject.fuelShots.size())
//                sb.append(":");
//        }
//        sb.append("},");
//
//
//
//        // gears
//        // cycle time
//        sb.append(teleopScoutingObject.gearCycleTime + ",");
//
//        // gear-pickup cycles
//        sb.append("{");
//        for(int i = 0; i < teleopScoutingObject.gearPickups.size(); i++) {
//            GearPickupEvent gearPickup = teleopScoutingObject.gearPickups.get(i);
//
//            sb.append(String.format("{%.2f;%.2f;%b;%b}", gearPickup.timestamp, gearPickup.endTime, gearPickup.pickupLocation, gearPickup.successful));
//
//            if(i < teleopScoutingObject.gearPickups.size())
//                sb.append(":");
//        }
//        sb.append("},");
//
//        // gear-delivery cycles
//        sb.append("{");
//        for(int i = 0; i < teleopScoutingObject.gearDelivevries.size(); i++) {
//            GearDelivevryEvent gearDelivevry = teleopScoutingObject.gearDelivevries.get(i);
//
//            sb.append(String.format("{%.2f;%.2f;%d;%d}", gearDelivevry.timestamp, gearDelivevry.endTime, gearDelivevry.deliveryStatus, gearDelivevry.lift));
//
//            if(i < teleopScoutingObject.gearDelivevries.size())
//                sb.append(":");
//        }
//        sb.append("},");
//
//
//        // post game stuff
//        sb.append(String.format("%b,%d,%d,", postGameObject.climb, postGameObject.climb_time, postGameObject.time_dead));
//
//        // since commas, semi-colons, braces, and <enter> are all special characters for the text file, let's rip those out just to be safe.
//        String cleanedNotes = postGameObject.notes.replaceAll(",","").replaceAll(";","").replaceAll("\\{","")
//                .replaceAll("\\}","").replaceAll("\n","");
//        sb.append(cleanedNotes+",");
//
//        // new line for next match
//        sb.append("\n");
//
//        return sb.toString();
//    }
//
    public Match(String str) {
//        preGameObject = new PreGameObject();
//        autoScoutingObject = new AutoScoutingObject();
//        teleopScoutingObject = new TeleopScoutingObject();
//        postGameObject = new PostGameObject();
//
//        String[] tokens = str.split(",");
//
//        // pre-game
//        preGameObject.matchNumber = Integer.valueOf(tokens[0]);
//        preGameObject.teamNumber = Integer.valueOf(tokens[1]);
//
//        // automode
//        autoScoutingObject.start_gear = Boolean.valueOf(tokens[2]);
//        autoScoutingObject.start_fuel = Boolean.valueOf(tokens[3]);
//        autoScoutingObject.gear_delivered = Integer.valueOf(tokens[4]);
//        autoScoutingObject.boiler = Boolean.valueOf(tokens[5]);
//        autoScoutingObject.numFuelScored = Integer.valueOf(tokens[6]);
//        autoScoutingObject.open_hopper = Integer.valueOf(tokens[7]);
//
//        // teleopmode
//
//        // fuelpickup
//        teleopScoutingObject.fuelCycleTime = Double.valueOf(tokens[8]);
//        if(!tokens[9].equals("")) {
//            String[] fuelPickups = tokens[9].split(":");
//            for(String s : fuelPickups) {
//                String[] fuelPickupTokens = s.split(";");
//                teleopScoutingObject.fuelPickups.add(new FuelPickupEvent(Double.valueOf(fuelPickupTokens[0]),
//                        Double.valueOf(fuelPickupTokens[1]), Integer.valueOf(fuelPickupTokens[2]),
//                        Integer.valueOf(fuelPickupTokens[3])));
//            }
//        }
//        //fuelshot
//        if(!tokens[10].equals("")) {
//            String[] fuelShots = tokens[10].split(":");
//            for(String s : fuelShots) {
//                String[] fuelPickupTokens = s.split(";");
//                teleopScoutingObject.fuelShots.add(new FuelShotEvent(Double.valueOf(fuelPickupTokens[0]),
//                        Double.valueOf(fuelPickupTokens[1]), Boolean.valueOf(fuelPickupTokens[2]),
//                        Integer.valueOf(fuelPickupTokens[3]), Integer.valueOf(fuelPickupTokens[4]),
//                        Integer.valueOf(fuelPickupTokens[5])));
//            }
//        }
//
//        // gears-pickups
//        teleopScoutingObject.gearCycleTime = Double.valueOf(tokens[11]);
//        if(!tokens[12].equals("")) {
//            String[] gearPickups = tokens[12].split(":");
//            for (String s : gearPickups) {
//                String[] gearPickupTokens = s.split(";");
//                teleopScoutingObject.gearPickups.add(new GearPickupEvent(Double.valueOf(gearPickupTokens[0]),
//                        Double.valueOf(gearPickupTokens[1]), Boolean.valueOf(gearPickupTokens[2]),
//                        Boolean.valueOf(gearPickupTokens[3])));
//            }
//        }
//        // gear delivery
//        if(!tokens[13].equals("")) {
//            String[] gearDelivery = tokens[13].split(":");
//            for (String s : gearDelivery) {
//                String[] gearDeliveryTokens = s.split(";");
//                teleopScoutingObject.gearDelivevries.add(new GearDelivevryEvent(Double.valueOf(gearDeliveryTokens[0]),
//                        Double.valueOf(gearDeliveryTokens[1]), Integer.valueOf(gearDeliveryTokens[2]),
//                        Integer.valueOf(gearDeliveryTokens[3])));
//            }
//        }
//
//        // post game
//        postGameObject.climb = Integer.valueOf(tokens[14]);
//        postGameObject.climb_time = Double.valueOf(tokens[15]);
//        postGameObject.time_dead = Integer.valueOf(tokens[16]);
//
//        postGameObject.notes = tokens[17];
    }

}

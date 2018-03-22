package ca.team2706.scouting.mcmergemanager.backend.dataObjects;

import java.util.ArrayList;

import ca.team2706.scouting.mcmergemanager.backend.WebServerUtils;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoCubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoLineCrossEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Auto.AutoMalfunctionEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.ClimbEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubeDroppedEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePickupEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubePlacementEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.Event;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BlueSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.BoostEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ForceEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.LevitateEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.RedSwitchEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.FieldWatcher.ScaleEvent;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.MatchData;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.PostGameObject;

/**
 * Created by Daniel Wall on 2018-02-26.
 */

public class PostMessageThread extends Thread {

    private boolean posted;

    private String matchKey;
    private String teamNumber;
    private Event event;


    public PostMessageThread(String matchKey, String teamNumber, Event event) {
        this.matchKey = matchKey;
        this.teamNumber = teamNumber;
        this.event = event;
    }

    @Override
    public void run() {
        String goal = "", success = "", startTime = Double.toString(event.timestamp), endTime = "", extra = "";

        if (event instanceof AutoCubePickupEvent) {
            goal = MatchData.AUTO_CUBE_PICKUP_ID;
            extra = ((AutoCubePickupEvent) event).pickupType.toString();
        } else if (event instanceof AutoCubePlacementEvent) {
            goal = MatchData.AUTO_CUBE_PLACE_ID;
            extra = ((AutoCubePlacementEvent) event).placementType.toString();
        } else if (event instanceof AutoLineCrossEvent) {
            goal = MatchData.AUTO_LINE_CROSS_ID;
            extra = Boolean.toString(((AutoLineCrossEvent) event).crossedAutoLine);
        } else if (event instanceof AutoMalfunctionEvent) {
            goal = MatchData.AUTO_MALFUNCTION_ID;
            extra = Boolean.toString(((AutoMalfunctionEvent) event).autoMalfunction);
        } else if (event instanceof ClimbEvent) {
            goal = MatchData.CLIMB_ID;
            extra = ((ClimbEvent) event).climbType.toString();
            endTime = Double.toString(((ClimbEvent) event).climb_time);
        } else if (event instanceof CubeDroppedEvent) {
            goal = MatchData.CUBE_DROPPED_ID;
            extra = ((CubeDroppedEvent) event).dropType.toString();
        } else if (event instanceof CubePickupEvent) {
            goal = MatchData.CUBE_PICKUP_ID;
            extra = ((CubePickupEvent) event).pickupType.toString();
        } else if (event instanceof CubePlacementEvent) {
            goal = MatchData.CUBE_PLACE_ID;
            extra = ((CubePlacementEvent) event).placementType.toString();
        } else if(event instanceof PostGameObject) {
            goal = MatchData.POST_GAME_ID;
            extra = Double.toString(((PostGameObject) event).time_dead);
            endTime = Double.toString(((PostGameObject) event).time_defending);
        } else if (event instanceof BlueSwitchEvent) {
            goal = MatchData.BLUE_SWITCH;
            extra = ((BlueSwitchEvent) event).getAllianceColour().toString();
        }else if (event instanceof RedSwitchEvent) {
            goal = MatchData.RED_SWITCH;
            extra = ((RedSwitchEvent) event).getAllianceColour().toString();
        }else if (event instanceof ScaleEvent) {
            goal = MatchData.SCALE;
            extra = ((ScaleEvent) event).getAllianceColour().toString();
        }else if (event instanceof BoostEvent) {
            goal = MatchData.BOOST;
        }else if (event instanceof ForceEvent) {
            goal = MatchData.FORCE;
        }else if (event instanceof LevitateEvent) {
            goal = MatchData.LEVITATE;
        }

        if(WebServerUtils.postMatchEvent(matchKey, teamNumber, goal, success, startTime, endTime, extra)) {
            posted = true;
        } else {
            posted = false;
        }
    }

    public boolean getPosted() {
        return posted;
    }
    public Event getEvent() { return event; }
}

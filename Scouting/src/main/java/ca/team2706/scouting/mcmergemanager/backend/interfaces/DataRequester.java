package ca.team2706.scouting.mcmergemanager.backend.interfaces;

import ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects.MatchSchedule;

/**
 * This interface is meant to be implemented by an activity that requests matchScoutingData and / or MatchResultsData from the FileUtils.
 *
 * Since syncing with Drive can take a few seconds, FileUtils will immediately call
 * the activity's updateData(matchResultsDataCSV, matchScoutingDataCSV) with whatever data is locally cached.
 * If FileUtils is able to connect to Drive then it will call it again after performing the sync.
 *
 * Created by Mike Ounsworth
 */
public interface DataRequester {

    void updateData(String[] matchResultsDataCSV, String[] matchScoutingDataCSV);

    void updateMatchSchedule(MatchSchedule matchSchedule);
}

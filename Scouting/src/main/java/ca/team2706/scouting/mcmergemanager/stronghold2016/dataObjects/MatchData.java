package ca.team2706.scouting.mcmergemanager.stronghold2016.dataObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class to encapsulate all the data collected during a match so that it can be easily
 * passed around, saved to file, loaded from file, etc.
 *
 * Created by mike on 03/02/16.
 */
public class MatchData implements Serializable {
    
    // Member Variables
    public ArrayList<Match> matches;

    /** Empty constructor **/
    public MatchData() {
        matches = new ArrayList<>();
    }
    
    public void addMatch(Match match) {
        matches.add(match);
    }



    public MatchData filterByTeam(int teamNo) {

        MatchData matchData = new MatchData();

        for(Match match : matches) {
            if (match.preGame.teamNumber == teamNo)
                matchData.addMatch(match);
        }
        return matchData;
    }




    /** An inner-class encapsulating the data from a single robot from a single match. **/
    public static class Match implements Serializable {

        public PreGameObject preGame;
        public AutoScoutingObject autoMode;
        public TeleopScoutingObject teleopMode;
        public PostGameObject postGame;


        /** Empty constructor **/
        public Match() {
            preGame = new PreGameObject();
            autoMode = new AutoScoutingObject();
            teleopMode = new TeleopScoutingObject();
            postGame = new PostGameObject();
        }


        /** All the things costructor **/
        public Match(PreGameObject preGame, AutoScoutingObject autoMode, TeleopScoutingObject teleopMode, PostGameObject postGame) {
            this.preGame = preGame;
            this.autoMode = autoMode;
            this.teleopMode = teleopMode;
            this.postGame = postGame;
        }


        // String serializers to read / write it to file

        /**
         * toString() turns it into a string
         */
        @Override
        public String toString() {
            /** build the string **/
            StringBuilder sb = new StringBuilder();

            /** Pre-game **/
            sb.append( String.format("%d,%d,", preGame.matchNumber, preGame.teamNumber) );



            /** Auto Mode **/

            sb.append( String.format("%b,%b,", autoMode.isSpyBot, autoMode.reachedDefense) );

            // a list of defensesBreached
            // {defenseBreached<int>;defenseBreached<int>;...}
            sb.append("{");
            for(int i=0; i<autoMode.defensesBreached.size(); i++) {
                sb.append(autoMode.defensesBreached.get(i));

                // the last one doesn't get a semi-colon
                if (i < autoMode.defensesBreached.size() - 1)
                    sb.append(";");
            }
            sb.append("},");

            // a list of BallShots
            // {{ballShot_X<int>;ballShot_Y<int>;ballShot_time<.3double>;ballshot_which<int>}:...}
            sb.append("{");
            for(int i=0; i<autoMode.ballsShot.size(); i++) {
                BallShot ballShot = autoMode.ballsShot.get(i);

                sb.append(String.format("{%d;%d;%.2f;%d}",ballShot.x,ballShot.y,ballShot.shootTime,ballShot.whichGoal));

                if (i < autoMode.ballsShot.size() - 1)
                    sb.append(":");
            }
            sb.append("},");



            /** Teleop Mode **/

            // a list of defensesBreached
            // {defenseBreached<int>;...}
            sb.append("{");
            for(int i=0; i<teleopMode.defensesBreached.size(); i++) {
                sb.append(teleopMode.defensesBreached.get(i));

                // the last one doesn't get a colon
                if (i < teleopMode.defensesBreached.size() - 1 )
                    sb.append(";");
            }
            sb.append("},");

            // a list of BallShots
            // {{ballShot_X<int>;ballShot_Y<int>;ballShot_time<.3double>;ballshot_which<int>}:...}
            sb.append("{");
            for(int i=0; i<teleopMode.ballsShot.size(); i++) {
                BallShot ballShot = teleopMode.ballsShot.get(i);

                sb.append(String.format("{%d;%d;%.2f;%d}",ballShot.x,ballShot.y,ballShot.shootTime,ballShot.whichGoal));

                // the last one doesn't get a colon
                if (i < teleopMode.ballsShot.size() - 1)
                    sb.append(":");
            }
            sb.append("},");

            sb.append( String.format("%.2f,",teleopMode.timeDefending));

            // Ball Pickup
            // {{%d;%,2f}:...}
            sb.append("{");
            for(int i=0; i<teleopMode.ballsPickedUp.size(); i++) {
                BallPickup pickup = teleopMode.ballsPickedUp.get(i);

                sb.append( String.format("{%d;%.2f}", pickup.selection, pickup.time));

                // the last one doesn't get a colon
                if (i < teleopMode.ballsPickedUp.size() - 1)
                    sb.append(":");
            }
            sb.append("},");


            // Scaling Times
            // {{%.2f;%d}:...}
            sb.append("{");
            for(int i=0; i<postGame.scalingTower.size(); i++) {
                ScalingTime scale = postGame.scalingTower.get(i);

                sb.append( String.format("{%.2f;%d}", scale.time, scale.completed));

                // the last one doesn't get a colon
                if (i < postGame.scalingTower.size() - 1)
                    sb.append(":");
            }
            sb.append("},");


            /** Post-Game **/

            // since commas, semi-colons, braces, and <enter> are all special characters for the text file, let's rip those out just to be safe.
            String cleanedNotes = postGame.notes .replaceAll(",","")
                    .replaceAll(";","")
                    .replaceAll("\\{","")
                    .replaceAll("\\}","")
                    .replaceAll("\n","");
            sb.append(cleanedNotes+",");

            sb.append( String.format("%b,",postGame.challenged) );
            sb.append( String.format("%d",postGame.timeDead) );

            sb.append("\n");

            return sb.toString();
        }

        /** De-serializing constructor **/
        public Match(String matchStr) {

            preGame = new PreGameObject();
            autoMode = new AutoScoutingObject();
            teleopMode = new TeleopScoutingObject();
            postGame = new PostGameObject();


            String[] tokens = matchStr.split(",");


            /** Pre-game **/

            preGame.matchNumber = Integer.valueOf(tokens[0]);
            preGame.teamNumber = Integer.valueOf(tokens[1]);


            /** Auto Mode **/

            autoMode.isSpyBot = Boolean.valueOf(tokens[2]);
            autoMode.reachedDefense = Boolean.valueOf(tokens[3]);

            // autoDefensesBreached
            if ( !tokens[4].equals("") ) {
                String[] autoDefensesBreachedStrs = tokens[4].split(";");
                for (String breach : autoDefensesBreachedStrs)
                    autoMode.defensesBreached.add(Integer.valueOf(breach));
            }

            // autoBallShots
            if ( !tokens[5].equals("") ) {
                String[] autoShots = tokens[5].split(":");
                for (String shot : autoShots) {
                    String[] shotTokens = shot.split(";");
                    BallShot ballShot = new BallShot();
                    ballShot.x = Integer.valueOf(shotTokens[0]);
                    ballShot.y = Integer.valueOf(shotTokens[1]);
                    ballShot.shootTime = Double.valueOf(shotTokens[2]);
                    ballShot.whichGoal = Integer.valueOf(shotTokens[3]);

                    autoMode.ballsShot.add(ballShot);
                }
            }

            /** Teleop Mode **/

            // teleopDefensesBreached
            if ( !tokens[6].equals("") ) {
                String[] teleopDefensesBreachedStrs = tokens[6].split(";");
                for (String breach : teleopDefensesBreachedStrs)
                    teleopMode.defensesBreached.add(Integer.valueOf(breach));
            }

            // teleopBallShots
            if ( !tokens[7].equals("") ) {
                String[] teleopShots = tokens[7].split(":");
                for (String shot : teleopShots) {
                    String[] shotTokens = shot.split(";");
                    BallShot ballShot = new BallShot();
                    ballShot.x = Integer.valueOf(shotTokens[0]);
                    ballShot.y = Integer.valueOf(shotTokens[1]);
                    ballShot.shootTime = Double.valueOf(shotTokens[2]);
                    ballShot.whichGoal = Integer.valueOf(shotTokens[3]);

                    teleopMode.ballsShot.add(ballShot);
                }
            }

            teleopMode.timeDefending = Double.valueOf(tokens[8]);


            // Ball Pickup
            if ( !tokens[9].equals("") ) {
                String[] ballPickups = tokens[9].split(":");
                for (String pickupStr : ballPickups) {
                    String[] pickupTokens = pickupStr.split(";");
                    BallPickup ballPickup = new BallPickup();
                    ballPickup.selection = Integer.valueOf(pickupTokens[0]);
                    ballPickup.time = Double.valueOf(pickupTokens[1]);

                    teleopMode.ballsPickedUp.add(ballPickup);
                }
            }

            // Scaling Times
            // {{%.2f;%d}:...}
            if ( !tokens[10].equals("") ) {
                String[] scaleStrs = tokens[10].split(":");
                for (String scaleStr : scaleStrs) {
                    String[] scaleTokens = scaleStr.split(";");
                    ScalingTime scalingTime = new ScalingTime();

                    scalingTime.time = Double.valueOf(scaleTokens[0]);
                    scalingTime.completed = Integer.valueOf(scaleTokens[1]);

                    postGame.scalingTower.add(scalingTime);
                }
            }


            /** Post-Game **/

            postGame.notes = tokens[11];
            postGame.challenged = Boolean.valueOf(tokens[12]);
            postGame.timeDead = Integer.valueOf(tokens[13]);
        }

    }

}

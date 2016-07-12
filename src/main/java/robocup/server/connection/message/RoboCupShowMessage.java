/*
 * Copyright 2016 Tom Warnke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package robocup.server.connection.message;

import java.util.HashMap;
import java.util.Map;

/**
 * A show message, i.e. a message containing the positions of the ball and all players as well as other information
 * about the current state of the game.
 *
 * @author Tom Warnke
 */
public class RoboCupShowMessage extends RoboCupMessage {

    // TODO replace with enum for better readability
    private final int playmode;

    private final String leftTeam, rightTeam;

    private final int leftScore, rightScore;

    private final Map<String, Location> locations = new HashMap<>(23);

    public RoboCupShowMessage(int turn, int playmode, String leftTeam,
                              String rightTeam, int leftScore, int rightScore) {
        super(turn);
        this.playmode = playmode;
        this.leftTeam = leftTeam;
        this.rightTeam = rightTeam;
        this.leftScore = leftScore;
        this.rightScore = rightScore;
    }

    public int getPlaymode() {
        return playmode;
    }

    public String getLeftTeam() {
        return leftTeam;
    }

    public String getRightTeam() {
        return rightTeam;
    }

    public int getLeftScore() {
        return leftScore;
    }

    public int getRightScore() {
        return rightScore;
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public Location get(String key) {
        return locations.get(key);
    }

    public Location put(String key, Location value) {
        return locations.put(key, value);
    }

    @Override
    public String toString() {
        return super.toString() + " [playmode=" + playmode + ", leftTeam="
                + leftTeam + ", rightTeam=" + rightTeam + ", leftScore=" + leftScore
                + ", rightScore=" + rightScore + ", locations=" + locations + "]";
    }

}

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

/**
 * This class and its subclasses represent messages sent from the RoboCup soccer server. For examples of the string
 * format of these messages, see the unit tests in ParserTest.
 *
 * @author Tom Warnke
 */
public abstract class RoboCupMessage {

    private final int turn;

    /**
     * @param turn
     */
    public RoboCupMessage(int turn) {
        super();
        this.turn = turn;
    }

    /**
     * Get the turn.
     *
     * @return the turn
     */
    public int getTurn() {
        return turn;
    }

    @Override
    public String toString() {
        return "RoboCupMessage [turn=" + turn + "]";
    }

}

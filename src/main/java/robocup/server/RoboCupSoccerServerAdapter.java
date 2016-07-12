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

package robocup.server;

import org.apache.log4j.Logger;
import robocup.server.connection.ISoccerServerConnection;
import robocup.server.connection.SoccerServerConnectionManager;

import java.io.IOException;

/**
 * This is the central class for communication with the RoboCup soccer server. The soccer server runs as external
 * binary. It can be downloaded at http://sourceforge.net/projects/sserver/. As it is available for linux only, this
 * project also relies on Linux, i.e. it is not platform agnostic.
 * <p>
 * Typical steps in the usage of the soccer server are initialization (most importantly choosing the contesting teams),
 * starting the game and observing it. Note that the game has to be restarted at half time (and possibly in extra time,
 * too), unless the soccer server is configured to run in auto mode.
 * <p>
 * To communicate with the soccer server, a UDP connection is used. The messages from the server are parsed and the
 * contained information is extracted.
 *
 * @author Tom Warnke
 */
public class RoboCupSoccerServerAdapter {

    private static final Logger log =
            Logger.getLogger(RoboCupSoccerServerAdapter.class);

    private RoboCupSoccerServerAdapter() {
        // private constructor to prevent instantiation
    }

    /**
     * Sets up a local soccer server, establishes an UDP connection to it and returns the connection.
     * <p>
     * Note that, depending on the configuration of the soccer server instance ("auto_mode"), it may automatically kick
     * off the game after some waiting time. Observers should be connected to the server as fast as possible to ensure
     * that they do not miss the first turns.
     *
     * @param matchDirectory the directory the log files shall be saved in.
     * @param autoMode       if set to true the game will be started by the server
     * @return a {@link ISoccerServerConnection} to the started server
     * @throws IOException
     */
    public static ISoccerServerConnection setUpLocalServer(String matchDirectory,
                                                           boolean autoMode)
            throws IOException {

        // set up server process
        new SoccerServerProcess(matchDirectory, autoMode);

        // wait until the server process is ready
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // connect to server process via UDP
        ISoccerServerConnection soccerServerConnection =
                SoccerServerConnectionManager.getConnectionTo("localhost", 6000);

        return soccerServerConnection;
    }
}

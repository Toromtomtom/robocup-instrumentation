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

package robocup.server.connection;

import robocup.server.connection.message.IRoboCupMessageHandler;

/**
 * This interface represents the connection to a soccer server instance.
 *
 * @author Tom Warnke
 */
public interface ISoccerServerConnection {

    void registerMessageHandler(IRoboCupMessageHandler messageHandler);

    /**
     * Observes the soccer server, recording the match.
     *
     * @param autoMode if set to false, the connection will send kickoff messages to the server to start the game and
     *                 the second half
     */
    void observe(boolean autoMode);

}

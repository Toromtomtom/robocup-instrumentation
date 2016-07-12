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

import org.apache.log4j.Logger;
import robocup.server.connection.message.RoboCupMessage;
import robocup.server.connection.message.RoboCupMsgMessage;
import robocup.server.connection.message.RoboCupShowMessage;
import robocup.server.connection.message.parser.RoboCupParser;
import robocup.server.connection.message.IRoboCupMessageHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class controls and observes a running soccer server instance.
 * <p>
 * After establishing an UDP connection to the server it observes the server.
 *
 * @author Tom Warnke
 */
class SoccerServerConnection implements ISoccerServerConnection {

    private static final Logger log =
            Logger.getLogger(SoccerServerConnection.class);

    private final UDPConnection udpConnection;

    private final Set<IRoboCupMessageHandler> messageHandlers = new HashSet<>();

    /**
     * Register as a monitor at the soccer server. First a init message is send to the given port (usually port 6000).
     * The answer from the server contains a new port to talk and listen to. We have to keep our port, so we need a
     * mutable UDPConnection object.
     *
     * @param hostname
     * @param port
     * @throws IOException
     */
    SoccerServerConnection(String hostname, int port) throws IOException {

        UDPConnection tempConnection = new UDPConnection(hostname, port);
        tempConnection.send("(dispinit version 4)");
        udpConnection = tempConnection.receiveNewConnection();
    }

    @Override
    public void registerMessageHandler(IRoboCupMessageHandler messageHandler) {
        messageHandlers.add(messageHandler);
    }


    @Override
    public void observe(boolean autoMode) {

        // start the game (if necessary) and observe it
        long beforeStart = System.currentTimeMillis();

        boolean gameEnded = false;

        try {

            if (!autoMode) {
                // game does not start automatically -> start game
                kickOff();
            }

            // message receive loop
            do {
                // receive and handle message
                String message = udpConnection.receiveMessage();
                int turn = handleMessage(message);

                if (turn >= 6000) {
                    gameEnded = true;
                } else if (!autoMode && turn == 3000) {
                    // start second half
                    kickOff();
                }

                // until game has ended
            } while (!gameEnded);

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Observation finished after " + (System.currentTimeMillis()
                - beforeStart) + " s.");

    }

    /**
     * Kick off the game, i.e. start it (or continue after half time if not in auto mode)
     *
     * @throws IOException
     */
    private void kickOff() throws IOException {
        udpConnection.send("(dispstart)");
    }

    /**
     * Handle the message string from the soccer server. Basically that means parsing. If the message contains info
     * about the positions of the ball and players (i.e. is a {@link RoboCupShowMessage}), pass that info to the
     * messageHandler. If the message has another type, log it. The method returns the turn number of the observed turn,
     * or -1 if no known message could be received.
     *
     * @param messageString
     */
    private int handleMessage(String messageString) {
        RoboCupMessage message = RoboCupParser.parseMessage(messageString);
        if (message != null) {
            int turn = message.getTurn();
            if (message instanceof RoboCupShowMessage) {
                for (IRoboCupMessageHandler messageHandler : messageHandlers) {
                    messageHandler.handleMessage((RoboCupShowMessage) message);
                }
            } else if (message instanceof RoboCupMsgMessage) {
                log.info(
                        "Received message: " + ((RoboCupMsgMessage) message).getMessage());
            } else {
                log.warn("Received unknown message: " + messageString);
            }
            return turn;
        }
        return -1;
    }
}

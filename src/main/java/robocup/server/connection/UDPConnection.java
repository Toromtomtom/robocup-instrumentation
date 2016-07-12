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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class represents a UDP connection. Must be mutable because due to the soccer server monitor protocol we have to
 * adapt the target targetPort after registering at targetPort 6000 (see {@link
 * SoccerServerConnection#SoccerServerConnection(String,
 * int)} .
 *
 * @author Tom Warnke
 */
class UDPConnection {

    private static final Logger log = Logger.getLogger(UDPConnection.class);

    private final static int replySize = 4096;

    private final DatagramSocket socket;

    private InetAddress targetHostAddress;

    private int targetPort;

    /**
     * Create a new connection to the specified address.
     *
     * @param hostname   the host to connect to
     * @param targetPort the target targetPort
     * @throws IOException if the connection can not be established
     */
    public UDPConnection(String hostname, int targetPort) throws IOException {
        this(InetAddress.getByName(hostname), targetPort);
    }

    /**
     * @param address
     * @param targetPort
     * @throws IOException
     */
    private UDPConnection(InetAddress address, int targetPort) throws
            IOException {
        this.targetHostAddress = address;
        this.targetPort = targetPort;

        // create socket on any local targetPort
        socket = new DatagramSocket();
        // set timeout to 10 seconds
        socket.setSoTimeout(10000);

        log.info("Got Socket on local targetPort " + socket.getLocalPort() +
                " to " +
                this.targetHostAddress + ":" + this.targetPort);
    }

    /**
     * Send a message to the server.
     *
     * @param message
     * @throws IOException if the message could not be sent
     */
    public void send(String message) throws IOException {
        byte[] bytes = message.getBytes();
        DatagramPacket packet =
                new DatagramPacket(bytes, bytes.length, targetHostAddress,
                        targetPort);
        socket.send(packet);
        log.info("Sent: " + message);
    }

    /**
     * Receive a message from the server.
     *
     * @return the message string
     * @throws IOException if no message could be received before timeout
     */
    public String receiveMessage() throws IOException {
        DatagramPacket packet = receivePacket();
        return new String(packet.getData(), 0, packet.getLength());
    }

    /**
     * Receives a message from the server, propagating a new connection target, and returns the changed connection to
     * the new target.
     * <p>
     * This is due to the soccer server protocol: first we choose a local port we can receive messages at. Then we send
     * a message from our local port to port 6000 and register as a monitor. We receive an answer on our local port that
     * comes from a third targetPort. This new targetPort is the one we use to communicate with the server from now on
     * (so we overwrite the targetPort 6000). Note that we have to keep our local port the entire time so that the
     * server can identify us. Because of this we have to overwrite the targetPort field in this class during the
     * connection procedure instead of creating a new instance (that would have a different local port).
     *
     * @return the functional monitor connection
     * @throws IOException
     */
    public UDPConnection receiveNewConnection() throws IOException {
        DatagramPacket packet = receivePacket();
        this.targetHostAddress = packet.getAddress();
        this.targetPort = packet.getPort();
        log.info("Changed Socket on local port " + socket.getLocalPort() + " " +
                "to " +
                this.targetHostAddress + ":" + this.targetPort);
        return this;
    }

    /**
     * @return the received packet
     * @throws IOException if no packet could be received before timeout
     */
    private DatagramPacket receivePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[replySize], replySize);
        socket.receive(packet);
        return packet;
    }

}

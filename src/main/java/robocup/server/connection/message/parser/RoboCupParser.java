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

package robocup.server.connection.message.parser;

import org.antlr.v4.runtime.*;
import org.apache.log4j.Logger;
import robocup.server.connection.message.Location;
import robocup.server.connection.message.RoboCupMessage;
import robocup.server.connection.message.RoboCupMsgMessage;
import robocup.server.connection.message.RoboCupShowMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Encapsulates the parser.
 *
 * @author Tom Warnke
 */
public class RoboCupParser {

    private static final Logger log = Logger.getLogger(RoboCupMessage.class);

    private RoboCupParser() {
        // no instantiation
    }

    /**
     * Parses the given string as a RoboCup message. Caution, may return null if the message type is not supported!
     *
     * @param messageString
     * @return
     */
    public static RoboCupMessage parseMessage(String messageString) {

        //    log.info("Parsing: " + messageString);

        MessageLexer l = new MessageLexer(new ANTLRInputStream(messageString));
        MessageParser p = new MessageParser(new CommonTokenStream(l));

        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line, int
                                            charPositionInLine, String msg,
                                    RecognitionException e) {
                throw new IllegalStateException(
                        "failed to parse at line " + line + " due to " + msg,
                        e);
            }
        });

        final AtomicReference<RoboCupMessage> result = new AtomicReference<>();
        final AtomicReference<String> infoString = new AtomicReference<>();
        final Map<String, Location> locations = new HashMap<>();

        p.addParseListener(new MessageBaseListener() {

            @Override
            public void exitShow(MessageParser.ShowContext ctx) {

                int turn = Integer.parseInt(ctx.stepNumber.getText());
                int playMode = Integer.parseInt(ctx.playMode.getText());
                String leftTeam = ctx.leftTeam.getText();
                String rightTeam = ctx.rightTeam.getText();
                int leftScore = Integer.parseInt(ctx.leftScore.getText());
                int rightScore = Integer.parseInt(ctx.rightScore.getText());

                RoboCupShowMessage message =
                        new RoboCupShowMessage(turn, playMode, leftTeam,
                                rightTeam,
                                leftScore, rightScore);
                locations.entrySet()
                        .forEach(loc -> message.put(loc.getKey(), loc
                                .getValue()));

                result.set(message);
            }

            @Override
            public void exitBall(MessageParser.BallContext ctx) {
                float x = Float.parseFloat(ctx.x.getText());
                float y = Float.parseFloat(ctx.y.getText());
                locations.put("ball", new Location(x, y));
            }

            @Override
            public void exitPlayer(MessageParser.PlayerContext ctx) {
                float x = Float.parseFloat(ctx.x.getText());
                float y = Float.parseFloat(ctx.y.getText());
                String id = ctx.side.getText() + ctx.number.getText();
                locations.put(id, new Location(x, y));

                boolean touchesBall;
                // strip "0x" and convert to bit string
                int number = Integer.parseInt(ctx.flags.getText().substring
                        (2), 16);
                String binary = Integer.toBinaryString(number);
                // if the bit before last is set, the player touches the ball
                if (binary.length() > 1 && binary.charAt(binary.length() - 2)
                        == '1') {
                    touchesBall = true;
                }
                touchesBall = false;
            }

            @Override
            public void exitInfo(MessageParser.InfoContext ctx) {
                String info = ctx.infoText.getText();
                infoString.set(infoString.get() + " " + info);
            }

            @Override
            public void exitMsg(MessageParser.MsgContext ctx) {
                int turn = Integer.parseInt(ctx.stepNumber.getText());
                result.set(new RoboCupMsgMessage(turn, infoString.get()));
            }
        });

        try {
            p.oneLine();
        } catch (RecognitionException e) {
            log.error("Exception thrown during parsing", e);
            return null;
        }

        return result.get();
    }
}
